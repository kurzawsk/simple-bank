package com.kurzawsk.simple_bank.dto;

import java.math.BigDecimal;
import java.util.Objects;


public final class AccountDTO {

    private Long id;
    private String owner;
    private BigDecimal balance;
    private String number;

    public Long getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getNumber() {
        return number;
    }

    public AccountDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public AccountDTO setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public AccountDTO setBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    public AccountDTO setNumber(String number) {
        this.number = number;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountDTO)) return false;
        AccountDTO that = (AccountDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(owner, that.owner) &&
                Objects.equals(balance, that.balance) &&
                Objects.equals(number, that.number);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, owner, balance, number);
    }
}
