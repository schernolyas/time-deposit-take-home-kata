package org.ikigaidigital.adapter.in.web;

import org.ikigaidigital.application.BalanceUpdateInProgressException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Maps application exceptions to RFC 9457 problem responses declared in the OpenAPI contract. */
@RestControllerAdvice
class TimeDepositExceptionHandler {

    @ExceptionHandler(BalanceUpdateInProgressException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    ProblemDetail balanceUpdateInProgress(BalanceUpdateInProgressException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        problem.setTitle("Balance update already in progress");
        return problem;
    }
}
