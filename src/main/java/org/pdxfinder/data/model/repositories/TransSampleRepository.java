package org.pdxfinder.data.model.repositories;

import org.pdxfinder.data.model.Sample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransSampleRepository extends JpaRepository<Sample, Integer> {

}
