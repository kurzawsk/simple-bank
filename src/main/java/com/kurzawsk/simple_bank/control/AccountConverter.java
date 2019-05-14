package com.kurzawsk.simple_bank.control;


import com.kurzawsk.simple_bank.entity.domain.Account;
import com.kurzawsk.simple_bank.entity.dto.AccountDTO;

import java.util.Objects;


public class AccountConverter {

    public Account dtoToDomain(AccountDTO accountDTO) {
        Account account = new Account();
        account.setId(accountDTO.getId());
        account.setOwner(accountDTO.getOwner());
        account.setNumber(accountDTO.getNumber());
        account.setBalance(accountDTO.getBalance());
        return account;
    }

    public AccountDTO domainToDto(Account account) {
        return Objects.isNull(account) ? null : AccountDTO.builder()
                .id(account.getId())
                .owner(account.getOwner())
                .number(account.getNumber())
                .balance(account.getBalance())
                .build();
    }
}
