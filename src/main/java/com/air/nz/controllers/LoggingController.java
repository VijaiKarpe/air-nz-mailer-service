package com.air.nz.controllers;

import ch.qos.logback.classic.Level;
import  com.air.nz.configurations.LogLevel;
import com.air.nz.exceptions.type.LogLevelNotFound;
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
@Tag(name = "Application Logging")
@RequestMapping("/0.0.1/logging")
public class LoggingController extends DataManager {

    @Value("${swaggerInfo.groupName}")
    private String GROUP_NAME;

    private  final Logger logger = LoggerFactory.getLogger(LoggingController.class);

    public LoggingController() throws Exception {
    }

    @Operation(summary = "Change the logging Level for the application.", description = "The levels which can be set are DEBUG, INFO, WARN, ERROR, TRACE, ALL and OFF")
    @PutMapping("{logLevel}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public LogLevel entirePackage(@RequestHeader(value = "x-request-id") String correlationId, @PathVariable String logLevel) throws LogLevelNotFound {

        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(GROUP_NAME);

        //Set level as defensive precaution
        root.setLevel(Level.ERROR);

        switch (logLevel.toUpperCase()){

            case "DEBUG":
                logger.info("Logging set to DEBUG.");
                root.setLevel(Level.DEBUG);
                return new LogLevel(logLevel);

            case "INFO":
                logger.info("Logging set to INFO.");
                root.setLevel(Level.INFO);
                return new LogLevel(logLevel);

            case "WARN":
                logger.info("Logging set to WARN.");
                root.setLevel(Level.WARN);
                return new LogLevel(logLevel);

            case "ERROR":
                logger.info("Logging set to ERROR.");
                root.setLevel(Level.ERROR);
                return new LogLevel(logLevel);

            case "TRACE":
                logger.info("Logging set to TRACE.");
                root.setLevel(Level.TRACE);
                return new LogLevel(logLevel);

            case "ALL":
                logger.info("Logging set to ALL.");
                root.setLevel(Level.ALL);
                return new LogLevel(logLevel);

            case "OFF":
                logger.info("Logging set to OFF.");
                root.setLevel(Level.OFF);
                return new LogLevel(logLevel);

                default:
                    throw new LogLevelNotFound(logLevel, "Logging Level is not recognized.");
        }

    }

    @Operation( summary = "Gets the logging Level for the application.")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public LogLevel level(@RequestHeader(value = "x-request-id") String correlationId){
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(GROUP_NAME);
        return new LogLevel(root.getLevel().toString());

    }
}