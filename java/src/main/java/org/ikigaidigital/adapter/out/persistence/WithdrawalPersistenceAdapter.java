package org.ikigaidigital.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.ikigaidigital.application.port.out.WithdrawalRepository;
import org.ikigaidigital.domain.Withdrawal;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Outbound adapter: withdrawals stored with Spring Data JPA / Hibernate. */
@Repository
@RequiredArgsConstructor
class WithdrawalPersistenceAdapter implements WithdrawalRepository {

    private final WithdrawalJpaRepository repository;
    private final TimeDepositPersistenceMapper mapper;

    @Override
    public List<Withdrawal> findAll() {
        return mapper.toDomainWithdrawals(repository.findAll(Sort.by("date", "id")));
    }
}
