package com.kurzawsk.simple_bank;


import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.util.concurrent.atomic.AtomicLong;

public class BaseRestApiIT {

    static RequestSpecification spec;
    private static final String VERSION = "v1.0";
    private static final String BASE_URI = "http://localhost:8000";
    private static final AtomicLong counter = new AtomicLong(0L);

    @BeforeAll
    public static void setUp() {
        spec = RestApiTestHelper.createSpec(BASE_URI).basePath(VERSION);
        Application.main(new String[0]);
    }

    @AfterAll
    public static void tearDown() throws Exception {
        Application.stopService();
    }

    protected long getNextNumber() {
        return counter.incrementAndGet();
    }
}
