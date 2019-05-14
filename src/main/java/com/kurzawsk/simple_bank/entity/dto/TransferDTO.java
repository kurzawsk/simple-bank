package com.kurzawsk.simple_bank.entity.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@JsonDeserialize(builder = TransferDTO.TransferDTOBuilder.class)
@Builder
@Getter
@EqualsAndHashCode
//@ApiModel( value = "Transfer", description = "Money transfer between accounts representation" )
public class TransferDTO {

    private final Long id;

    // @ApiModelProperty( value = "Transfer title", required = true )
    private final String title;

    // @ApiModelProperty( value = "Transfer operation timestamp (ISO-8601 format)", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private final ZonedDateTime timestamp;

    //@ApiModelProperty( value = "Amount of money to transfer", required = true )
    private final BigDecimal amount;

    //@ApiModelProperty( value = "Id of target account", required = true )
    private final Long targetAccountId;

    // @ApiModelProperty( value = "Id of source account", required = true )
    private final Long sourceAccountId;

    @JsonPOJOBuilder(withPrefix = "")
    public static final class TransferDTOBuilder {
    }


}
