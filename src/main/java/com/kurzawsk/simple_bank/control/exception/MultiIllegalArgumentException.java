package com.kurzawsk.simple_bank.control.exception;

public class MultiIllegalArgumentException extends IllegalArgumentException {
    private final String[] messages;

    public MultiIllegalArgumentException(String[] messages) {
        super();
        this.messages = messages;
    }

    public String[] getMessages() {
        return messages;
    }
}
