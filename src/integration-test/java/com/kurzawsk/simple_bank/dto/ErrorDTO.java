package com.kurzawsk.simple_bank.dto;

import java.util.Arrays;

public class ErrorDTO {

    private String[] message;

    public String[] getMessage() {
        return message;
    }

    public ErrorDTO setMessage(String[] message) {
        this.message = message;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ErrorDTO)) return false;
        ErrorDTO errorDTO = (ErrorDTO) o;
        return Arrays.equals(message, errorDTO.message);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(message);
    }
}
