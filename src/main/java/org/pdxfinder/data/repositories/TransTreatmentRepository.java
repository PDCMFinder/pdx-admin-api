package org.pdxfinder.data.repositories;

import org.pdxfinder.data.model.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransTreatmentRepository extends JpaRepository<Treatment, Integer> {

}
