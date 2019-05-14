package com.kurzawsk.simple_bank.control.exception;

import java.util.concurrent.TimeUnit;

public class OperationTimeoutException extends RuntimeException {

    public OperationTimeoutException(int timeout, TimeUnit timeUnit) {
        super("Could not complete operation in " + timeout + " " + timeUnit.name() + " timeout");
    }

    public OperationTimeoutException() {
        super("Could not complete operation due to a timeuot");
    }
}
