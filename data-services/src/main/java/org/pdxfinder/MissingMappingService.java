package org.pdxfinder;

import org.pdxfinder.utils.reader.Reader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MissingMappingService {

    MappingContainer existingMappingsContainer;
    MappingContainer missingMappingsContainer;

    Reader reader;
    MappingService mappingService;


    @Autowired
    public MissingMappingService(MappingService mappingService) {
        this.mappingService = mappingService;
        reader = new Reader();

    }


    public void initExistingMappings(){

        existingMappingsContainer = mappingService.getInitializedContainer();
    }

    public MappingContainer getMissingMappings(){

        existingMappingsContainer = mappingService.getInitializedContainer();


        return missingMappingsContainer;
    }






}
