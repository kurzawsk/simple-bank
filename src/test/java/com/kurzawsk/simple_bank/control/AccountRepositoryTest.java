package com.kurzawsk.simple_bank.control;

import com.kurzawsk.simple_bank.control.exception.EntityAlreadyExistsException;
import com.kurzawsk.simple_bank.entity.domain.Account;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
public class AccountRepositoryTest {

    private AccountRepository accountRepository;

    @BeforeEach
    public void setUp() {
        accountRepository = new AccountRepository();
    }

    @Test
    @DisplayName("Create Transfer - Success")
    public void testCreateAccountSuccess() {
        Account account = new Account();
        account.setNumber("555-555");
        account.setOwner("test");
        account.setBalance(new BigDecimal("1000.00"));
        Account createdAccount = accountRepository.createAccount(account);

        assertThat(createdAccount.getId(), is(1L));
        assertThat(createdAccount.getNumber(), is(account.getNumber()));
        assertThat(createdAccount.getBalance(), is(account.getBalance()));
    }

    @Test
    @DisplayName("Create Transfer - Failure - Duplicate Number")
    public void testCreateAccountFailureDueToDuplicateNumber() {
        Account account = new Account();
        account.setNumber("555-555");
        account.setOwner("test");
        account.setBalance(new BigDecimal("1000.00"));
        accountRepository.createAccount(account);

        Assertions.assertThrows(EntityAlreadyExistsException.class, () -> accountRepository.createAccount(account));
    }

    @Test
    @DisplayName("Find By Id - Success")
    public void testFindAccountByIdSuccess() {
        Account account = new Account();
        account.setNumber("555-555");
        account.setOwner("test");
        account.setBalance(new BigDecimal("1000.00"));
        accountRepository.createAccount(account);

        Account foundAccount = accountRepository.find(1L).get();

        assertThat(foundAccount.getId(), is(1L));
        assertThat(foundAccount.getNumber(), is(account.getNumber()));
        assertThat(foundAccount.getBalance(), is(account.getBalance()));
    }

    @Test
    @DisplayName("Find By Id - Failure")
    public void testFindAccountByIdFailure() {
        Account account = new Account();
        account.setNumber("555-555");
        account.setOwner("test");
        account.setBalance(new BigDecimal("1000.00"));
        accountRepository.createAccount(account);

        assertThat(accountRepository.find(2L), is(Optional.empty()));
    }

    @Test
    @DisplayName("Find By Number - Success")
    public void testFindAccountByNumberSuccess() {
        Account account = new Account();
        account.setNumber("555-555");
        account.setOwner("test");
        account.setBalance(new BigDecimal("1000.00"));
        accountRepository.createAccount(account);

        Account foundAccount = accountRepository.findByNumber("555-555").get();

        assertThat(foundAccount.getId(), is(1L));
        assertThat(foundAccount.getNumber(), is(account.getNumber()));
        assertThat(foundAccount.getBalance(), is(account.getBalance()));
    }

    @Test
    @DisplayName("Find By Number - Failure")
    public void testFindAccountByNumberFailure() {
        Account account = new Account();
        account.setNumber("555-555");
        account.setOwner("test");
        account.setBalance(new BigDecimal("1000.00"));
        accountRepository.createAccount(account);

        assertThat(accountRepository.findByNumber("555-554"), is(Optional.empty()));
    }
}
