package org.ikigaidigital;

import org.ikigaidigital.application.service.ExclusiveUpdateAllBalances;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end through both endpoints against a real PostgreSQL (Testcontainers) with the Flyway schema.
 * Sample data is excluded; each test seeds exactly what it needs.
 */
@SpringBootTest(properties = "spring.flyway.locations=classpath:db/migration")
@AutoConfigureMockMvc
@Testcontainers
class TimeDepositApiIT {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void seed() {
        // Release, never delete, the ShedLock row: the lock provider remembers that the row exists and from then on
        // only UPDATEs it. ShedLock (usingDbTime) works in UTC, so timestamps are written in UTC as well.
        jdbcTemplate.update("UPDATE shedlock SET lock_until = timezone('utc', now()) - interval '1 minute'");
        jdbcTemplate.update("DELETE FROM withdrawals");
        jdbcTemplate.update("DELETE FROM time_deposits");
        jdbcTemplate.update("""
                INSERT INTO time_deposits (id, plan_type, days, balance) VALUES
                  (1, 'basic',   31,  1234567.00),
                  (2, 'student', 366, 1234567.00),
                  (3, 'premium', 46,  1234567.00),
                  (4, 'student', 100, 6.00)
                """);
        jdbcTemplate.update("""
                INSERT INTO withdrawals (id, time_deposit_id, amount, date) VALUES
                  (1, 3, 100.00, '2026-10-15'),
                  (2, 3, 50.50,  '2026-09-01')
                """);
    }

    @Test
    void getAllTimeDeposits_returnsDepositsWithWithdrawals() throws Exception {
        mockMvc.perform(get("/time-deposits"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                          {"id": 1, "planType": "basic",   "balance": 1234567.00, "days": 31,  "withdrawals": []},
                          {"id": 2, "planType": "student", "balance": 1234567.00, "days": 366, "withdrawals": []},
                          {"id": 3, "planType": "premium", "balance": 1234567.00, "days": 46,
                           "withdrawals": [
                             {"id": 2, "amount": 50.50,  "date": "2026-09-01"},
                             {"id": 1, "amount": 100.00, "date": "2026-10-15"}
                           ]},
                          {"id": 4, "planType": "student", "balance": 6.00, "days": 100, "withdrawals": []}
                        ]
                        """, true));
    }

    @Test
    void updateAllBalances_persistsOneMonthOfInterestForEveryDeposit() throws Exception {
        mockMvc.perform(post("/time-deposits/balance-updates"))
                .andExpect(status().isNoContent());

        assertThat(balanceOf(1)).isEqualByComparingTo("1235595.81");
        assertThat(balanceOf(2)).isEqualByComparingTo("1234567.00");
        assertThat(balanceOf(3)).isEqualByComparingTo("1239711.03");
        // 6.00 * 3% / 12 = 0.015 in decimal, but 0.01499... in double: the legacy rounding gives 0.01.
        assertThat(balanceOf(4)).isEqualByComparingTo("6.01");
    }

    @Test
    void updateAllBalances_isVisibleThroughTheGetEndpoint() throws Exception {
        mockMvc.perform(post("/time-deposits/balance-updates")).andExpect(status().isNoContent());

        mockMvc.perform(get("/time-deposits"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [{"id": 1, "balance": 1235595.81}, {"id": 2, "balance": 1234567.00},
                         {"id": 3, "balance": 1239711.03}, {"id": 4, "balance": 6.01}]
                        """));
    }

    @Test
    void updateAllBalances_whileAnotherInstanceHoldsTheLock_returnsConflictAndChangesNothing() throws Exception {
        // Another pod is in the middle of a balance update: its ShedLock row is still valid.
        jdbcTemplate.update("""
                INSERT INTO shedlock (name, lock_until, locked_at, locked_by)
                VALUES (?, timezone('utc', now()) + interval '5 minutes', timezone('utc', now()), 'other-pod')
                ON CONFLICT (name) DO UPDATE
                    SET lock_until = excluded.lock_until, locked_at = excluded.locked_at, locked_by = excluded.locked_by
                """, ExclusiveUpdateAllBalances.LOCK_NAME);

        mockMvc.perform(post("/time-deposits/balance-updates"))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
        assertThat(balanceOf(1)).isEqualByComparingTo("1234567.00");

        // The other pod finishes and releases the lock.
        jdbcTemplate.update("UPDATE shedlock SET lock_until = timezone('utc', now()) - interval '1 second' WHERE name = ?",
                ExclusiveUpdateAllBalances.LOCK_NAME);

        mockMvc.perform(post("/time-deposits/balance-updates")).andExpect(status().isNoContent());
        assertThat(balanceOf(1)).isEqualByComparingTo("1235595.81");
    }

    private BigDecimal balanceOf(int id) {
        return jdbcTemplate.queryForObject("SELECT balance FROM time_deposits WHERE id = ?", BigDecimal.class, id);
    }
}
