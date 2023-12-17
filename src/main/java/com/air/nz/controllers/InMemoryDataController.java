package com.air.nz.controllers;

import com.air.nz.exceptions.type.InternalFailure;
import com.air.nz.repository.DataManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Component
@RestController
@Tag(name = "Data (Set Up & Tear Down)")
@RequestMapping("/0.0.1/data")
public class InMemoryDataController extends DataManager {

    @Value("${swaggerInfo.groupName}")
    private String GROUP_NAME;

    private  final Logger logger = LoggerFactory.getLogger(InMemoryDataController.class);
    final String DATA_SET_UP_FAILURE = "Failure whilst setting up data. ";

    public InMemoryDataController() throws Exception {
    }

    @Operation(summary = "Load Email (Test Set Up).", description = "Loads a test set of Emails in memory")
    @PostMapping("/load")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public boolean loadEmails(@RequestHeader(value = "x-request-id") String correlationId) throws Exception {

        try {
            return loadAvailableFolderItems();
        }catch (Exception e){
            throw new InternalFailure(DATA_SET_UP_FAILURE + stackTraceToString(e));
        }
    }

    @Operation(summary = "Un-Load Email (Test Tear Down.", description = "Deletes all Emails in memory")
    @PostMapping("/unload")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public boolean unLoadEmails(@RequestHeader(value = "x-request-id") String correlationId) throws Exception {

        try {
            return unloadAvailableFolderItems();
        }catch (Exception e){
            throw new InternalFailure(DATA_SET_UP_FAILURE + stackTraceToString(e));
        }
    }
}