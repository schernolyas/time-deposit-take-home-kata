package org.ikigaidigital.architecture;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/** Enforces the hexagonal dependency rule: everything points inwards, the core knows no framework. */
@AnalyzeClasses(packages = "org.ikigaidigital", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    private static final String[] OUTER_LAYERS = {
            "org.ikigaidigital.adapter..", "org.ikigaidigital.config..", "org.springframework..", "jakarta.."};

    @ArchTest
    static final ArchRule sharedDomainClassesAreFrameworkFree = noClasses()
            .that().resideInAPackage("org.ikigaidigital")
            .and().doNotHaveSimpleName("TimeDepositApplication")
            .should().dependOnClassesThat().resideInAnyPackage(OUTER_LAYERS)
            .orShould().dependOnClassesThat().resideInAPackage("org.ikigaidigital.application..");

    @ArchTest
    static final ArchRule domainIsFrameworkFree = noClasses()
            .that().resideInAPackage("org.ikigaidigital.domain..")
            .should().dependOnClassesThat().resideInAnyPackage(OUTER_LAYERS)
            .orShould().dependOnClassesThat().resideInAPackage("org.ikigaidigital.application..");

    /** Pragmatic hexagon: the application layer may use Spring stereotypes and @Transactional, nothing else. */
    @ArchTest
    static final ArchRule applicationUsesOnlySpringStereotypesAndTransactions = noClasses()
            .that().resideInAPackage("org.ikigaidigital.application..")
            .should().dependOnClassesThat(resideInAnyPackage("org.springframework..")
                    .and(not(resideInAnyPackage(
                            "org.springframework.stereotype..",
                            "org.springframework.context.annotation..",
                            "org.springframework.transaction.annotation.."))))
            .orShould().dependOnClassesThat().resideInAnyPackage(
                    "org.ikigaidigital.adapter..", "org.ikigaidigital.config..", "jakarta..");

    @ArchTest
    static final ArchRule adaptersDoNotDependOnEachOther = noClasses()
            .that().resideInAPackage("org.ikigaidigital.adapter.in..")
            .should().dependOnClassesThat().resideInAPackage("org.ikigaidigital.adapter.out..");

    @ArchTest
    static final ArchRule webAdapterUsesInboundPortsOnly = noClasses()
            .that().resideInAPackage("org.ikigaidigital.adapter.in..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.ikigaidigital.application.service..", "org.ikigaidigital.application.port.out..");

    @ArchTest
    static final ArchRule jpaStaysInsideThePersistenceAdapter = noClasses()
            .that().resideOutsideOfPackage("org.ikigaidigital.adapter.out.persistence..")
            .should().dependOnClassesThat().resideInAnyPackage("jakarta.persistence..", "org.hibernate..",
                    "org.springframework.data..");

    @ArchTest
    static final ArchRule persistenceAdaptersImplementOutboundPorts = classes()
            .that().resideInAPackage("org.ikigaidigital.adapter.out.persistence..")
            .and().haveSimpleNameEndingWith("PersistenceAdapter")
            .should().implement(DescribedPredicate.describe("an outbound port",
                    (JavaClass port) -> port.getPackageName().startsWith("org.ikigaidigital.application.port.out")));
}
