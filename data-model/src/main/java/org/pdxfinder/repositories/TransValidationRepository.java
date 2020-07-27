package org.pdxfinder.repositories;

import org.pdxfinder.Validation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransValidationRepository extends JpaRepository<Validation, Integer> {

}