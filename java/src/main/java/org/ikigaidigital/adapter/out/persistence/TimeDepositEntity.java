package org.ikigaidigital.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * JPA mapping of the {@code time_deposits} table.
 * <p>
 * A separate persistence model on purpose: the shared domain class {@link org.ikigaidigital.TimeDeposit} must not
 * get breaking changes (JPA would need annotations and a no-arg constructor on it), and this keeps Hibernate out of
 * the domain. MapStruct converts between the two ({@link TimeDepositPersistenceMapper}).
 */
@Entity
@Table(name = "time_deposits")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // for JPA
class TimeDepositEntity {

    @Id
    private Integer id;

    @Column(name = "plan_type", nullable = false, length = 50)
    private String planType;

    @Column(nullable = false)
    private Integer days;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    void changeBalance(BigDecimal newBalance) {
        this.balance = newBalance;
    }
}
