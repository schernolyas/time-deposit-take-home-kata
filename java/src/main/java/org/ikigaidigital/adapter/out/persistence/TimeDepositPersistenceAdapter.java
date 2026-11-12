package org.ikigaidigital.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.ikigaidigital.domain.Money;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Outbound adapter: time deposits stored with Spring Data JPA / Hibernate. */
@Repository
@RequiredArgsConstructor
class TimeDepositPersistenceAdapter implements TimeDepositRepository {

    private final TimeDepositJpaRepository repository;
    private final TimeDepositPersistenceMapper mapper;

    @Override
    public List<TimeDeposit> findAll() {
        return mapper.toDomainDeposits(repository.findAll(Sort.by("id")));
    }

    /**
     * Loads the managed entities in one query and changes their balance; Hibernate's dirty checking flushes the
     * UPDATEs (JDBC-batched, see application.yml) when the surrounding use case transaction commits.
     */
    @Override
    public void updateBalances(List<TimeDeposit> timeDeposits) {
        Map<Integer, TimeDepositEntity> entitiesById = repository
                .findAllById(timeDeposits.stream().map(TimeDeposit::getId).toList())
                .stream()
                .collect(Collectors.toMap(TimeDepositEntity::getId, Function.identity()));

        timeDeposits.forEach(deposit -> entitiesById.get(deposit.getId())
                .changeBalance(Money.toCents(deposit.getBalance())));
    }
}
