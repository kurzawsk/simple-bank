package com.kurzawsk.simple_bank.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

public class TransferDTO {

    private Long id;
    private String title;
    private ZonedDateTime timestamp;
    private BigDecimal amount;
    private Long sourceAccountId;
    private Long targetAccountId;


    public Long getId() {
        return id;
    }

    public TransferDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public TransferDTO setTitle(String title) {
        this.title = title;
        return this;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public TransferDTO setTimestamp(ZonedDateTime timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransferDTO setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public Long getTargetAccountId() {
        return targetAccountId;
    }

    public TransferDTO setTargetAccountId(Long targetAccountId) {
        this.targetAccountId = targetAccountId;
        return this;
    }

    public Long getSourceAccountId() {
        return sourceAccountId;
    }

    public TransferDTO setSourceAccountId(Long sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransferDTO)) return false;
        TransferDTO that = (TransferDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(title, that.title) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.nonNull(amount) && amount.compareTo(that.amount) == 0 &&
                Objects.equals(sourceAccountId, that.sourceAccountId) &&
                Objects.equals(targetAccountId, that.targetAccountId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, title, timestamp, amount, sourceAccountId, targetAccountId);
    }
}
