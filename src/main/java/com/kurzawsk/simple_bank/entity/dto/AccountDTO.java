package com.kurzawsk.simple_bank.entity.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;


@JsonDeserialize(builder = AccountDTO.AccountDTOBuilder.class)
@Builder
@Getter
@Setter
public final class AccountDTO {

    private final Long id;

    //@ApiModelProperty(value = "Account owner name", required = true)
    @NotNull(message = "Account owner name must not be null")
    @Size(max = 100, min = 2, message = "Account owner name must not be shorter than 2 and longer than 100 characters")
    private final String owner;

    //@ApiModelProperty(value = "Account balance", required = true)
    @NotNull(message = "Account balance must not be null")
    @Digits(integer = 12, fraction = 2)
    private BigDecimal balance;

    //@ApiModelProperty(value = "Account number / BBAN", required = true)
    @NotNull(message = "Account number must not be null")
    @Size(max = 50, min = 2, message = "Account number must not be shorter than 2 and longer than 50 characters")
    private final String number;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class AccountDTOBuilder {
    }

}
