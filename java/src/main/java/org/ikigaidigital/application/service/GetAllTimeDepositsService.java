package org.ikigaidigital.application.service;

import lombok.RequiredArgsConstructor;
import org.ikigaidigital.TimeDeposit;
import org.ikigaidigital.application.TimeDepositWithWithdrawals;
import org.ikigaidigital.application.port.in.GetAllTimeDepositsUseCase;
import org.ikigaidigital.application.port.out.TimeDepositRepository;
import org.ikigaidigital.application.port.out.WithdrawalRepository;
import org.ikigaidigital.domain.Withdrawal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Joins deposits with their withdrawals. Two queries instead of N+1, in one read-only transaction so both are read
 * from the same consistent snapshot.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetAllTimeDepositsService implements GetAllTimeDepositsUseCase {

    private final TimeDepositRepository timeDepositRepository;
    private final WithdrawalRepository withdrawalRepository;

    @Override
    public List<TimeDepositWithWithdrawals> getAllTimeDeposits() {
        List<TimeDeposit> timeDeposits = timeDepositRepository.findAll();
        Map<Integer, List<Withdrawal>> withdrawalsByDeposit = withdrawalRepository.findAll().stream()
                .collect(Collectors.groupingBy(Withdrawal::timeDepositId));

        return timeDeposits.stream()
                .map(deposit -> new TimeDepositWithWithdrawals(
                        deposit, withdrawalsByDeposit.getOrDefault(deposit.getId(), List.of())))
                .toList();
    }
}
