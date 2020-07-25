package org.pdxfinder.data.repositories;

import org.pdxfinder.data.model.Validation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransValidationRepository extends JpaRepository<Validation, Integer> {

}