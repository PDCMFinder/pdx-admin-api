package org.pdxfinder.repositories;

import org.pdxfinder.OntologyTerm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OntologyTermRepository extends JpaRepository<OntologyTerm, Integer> {

  OntologyTerm findByLabelAndType(String label, String type);

  List<OntologyTerm> findAllByType(String type);

  void deleteAllByType(String type);
}
