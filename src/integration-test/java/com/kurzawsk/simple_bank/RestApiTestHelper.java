package com.kurzawsk.simple_bank;


import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class RestApiTestHelper {

    public static RequestSpecification createSpec(String baseUri) {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(baseUri)
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new RequestLoggingFilter())
                .build();
    }

    public static <T> T createAndGetResource(String path, Object bodyPayload, Class<T> bodyClass, RequestSpecification spec) {
        String location = given()
                .spec(spec)
                .body(bodyPayload)
                .when()
                .post(path)
                .then()
                .statusCode(201)
                .extract()
                .header("location");
        return getResource(location, bodyClass, spec);
    }

    public static String createResource(String path, Object bodyPayload, RequestSpecification spec) {
        return given()
                .spec(spec)
                .body(bodyPayload)
                .when()
                .post(path)
                .then()
                .statusCode(201)
                .extract()
                .header("location");
    }

    public static <T> T getResource(String location, Class<T> responseClass, RequestSpecification spec) {
        return given()
                .spec(spec)
                .when()
                .get(location)
                .then()
                .statusCode(200)
                .extract()
                .as(responseClass);
    }
}
