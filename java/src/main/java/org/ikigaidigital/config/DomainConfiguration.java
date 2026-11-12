package org.ikigaidigital.config;

import org.ikigaidigital.TimeDepositCalculator;
import org.ikigaidigital.domain.interest.InterestPolicies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Registers the framework-free domain (shared legacy classes and interest policies) as Spring beans. */
@Configuration(proxyBeanMethods = false)
class DomainConfiguration {

    @Bean
    TimeDepositCalculator timeDepositCalculator() {
        return new TimeDepositCalculator(InterestPolicies.defaults());
    }
}
