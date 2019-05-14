package com.kurzawsk.simple_bank;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.kurzawsk.simple_bank.control.*;
import com.kurzawsk.simple_bank.control.exception.BeanValidationConstraintExceptionMapper;
import com.kurzawsk.simple_bank.control.exception.DefaultExceptionMapper;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import javax.inject.Singleton;
import java.util.Collections;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AppConfig extends ResourceConfig {

    public static final String API_VERSION = "v1.0";

    public AppConfig() {
        register(getSwaggerResourceConfig());
        register(new JacksonJaxbJsonProvider(getObjectMapper(), JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS));
        register(BeanValidationConstraintExceptionMapper.class);
        register(DefaultExceptionMapper.class);
        registerAsSingletons(TransferService.class, AccountService.class, TransferRepository.class, AccountRepository.class, AccountConverter.class);
        packages("com.kurzawsk.simple_bank.boundary");
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    private ObjectMapper getObjectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    private void registerAsSingletons(Class<?>... classes) {
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                for (Class<?> clazz : classes) {
                    bindAsContract(clazz).in(Singleton.class);
                }
            }
        });
    }

    private OpenApiResource getSwaggerResourceConfig() {
        OpenAPI oas = new OpenAPI();
        Info info = new Info()
                .title("Simple bank")
                .version("1.0")
                .description("Simple application for demonstrating money transfers between bank accounts");
        oas.info(info);


        oas.servers(Collections.singletonList(new Server().url("/" + API_VERSION)));


        SwaggerConfiguration oasConfig = new SwaggerConfiguration()
                .openAPI(oas)
                .prettyPrint(true)
                .resourcePackages(Stream.of("com.kurzawsk.simple_bank.boundary").collect(Collectors.toSet()));


        OpenApiResource openApiResource = new OpenApiResource();
        openApiResource.setOpenApiConfiguration(oasConfig);
        return openApiResource;
    }
}
