package com.kurzawsk.simple_bank.dto;

import java.math.BigDecimal;


public class TransferRequestDTO {
    private String title;
    private BigDecimal amount;
    private Long targetAccountId;
    private Long sourceAccountId;

    public String getTitle() {
        return title;
    }

    public TransferRequestDTO setTitle(String title) {
        this.title = title;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransferRequestDTO setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public Long getTargetAccountId() {
        return targetAccountId;
    }

    public TransferRequestDTO setTargetAccountId(Long targetAccountId) {
        this.targetAccountId = targetAccountId;
        return this;
    }

    public Long getSourceAccountId() {
        return sourceAccountId;
    }

    public TransferRequestDTO setSourceAccountId(Long sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
        return this;
    }
}
