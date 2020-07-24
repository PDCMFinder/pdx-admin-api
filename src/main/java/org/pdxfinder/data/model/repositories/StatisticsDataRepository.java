package org.pdxfinder.data.model.repositories;

import org.pdxfinder.data.model.StatisticsData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticsDataRepository extends JpaRepository<StatisticsData, Integer> {


}
