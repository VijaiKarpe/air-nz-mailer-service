package com.air.nz.filtersAndInterceptors;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class IncomingRequestFilter implements Filter {

    private  final Logger logger = LoggerFactory.getLogger(IncomingRequestFilter.class);

    @Value("${tracing.header.correlationIdKey}")
    private String HEADER_CORRELATION_ID_KEY;

    @Value("${tracing.applicationNameKey}")
    private String APPLICATION_NAME_KEY;

    @Value("${spring.application.name}")
    private String APPLICATION_NAME;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        if (req instanceof HttpServletRequest request) {
            String correlationId = request.getHeader(HEADER_CORRELATION_ID_KEY);
            MDC.put(APPLICATION_NAME_KEY, APPLICATION_NAME);

            if (correlationId == null || correlationId.isEmpty()){
                //Generate a UUID for the purpose of traceability
                MDC.put(HEADER_CORRELATION_ID_KEY, UUID.randomUUID().toString());
                logger.info("correlation-id not set");

            }else{
                // add cid to the MDC
                MDC.put(HEADER_CORRELATION_ID_KEY, correlationId);
            }
        }

        try {
            // call filter(s) upstream for the real processing of the request
            chain.doFilter(req, res);
        } finally {
            // it's important to always clean the cid from the MDC,
            // this Thread goes to the pool but it's loglines would still contain the cid.
            MDC.remove(HEADER_CORRELATION_ID_KEY);
            MDC.remove(APPLICATION_NAME_KEY);
        }

    }

    @Override
    public void destroy() {
        // nothing
    }
    @Override
    public void init(FilterConfig fc) throws ServletException {
        // nothing
    }
}
