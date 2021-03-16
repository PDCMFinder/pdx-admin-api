package org.pdxfinder.controllers;


import org.pdxfinder.OntologyTermService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/ontology")
public class OntologyTermController {

    OntologyTermService ontologyTermService;

    @Autowired
    public OntologyTermController(OntologyTermService ontologyTermService) {
        this.ontologyTermService = ontologyTermService;
    }


    @GetMapping("reloaddiagnosisterms")
    public ResponseEntity<?> reloadDiagnosisTerms() {
        ontologyTermService.reloadDiagnosisTerms();
        return new ResponseEntity<>(ontologyTermService.getVisitedTerms().size(), HttpStatus.OK);
    }

    @GetMapping("reloadtreatmentterms")
    public ResponseEntity<?> reloadTreatmentTerms() {
        ontologyTermService.reloadTreatmentTerms();
        return new ResponseEntity<>(ontologyTermService.getVisitedTerms().size(), HttpStatus.OK);
    }



}
