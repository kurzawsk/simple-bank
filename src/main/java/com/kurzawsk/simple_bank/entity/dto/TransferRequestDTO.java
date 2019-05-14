package com.kurzawsk.simple_bank.entity.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@JsonDeserialize(builder = TransferRequestDTO.TransferRequestDTOBuilder.class)
@Builder
@Getter
@EqualsAndHashCode
public class TransferRequestDTO {

    @NotNull(message = "Transfer title must not be null")
    @Size(max = 50, min = 2, message = "Transfer title must not be shorter than 2 and longer than 200 characters")
    private final String title;

    @DecimalMin(message = "Please provide correct amount to transfer", value = "0.01")
    @Digits(integer = 12, fraction = 2)
    private final BigDecimal amount;

    @NotNull(message = "Target account id must not be null")
    private final Long targetAccountId;

    @NotNull(message = "Source account id must not be null")
    private final Long sourceAccountId;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class TransferRequestDTOBuilder {
    }

}
