package org.pdxfinder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.pdxfinder.repositories.OntologyTermRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

@Service
public class OntologyTermService {

    private static final Logger log = LoggerFactory.getLogger(OntologyTermService.class);

    private static final String UTF8 = "UTF-8";
    private static final String EMBEDDED = "_embedded";
    private static final String OLS_BASE_URL = "https://www.ebi.ac.uk/ols/api/ontologies/ncit/terms/";

    Set<String> loadedTerms = new HashSet<>();
    Set<OntologyTerm> discoveredTerms = new HashSet<>();

    @Autowired
    OntologyTermRepository ontologyTermRepository;

    @Autowired
    UtilityService utilityService;

    public Set<String> getLoadedTerms() {
        return loadedTerms;
    }

    @Transactional
    public void reloadDiagnosisTerms(){
        loadedTerms = new HashSet<>();
        discoveredTerms = new HashSet<>();
        ontologyTermRepository.deleteAllByType("diagnosis");
        getDiagnosisTerms();
    }

    @Transactional
    public void reloadTreatmentTerms(){
        loadedTerms = new HashSet<>();
        discoveredTerms = new HashSet<>();
        ontologyTermRepository.deleteAllByType("treatment");
        ontologyTermRepository.deleteAllByType("regimen");
        getTreatmentTerms();
    }

    private void getTreatmentTerms(){

    }

    private void getDiagnosisTerms(){
        log.info("Getting diagnosis terms");

        //create the Cancer term
        OntologyTerm cancerTerm = new OntologyTerm("http://purl.obolibrary.org/obo/NCIT_C9305","Cancer","diagnosis");
        ontologyTermRepository.save(cancerTerm);

        discoveredTerms.add(cancerTerm);


        while(!discoveredTerms.isEmpty()){

            OntologyTerm notYetVisitedTerm = discoveredTerms.iterator().next();
            discoveredTerms.remove(notYetVisitedTerm);
            if(loadedTerms.contains(notYetVisitedTerm.getUrl())) continue;
            loadedTerms.add(notYetVisitedTerm.getUrl());

            String parentUrlEncoded = "";
            try {
                //have to double encode the url to get the desired result
                parentUrlEncoded = URLEncoder.encode(notYetVisitedTerm.getUrl(), UTF8);
                parentUrlEncoded = URLEncoder.encode(parentUrlEncoded, UTF8);

            } catch (UnsupportedEncodingException e) {
                log.warn(e.getMessage());
            }
            String url = OLS_BASE_URL+parentUrlEncoded+"/hierarchicalChildren?size=200";
            String json = utilityService.parseURL(url);
            parseHierarchicalChildren(json, "diagnosis");
        }

    }

    private void parseHierarchicalChildren(String json, String type){

        try {
            JSONObject job = new JSONObject(json);
            //if this term does not have child nodes, continue
            if (!job.has(EMBEDDED)) return;

            JSONObject job2 = job.getJSONObject("_embedded");

            //JSONObject job2 = new JSONObject(embedded);
            JSONArray hierarchicalChildren = job2.getJSONArray("terms");

            for (int i = 0; i < hierarchicalChildren.length(); i++) {

                JSONObject term = hierarchicalChildren.getJSONObject(i);
                String termLabel = term.getString("label");
                String updatedTermLabel = updateTermLabel(termLabel);

                termLabel = termLabel.replaceAll(",", "");

                OntologyTerm newTerm = new OntologyTerm(
                        term.getString("iri"),
                        updatedTermLabel != null ? updatedTermLabel : termLabel, type);

                JSONArray synonyms = term.getJSONArray("synonyms");
                Set<String> synonymsSet = new HashSet<>();

                for(int j=0; j<synonyms.length();j++){
                    synonymsSet.add(synonyms.getString(j));
                }

                newTerm.setSynonyms(synonymsSet);
                discoveredTerms.add(newTerm);
                ontologyTermRepository.save(newTerm);
                log.info("Saving {}", newTerm.getLabel());
            }

        } catch (Exception e) {
            log.error(" {} ", e.getMessage());
        }
    }

    private String updateTermLabel(String termLabel){

        // Changes Malignant * Neoplasm to * Cancer
        String pattern = "(.*)Malignant(.*)Neoplasm(.*)";
        String updatedTermlabel = null;

        if (termLabel.matches(pattern)) {
            updatedTermlabel = (termLabel.replaceAll(pattern, "\t$1$2Cancer$3")).trim();
            log.trace("Replacing term label '{}' with '{}'", termLabel, updatedTermlabel);
        }
        return updatedTermlabel;
    }

}
