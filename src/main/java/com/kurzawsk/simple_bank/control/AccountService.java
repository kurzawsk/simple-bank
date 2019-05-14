package com.kurzawsk.simple_bank.control;

import com.kurzawsk.simple_bank.entity.domain.Account;
import com.kurzawsk.simple_bank.entity.dto.AccountDTO;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

public class AccountService {

    private final AccountConverter accountConverter;
    private final AccountRepository accountRepository;

    @Inject
    public AccountService(AccountRepository accountRepository, AccountConverter accountConverter) {
        this.accountRepository = accountRepository;
        this.accountConverter = accountConverter;
    }

    public AccountDTO createAccount(AccountDTO dto) {
        Account account = accountConverter.dtoToDomain(dto);
        return accountConverter.domainToDto(accountRepository.createAccount(account));
    }

    public AccountDTO find(long id) {
        return accountRepository.find(id)
                .map(accountConverter::domainToDto)
                .orElseThrow(() -> new NotFoundException("Account with id: " + id + " does not exist"));
    }

    public AccountDTO findByNumber(String number) {
        return accountRepository.findByNumber(number)
                .map(accountConverter::domainToDto)
                .orElseThrow(() -> new NotFoundException("Account with number: " + number + " does not exist"));
    }

}
