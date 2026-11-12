package org.ikigaidigital.adapter.in.web;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.application.BalanceUpdateInProgressException;
import org.ikigaidigital.application.TimeDepositWithWithdrawals;
import org.ikigaidigital.application.port.in.GetAllTimeDepositsUseCase;
import org.ikigaidigital.application.port.in.UpdateAllBalancesUseCase;
import org.ikigaidigital.domain.Withdrawal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TimeDepositController.class)
@Import(TimeDepositDtoMapperImpl.class) // MapStruct-generated bean; @WebMvcTest does not scan @Component classes
class TimeDepositControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetAllTimeDepositsUseCase getAllTimeDeposits;

    @MockitoBean
    private UpdateAllBalancesUseCase updateAllBalances;

    @Test
    void getAllTimeDeposits_returnsContractSchema() throws Exception {
        given(getAllTimeDeposits.getAllTimeDeposits()).willReturn(List.of(
                new TimeDepositWithWithdrawals(new TimeDeposit(1, "premium", 1_239_711.03, 46),
                        List.of(new Withdrawal(7, 1, new BigDecimal("100.00"), LocalDate.of(2026, 10, 15)))),
                new TimeDepositWithWithdrawals(new TimeDeposit(2, "basic", 10.0, 5), List.of())));

        mockMvc.perform(get("/time-deposits"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [
                          {"id": 1, "planType": "premium", "balance": 1239711.03, "days": 46,
                           "withdrawals": [{"id": 7, "amount": 100.00, "date": "2026-10-15"}]},
                          {"id": 2, "planType": "basic", "balance": 10.00, "days": 5, "withdrawals": []}
                        ]
                        """, true));
    }

    @Test
    void updateAllBalances_delegatesToUseCaseAndReturnsNoContent() throws Exception {
        mockMvc.perform(post("/time-deposits/balance-updates"))
                .andExpect(status().isNoContent());

        verify(updateAllBalances).updateAllBalances();
    }

    @Test
    void updateAllBalances_whileAnotherUpdateRuns_returnsConflictProblem() throws Exception {
        willThrow(new BalanceUpdateInProgressException()).given(updateAllBalances).updateAllBalances();

        mockMvc.perform(post("/time-deposits/balance-updates"))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Balance update already in progress"));
    }
}
