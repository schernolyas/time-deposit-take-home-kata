package org.ikigaidigital.application.service;

import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.application.port.out.TimeDepositRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Test double for the outbound port: stores copies, so only {@link #updateBalances} persists changes. */
class InMemoryTimeDepositRepository implements TimeDepositRepository {

    private final Map<Integer, TimeDeposit> store = new LinkedHashMap<>();

    InMemoryTimeDepositRepository(TimeDeposit... deposits) {
        for (TimeDeposit deposit : deposits) {
            store.put(deposit.getId(), copy(deposit));
        }
    }

    @Override
    public List<TimeDeposit> findAll() {
        List<TimeDeposit> result = new ArrayList<>();
        store.values().forEach(deposit -> result.add(copy(deposit)));
        return result;
    }

    @Override
    public void updateBalances(List<TimeDeposit> timeDeposits) {
        timeDeposits.forEach(deposit -> store.get(deposit.getId()).setBalance(deposit.getBalance()));
    }

    double balanceOf(int id) {
        return store.get(id).getBalance();
    }

    private static TimeDeposit copy(TimeDeposit d) {
        return new TimeDeposit(d.getId(), d.getPlanType(), d.getBalance(), d.getDays());
    }
}
