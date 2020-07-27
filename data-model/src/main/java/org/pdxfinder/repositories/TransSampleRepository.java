package org.pdxfinder.repositories;

import org.pdxfinder.Sample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransSampleRepository extends JpaRepository<Sample, Integer> {

}
