package com.air.nz.exceptions;

import com.air.nz.exceptions.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@ControllerAdvice
public class ExceptionHandlingController {


    @Value("${responseMessages.internalError}")
    private String INTERNAL_SERVER_RESPONSE_MESSAGE;

    @Value("${responseMessages.noEmailFound}")
    private String NO_EMAIL_FOUND_MESSAGE;

    @Value("${responseMessages.resourceNotFound}")
    private String RESOURCE_NOT_FOUND_FOR;

    @Value("${responseMessages.corruptData}")
    private String CORRUPT_DATA;
    private  final Logger logger = LoggerFactory.getLogger(ExceptionHandlingController.class);


    @ExceptionHandler(BadRequestFailure.class)
    public ResponseEntity<BadRequestFailureResponse> badRequestFailure(BadRequestFailure ex) {
        logger.error(ex.getMessage());
        BadRequestFailureResponse response = new BadRequestFailureResponse();
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoEmailFound.class)
    public ResponseEntity<NoEmailFoundResponse> noEmailFound(NoEmailFound ex) {
        String s = NO_EMAIL_FOUND_MESSAGE;
        logger.info(s);
        NoEmailFoundResponse response = new NoEmailFoundResponse();
        response.setMessage(s);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<ResourceNotFoundResponse> resourceNotFound(ResourceNotFound ex) {
        String s = RESOURCE_NOT_FOUND_FOR + " " + ex.getMessage();
        logger.info(s);
        ResourceNotFoundResponse response = new ResourceNotFoundResponse();
        response.setMessage(s);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CorruptData.class)
    public ResponseEntity<CorruptDataResponse> corruptData(CorruptData ex) {
        String s = CORRUPT_DATA + " " + ex.getMessage();
        logger.info(s);
        CorruptDataResponse response = new CorruptDataResponse();
        response.setMessage(s);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InternalFailure.class)
    public ResponseEntity<InternalFailureResponse> internalFailure(InternalFailure ex) {
        logger.error(ex.getMessage());
        InternalFailureResponse response = new InternalFailureResponse();
        response.setMessage(INTERNAL_SERVER_RESPONSE_MESSAGE);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(LogLevelNotFound.class)
    public ResponseEntity<LogLevelNotFound> logLevelNotFound(LogLevelNotFound ex) {
        LogLevelNotFoundResponse response = new LogLevelNotFoundResponse();
        response.setErrorMessage(ex.getMessage());
        response.setLogLevel(ex.getLoglevel());
        logger.error(ex.getMessage()+ " " + ex.getLoglevel());
        return new ResponseEntity(response, HttpStatus.NOT_FOUND);
    }
}
