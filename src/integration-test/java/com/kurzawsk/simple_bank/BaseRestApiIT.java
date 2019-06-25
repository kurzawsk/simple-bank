package com.kurzawsk.simple_bank;


import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class BaseRestApiIT {

    static RequestSpecification spec;
    private static final String VERSION = "v1.0";
    private static final String BASE_URI = "http://localhost:8090";
    private static final AtomicLong counter = new AtomicLong(0L);
    private static final Executor pool = Executors.newSingleThreadExecutor();

    @BeforeAll
    public static void setUp() {
        spec = RestApiTestHelper.createSpec(BASE_URI).basePath(VERSION);
        CompletableFuture.runAsync(Application::startService, pool);
    }

    @AfterAll
    public static void tearDown() throws Exception {
        Application.stopService();
    }

    protected long getNextNumber() {
        return counter.incrementAndGet();
    }
}
