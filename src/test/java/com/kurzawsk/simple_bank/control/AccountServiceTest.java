package com.kurzawsk.simple_bank.control;

import com.kurzawsk.simple_bank.entity.domain.Account;
import com.kurzawsk.simple_bank.entity.dto.AccountDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.NotFoundException;
import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountConverter accountConverter;

    @Mock
    private AccountRepository accountRepository;

    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        accountService = new AccountService(accountRepository, accountConverter);
    }

    @Test
    @DisplayName("Create Account")
    public void testCreateAccount() {
        AccountDTO accountDTO = AccountDTO.builder()
                .balance(new BigDecimal("1000.00"))
                .number("5555")
                .owner("test")
                .build();
        Account account = new Account();
        account.setOwner("test");
        account.setNumber("5555");
        account.setBalance(new BigDecimal("1000.00"));
        when(accountConverter.dtoToDomain(eq(accountDTO))).thenReturn(account);
        when(accountConverter.domainToDto(eq(account))).thenReturn(accountDTO);
        when(accountRepository.createAccount(eq(account))).thenReturn(account);

        AccountDTO createdAccount = accountService.createAccount(accountDTO);

        verify(accountRepository).createAccount(eq(account));
        assertThat(createdAccount, is(accountDTO));
    }

    @Test
    @DisplayName("Find Account By Id - Success")
    public void testFindByIdSuccess() {
        AccountDTO accountDTO = AccountDTO.builder()
                .balance(new BigDecimal("1000.00"))
                .number("5555")
                .owner("test")
                .build();
        Account account = new Account();
        account.setOwner("test");
        account.setNumber("5555");
        account.setBalance(new BigDecimal("1000.00"));
        account.setId(1L);
        when(accountRepository.find(eq(1L))).thenReturn(Optional.of(account));
        when(accountConverter.domainToDto(eq(account))).thenReturn(accountDTO);

        AccountDTO foundAccountDTO = accountService.find(1L);

        assertThat(foundAccountDTO, is(accountDTO));
    }

    @Test
    @DisplayName("Find Account By Id - Failure")
    public void testFindByIdFailure() {
        AccountDTO accountDTO = AccountDTO.builder()
                .balance(new BigDecimal("1000.00"))
                .number("5555")
                .owner("test")
                .build();
        Account account = new Account();
        account.setOwner("test");
        account.setNumber("5555");
        account.setBalance(new BigDecimal("1000.00"));
        account.setId(1L);
        lenient().when(accountRepository.find(eq(1L))).thenReturn(Optional.of(account));
        lenient().when(accountConverter.domainToDto(eq(account))).thenReturn(accountDTO);

        Assertions.assertThrows(NotFoundException.class, () -> accountService.find(2L));
    }

    @Test
    @DisplayName("Find Account By Number - Success")
    public void testFindByNumberSuccess() {
        AccountDTO accountDTO = AccountDTO.builder()
                .balance(new BigDecimal("1000.00"))
                .number("5555")
                .owner("test")
                .build();
        Account account = new Account();
        account.setOwner("test");
        account.setNumber("5555");
        account.setBalance(new BigDecimal("1000.00"));
        account.setId(1L);
        when(accountRepository.findByNumber(eq("5555"))).thenReturn(Optional.of(account));
        when(accountConverter.domainToDto(eq(account))).thenReturn(accountDTO);

        AccountDTO foundAccountDTO = accountService.findByNumber("5555");

        assertThat(foundAccountDTO, is(accountDTO));
    }

    @Test
    @DisplayName("Find Account By Number - Failure")
    public void testFindByNumberFailure() {
        AccountDTO accountDTO = AccountDTO.builder()
                .balance(new BigDecimal("1000.00"))
                .number("5555")
                .owner("test")
                .build();
        Account account = new Account();
        account.setOwner("test");
        account.setNumber("5555");
        account.setBalance(new BigDecimal("1000.00"));
        account.setId(1L);
        lenient().when(accountRepository.findByNumber(eq("5555"))).thenReturn(Optional.of(account));
        lenient().when(accountConverter.domainToDto(eq(account))).thenReturn(accountDTO);

        Assertions.assertThrows(NotFoundException.class, () -> accountService.findByNumber("5558"));
    }
}
