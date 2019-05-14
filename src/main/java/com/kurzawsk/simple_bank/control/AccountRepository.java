package com.kurzawsk.simple_bank.control;

import com.google.common.collect.Maps;
import com.kurzawsk.simple_bank.control.exception.EntityAlreadyExistsException;
import com.kurzawsk.simple_bank.entity.domain.Account;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class AccountRepository {

    private Map<Long, Account> accounts = Maps.newConcurrentMap();
    private Map<String, Long> accountIdByNumber = Maps.newConcurrentMap();
    private AtomicLong idGenerator = new AtomicLong(0);

    public Optional<Account> find(long id) {
        return Optional.ofNullable(accounts.get(id));
    }

    public Optional<Account> findByNumber(String number) {
        return Optional.ofNullable(accountIdByNumber.get(number))
                .map(accounts::get);
    }

    public Account createAccount(Account account) {
        long id = idGenerator.incrementAndGet();
        Account createdAccount = new Account();

        if (Objects.nonNull(accountIdByNumber.putIfAbsent(account.getNumber(), id))) {
            throw new EntityAlreadyExistsException("Account with number: " + account.getNumber() + " already exists");
        }

        createdAccount.setBalance(account.getBalance());
        createdAccount.setNumber(account.getNumber());
        createdAccount.setOwner(account.getOwner());
        createdAccount.setId(id);
        createdAccount.setLock(new ReentrantLock());
        accounts.put(id, createdAccount);
        return createdAccount;
    }


}
