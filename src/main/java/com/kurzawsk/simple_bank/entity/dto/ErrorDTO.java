package com.kurzawsk.simple_bank.entity.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;

@JsonDeserialize(builder = ErrorDTO.ErrorDTOBuilder.class)
@Builder
@Getter
public final class ErrorDTO {
    private final String[] message;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class ErrorDTOBuilder {
    }
}
