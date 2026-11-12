package org.ikigaidigital.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface TimeDepositJpaRepository extends JpaRepository<TimeDepositEntity, Integer> {
}
