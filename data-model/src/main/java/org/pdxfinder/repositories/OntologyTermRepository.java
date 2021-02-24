package org.pdxfinder.repositories;

import org.pdxfinder.OntologyTerm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OntologyTermRepository extends JpaRepository<OntologyTerm, Integer> {

  OntologyTerm findByLabelAndType(String label, String type);

  void deleteAllByType(String type);
}
