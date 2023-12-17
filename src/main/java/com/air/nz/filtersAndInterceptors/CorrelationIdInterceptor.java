package com.air.nz.filtersAndInterceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
public class CorrelationIdInterceptor implements ClientHttpRequestInterceptor {

    private final Logger logger = LoggerFactory.getLogger(CorrelationIdInterceptor.class);
    private static final String HEADERS = "HEADERS:";

    @Value("${tracing.header.correlationIdKey}")
    public String HEADER_CORRELATION_ID_KEY;


    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logger.info(HEADERS);
        HttpHeaders headers = request.getHeaders();
        //headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HEADER_CORRELATION_ID_KEY, MDC.get(HEADER_CORRELATION_ID_KEY));
        headers.remove(HttpHeaders.CONTENT_LENGTH);
        headers.remove(HttpHeaders.ACCEPT_CHARSET);
        headers.forEach((key, value) -> {
            logger.info(
                "%s = %s".formatted(key, value.stream().collect(Collectors.joining("|"))));
        });

        return execution.execute(request, body);
    }
}