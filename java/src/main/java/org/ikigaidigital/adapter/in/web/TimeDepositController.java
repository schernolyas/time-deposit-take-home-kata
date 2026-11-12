package org.ikigaidigital.adapter.in.web;

import lombok.RequiredArgsConstructor;
import org.ikigaidigital.adapter.in.web.api.TimeDepositsApi;
import org.ikigaidigital.adapter.in.web.api.model.TimeDepositDto;
import org.ikigaidigital.application.port.in.GetAllTimeDepositsUseCase;
import org.ikigaidigital.application.port.in.UpdateAllBalancesUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** REST adapter: implements the interface generated from the OpenAPI contract and delegates to use cases. */
@RestController
@RequiredArgsConstructor
class TimeDepositController implements TimeDepositsApi {

    private final GetAllTimeDepositsUseCase getAllTimeDeposits;
    private final UpdateAllBalancesUseCase updateAllBalances;
    private final TimeDepositDtoMapper mapper;

    @Override
    public ResponseEntity<List<TimeDepositDto>> getAllTimeDeposits() {
        return ResponseEntity.ok(mapper.toDtos(getAllTimeDeposits.getAllTimeDeposits()));
    }

    @Override
    public ResponseEntity<Void> updateAllBalances() {
        updateAllBalances.updateAllBalances();
        return ResponseEntity.noContent().build();
    }
}
