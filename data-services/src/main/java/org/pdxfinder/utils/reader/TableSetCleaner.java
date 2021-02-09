package org.pdxfinder.utils.reader;


import org.springframework.stereotype.Service;
import tech.tablesaw.api.Table;

import java.util.Map;

@Service
public class TableSetCleaner {

    public Map<String, Table> cleanPdxTables(Map<String, Table> pdxTableSet) {
        pdxTableSet = TableSetUtilities.removeProviderNameFromFilename(pdxTableSet);
        pdxTableSet.remove("metadata-checklist.tsv");
        TableSetUtilities.removeDescriptionColumn(pdxTableSet);
        pdxTableSet = TableSetUtilities.removeHeaderRows(pdxTableSet);
        pdxTableSet = TableSetUtilities.removeBlankRows(pdxTableSet);
        return pdxTableSet;
    }

    public Map<String, Table> cleanTreatmentTables(Map<String, Table> treatmentTableSet) {
        treatmentTableSet = TableSetUtilities.removeProviderNameFromFilename(treatmentTableSet);
        treatmentTableSet = TableSetUtilities.removeHeaderRowsIfPresent(treatmentTableSet);
        TableSetUtilities.removeDescriptionColumn(treatmentTableSet);
        return treatmentTableSet;
    }
}