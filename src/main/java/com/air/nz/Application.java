package com.air.nz;


import com.air.nz.filtersAndInterceptors.CorrelationIdInterceptor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;


@SpringBootApplication
@EnableScheduling
public class Application {

	@Value("${tracing.applicationNameKey}")
	private String APPLICATION_NAME_KEY;

	@Value("${spring.application.name}")
	private String APPLICATION_NAME;

	public static void main(String[] args) {
		try {
			SpringApplication.run(Application.class, args);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Value("${rest-client.timeout.connect}")
	public Integer CONNECT_TIMEOUT;

	@Autowired
	CorrelationIdInterceptor correlationIdInterceptor;

	@Value("${rest-client.timeout.read}")
	public Integer READ_TIMEOUT;

	@Bean
	public boolean setApplicationNameForLogs(){
		MDC.put(APPLICATION_NAME_KEY, APPLICATION_NAME);
		return true;
	}

	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		ClientHttpRequestFactory requestFactory = restTemplate.getRequestFactory();
		if (requestFactory instanceof SimpleClientHttpRequestFactory factory) {
			factory.setConnectTimeout(CONNECT_TIMEOUT * 1000);
			factory.setReadTimeout(READ_TIMEOUT * 1000);
		} else if (requestFactory instanceof HttpComponentsClientHttpRequestFactory factory) {
			factory.setConnectTimeout(CONNECT_TIMEOUT * 1000);
		}
		restTemplate.setInterceptors(Arrays.asList(correlationIdInterceptor));
		return restTemplate;
	}


}

