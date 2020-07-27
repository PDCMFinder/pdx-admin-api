package org.pdxfinder.repositories;

import org.pdxfinder.StatisticsData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatisticsDataRepository extends JpaRepository<StatisticsData, Integer> {


}
