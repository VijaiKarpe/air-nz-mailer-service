package com.air.nz.configurations;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Component
@Configuration
@EnableWebMvc
public class Swagger {

    //region SWAGGER INFO
    @Value("${swaggerInfo.groupName}")
    private String GROUP_NAME;

    @Value("${swaggerInfo.basePackage}")
    private String BASE_PACKAGE;

    @Value("${swaggerInfo.title}")
    private String TITLE;

    @Value("${swaggerInfo.description}")
    private String DESCRIPTION;

    @Value("${swaggerInfo.version}")
    private String VERSION;

    @Value("${swaggerInfo.summary}")
    private String SUMMARY;

    @Value("${swaggerInfo.termsOfService}")
    private String TERMS_OF_SERVICE;

    @Value("${swaggerInfo.contactDetails.name}")
    private String CONTACT_NAME;

    @Value("${swaggerInfo.contactDetails.url}")
    private String CONTACT_URL;

    @Value("${swaggerInfo.contactDetails.email}")
    private String CONTACT_EMAIL;

    @Value("${swaggerInfo.license.terms}")
    private String LICENSE_TERMS;

    @Value("${swaggerInfo.license.url}")
    private String LICENSE_URL;
    //endregion


    @ConditionalOnMissingBean
    @Bean
    public GroupedOpenApi domainApi(){

        return GroupedOpenApi.builder().group(GROUP_NAME).pathsToMatch("/**").build();

    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(TITLE)
                        .description(DESCRIPTION)
                        .version(VERSION)
                        .summary(SUMMARY)
                        .contact( new Contact()
                                .email(CONTACT_EMAIL)
                                .name(CONTACT_NAME))
                        .license(new License()
                                .name(LICENSE_TERMS)
                                .url(LICENSE_URL)));
    }

}
