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

    private static final String chemicalModifierBranchUrl = "http://purl.obolibrary.org/obo/NCIT_C1932";
    private static final String dietarySupplementBranchUrl = "http://purl.obolibrary.org/obo/NCIT_C1505";
    private static final String drugOrChemByStructBranchUrl = "http://purl.obolibrary.org/obo/NCIT_C1913";
    private static final String industrialAidBranchUrl = "http://purl.obolibrary.org/obo/NCIT_C45678";
    private static final String pharmaSubstanceBranchUrl = "http://purl.obolibrary.org/obo/NCIT_C1909";
    private static final String physiologyBranchUrl = "http://purl.obolibrary.org/obo/NCIT_C1899";
    private static final String hematopoieticBranchUrl = "http://purl.obolibrary.org/obo/NCIT_C15431";
    private static final String therapeuticProceduresBranchUrl = "http://purl.obolibrary.org/obo/NCIT_C49236";
    private static final String clinicalStudyBranchUrl = "http://purl.obolibrary.org/obo/NCIT_C15206";
    private static final String geneProductBranchUrl = "http://purl.obolibrary.org/obo/NCIT_C26548";


    private static final String regimenBranchUrl = "http://purl.obolibrary.org/obo/NCIT_C12218";
    private static final String customRegimenBranchUrl = "http://purl.obolibrary.org/obo/NCIT_C11197";

    Set<String> visitedTerms = new HashSet<>();
    Set<OntologyTerm> toBeSavedTerms = new HashSet<>();
    Set<OntologyTerm> discoveredTerms = new HashSet<>();

    @Autowired
    OntologyTermRepository ontologyTermRepository;

    @Autowired
    UtilityService utilityService;

    public Set<String> getVisitedTerms() {
        return visitedTerms;
    }

    @Transactional
    public void reloadDiagnosisTerms(){
        visitedTerms = new HashSet<>();
        discoveredTerms = new HashSet<>();
        toBeSavedTerms = new HashSet<>();
        ontologyTermRepository.deleteAllByType("diagnosis");
        getDiagnosisTerms();
        saveOntologyTerms();
        log.info("done");
    }

    @Transactional
    public void reloadTreatmentTerms(){
        visitedTerms = new HashSet<>();
        discoveredTerms = new HashSet<>();
        toBeSavedTerms = new HashSet<>();
        ontologyTermRepository.deleteAllByType("treatment");
        ontologyTermRepository.deleteAllByType("regimen");
        getTreatmentTerms();
        saveOntologyTerms();
        log.info("done");
    }

    @Transactional
    public void getTreatmentTerms(){
        log.info("Querying treatment and regimen branches in OLS");
        getTreatmentTermsFromBranch("treatment", chemicalModifierBranchUrl);
        getTreatmentTermsFromBranch("treatment", dietarySupplementBranchUrl);
        getTreatmentTermsFromBranch("treatment", drugOrChemByStructBranchUrl);
        getTreatmentTermsFromBranch("treatment", industrialAidBranchUrl);
        getTreatmentTermsFromBranch("treatment", pharmaSubstanceBranchUrl);
        getTreatmentTermsFromBranch("treatment", physiologyBranchUrl);
        getTreatmentTermsFromBranch("treatment", hematopoieticBranchUrl);
        getTreatmentTermsFromBranch("treatment", therapeuticProceduresBranchUrl);
        getTreatmentTermsFromBranch("treatment", clinicalStudyBranchUrl);
        getTreatmentTermsFromBranch("treatment", geneProductBranchUrl);
        getTreatmentTermsFromBranch("regimen", regimenBranchUrl);
        getTreatmentTermsFromBranch("regimen", customRegimenBranchUrl);

    }

    public void getTreatmentTermsFromBranch(String type, String branchRootUrl){

        int totalPages = 0;
        boolean totalPagesDetermined = false;

        for (int currentPage = 0; currentPage <= totalPages; currentPage++) {

            String encodedTermUrl = "";
            try {
                //have to double encode the url to get the desired result
                encodedTermUrl = URLEncoder.encode(branchRootUrl, UTF8);
                encodedTermUrl = URLEncoder.encode(encodedTermUrl, UTF8);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String url = OLS_BASE_URL + encodedTermUrl + "/hierarchicalDescendants?size=200&page=" + currentPage;
            log.info(url);
            String json = utilityService.parseURL(url);
            JSONObject job = new JSONObject(json);
            parseHierarchicalChildren(job, type);

            if (!totalPagesDetermined) {
                //String page = job.getString("page");
                JSONObject pageObj = job.getJSONObject("page");
                    totalPages = pageObj.getInt("totalPages") - 1;
                    totalPagesDetermined = true;
                }
        }
    }


    public void getDiagnosisTerms(){
        log.info("Getting diagnosis terms");

        //create the Cancer term
        OntologyTerm cancerTerm = new OntologyTerm("http://purl.obolibrary.org/obo/NCIT_C9305","Cancer","diagnosis");

        discoveredTerms.add(cancerTerm);
        toBeSavedTerms.add(cancerTerm);

        while(!discoveredTerms.isEmpty()){

            OntologyTerm notYetVisitedTerm = discoveredTerms.iterator().next();
            discoveredTerms.remove(notYetVisitedTerm);
            if(visitedTerms.contains(notYetVisitedTerm.getUrl())) continue;
            visitedTerms.add(notYetVisitedTerm.getUrl());
            if(visitedTerms.size()%500 == 0) log.info("Loaded {} terms", visitedTerms.size());

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
            JSONObject job = new JSONObject(json);
            //if this term does not have child nodes, continue
            parseHierarchicalChildren(job, "diagnosis");
        }
    }

    public void parseHierarchicalChildren(JSONObject job, String type){

        try {

            if (!job.has(EMBEDDED)) return;
            JSONObject job2 = job.getJSONObject("_embedded");
            JSONArray hierarchicalChildren = job2.getJSONArray("terms");

            for (int i = 0; i < hierarchicalChildren.length(); i++) {

                JSONObject term = hierarchicalChildren.getJSONObject(i);
                OntologyTerm newTerm = null;
                if(type.equalsIgnoreCase("diagnosis")) {
                    newTerm = createDiagnosisOntologyTerm(term);
                }
                else{
                    newTerm = createOntologyTerm(term, type);
                }
                if(newTerm != null){
                    toBeSavedTerms.add(newTerm);
                    discoveredTerms.add(newTerm);
                }
            }

        } catch (Exception e) {
            log.error(" {} ", e.getMessage());
        }

    }

    private OntologyTerm createOntologyTerm(JSONObject term, String type){
        String url = term.getString("iri");
        if(visitedTerms.contains(url)) return null;

        String termLabel = term.getString("label");
        OntologyTerm newTerm = new OntologyTerm(url, termLabel, type);
        String description = "";
        newTerm.setDescription(description);
        return newTerm;
    }

    private OntologyTerm createDiagnosisOntologyTerm(JSONObject term){
        String url = term.getString("iri");
        if(visitedTerms.contains(url)) return null;

        String termLabel = term.getString("label");
        String updatedTermLabel = updateTermLabel(termLabel);

        termLabel = termLabel.replaceAll(",", "");

        OntologyTerm newTerm = new OntologyTerm(
                url,
                updatedTermLabel != null ? updatedTermLabel : termLabel, "diagnosis");

        JSONArray synonyms = term.getJSONArray("synonyms");
        Set<String> synonymsSet = new HashSet<>();

        for(int j=0; j<synonyms.length();j++){
            synonymsSet.add(synonyms.getString(j));
        }
        newTerm.setSynonyms(synonymsSet);

        String description = "";
                /*
                if(term.has("description")){
                    try {
                        JSONArray descriptions = term.getJSONArray("description");

                        for (int j = 0; j < descriptions.length(); j++) {
                            description += descriptions.getString(j);
                        }
                    }
                    catch(Exception e){
                        description = term.getString("description");
                    }
                }
*/
        newTerm.setDescription(description);
        return newTerm;
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


    public void saveOntologyTerms(){
        log.info("Saving {} ontology terms to db", toBeSavedTerms.size());
        ontologyTermRepository.saveAll(toBeSavedTerms);
    }


    public List<OntologyTerm> getTermsByType(String type){

        List<OntologyTerm> result;
        if(type.equalsIgnoreCase("treatment")){
            result = ontologyTermRepository.findAllByType(type);
            result.addAll(ontologyTermRepository.findAllByType("regimen"));
        }
        else{
            result = ontologyTermRepository.findAllByType(type);
        }
        return result;
    }

}
