package com.kurzawsk.simple_bank.control;

import com.kurzawsk.simple_bank.entity.domain.Account;
import com.kurzawsk.simple_bank.entity.dto.TransferDTO;
import com.kurzawsk.simple_bank.entity.dto.TransferRequestDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.NotFoundException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransferRepository transferRepository;

    private TransferService transferService;

    @BeforeEach
    public void setUp() {
        transferService = new TransferService(accountRepository, transferRepository);
    }

    @Test
    @DisplayName("Find Transfers By Account")
    public void testFindTransfersByAccount() {
        when(transferRepository.findByAccount(anyLong()))
                .thenReturn(Collections.nCopies(3, mock(TransferDTO.class)));

        List<TransferDTO> result = transferService.findByAccount(1L);

        assertThat(result, hasSize(3));
    }

    @Test
    @DisplayName("Get Transfer By Id - Success")
    public void testGetAccountTransfersByIdSuccess() {
        long id = 1;
        when(transferRepository.find(eq(id)))
                .thenReturn(Optional.of(mock(TransferDTO.class)));

        TransferDTO result = transferService.find(id);

        assertThat(result, is(notNullValue()));
    }

    @Test
    @DisplayName("Get Transfer By Id - Failure")
    public void testGetAccountTransfersByIdFailure() {
        long id = 1;
        when(transferRepository.find(eq(id)))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> transferService.find(id));
    }

    @Test
    @DisplayName("TransferDo - Success")
    public void testTransferDoSuccess() throws InterruptedException {
        TransferRequestDTO transferRequestDTO = TransferRequestDTO.builder()
                .amount(new BigDecimal("10.00"))
                .sourceAccountId(1L)
                .targetAccountId(2L)
                .title("test")
                .build();

        Account accountSrc = new Account();
        accountSrc.setId(1L);
        accountSrc.setBalance(new BigDecimal("100.00"));
        accountSrc.setLock(new ReentrantLock());

        Account accountTgt = new Account();
        accountTgt.setId(2L);
        accountTgt.setBalance(new BigDecimal("100.00"));
        accountTgt.setLock(new ReentrantLock());

        lenient().when(accountRepository.find(eq(1L))).thenReturn(Optional.of(accountSrc));
        lenient().when(accountRepository.find(eq(2L))).thenReturn(Optional.of(accountTgt));


        transferService.transfer(transferRequestDTO);


        assertThat(accountSrc.getBalance().intValue(), is(new BigDecimal(90.00).intValue()));
        assertThat(accountTgt.getBalance().intValue(), is(new BigDecimal(110.00).intValue()));
    }

    @ParameterizedTest(name = "{index} Transfer failure - reason: {3}")
    @MethodSource("provideArgumentsForTransferFailure")
    public void testTransferDoFailure(Account accountSrc, Account accountTgt, TransferRequestDTO transferRequestDTO, String expectedFailureMessage) {
        lenient().when(accountRepository.find(eq(accountSrc.getId())))
                .thenReturn(Optional.of(accountSrc));
        lenient().when(accountRepository.find(eq(accountTgt.getId())))
                .thenReturn(Optional.of(accountTgt));

        Assertions.assertThrows(IllegalArgumentException.class, () -> transferService.transfer(transferRequestDTO));
    }

    private static Stream<Arguments> provideArgumentsForTransferFailure() {
        return Stream.of(
                Arguments.of(new Account(1L, "o1", BigDecimal.valueOf(10), "1", new ReentrantLock()),
                        new Account(2L, "o1", BigDecimal.valueOf(10), "2", new ReentrantLock()),
                        TransferRequestDTO.builder()
                                .amount(BigDecimal.valueOf(5))
                                .sourceAccountId(1L)
                                .targetAccountId(1L)
                                .build(), "The same source and target account"),
                Arguments.of(new Account(1L, "o1", BigDecimal.valueOf(10), "1", new ReentrantLock()),
                        new Account(2L, "o1", BigDecimal.valueOf(10), "2", new ReentrantLock()),
                        TransferRequestDTO.builder()
                                .amount(BigDecimal.valueOf(11))
                                .sourceAccountId(1L)
                                .targetAccountId(2L)
                                .build(), "Not enough resources to transfer"),
                Arguments.of(new Account(1L, "o1", BigDecimal.valueOf(10), "1", new ReentrantLock()),
                        new Account(2L, "o1", BigDecimal.valueOf(10), "2", new ReentrantLock()),
                        TransferRequestDTO.builder()
                                .amount(BigDecimal.valueOf(1))
                                .sourceAccountId(3L)
                                .targetAccountId(2L)
                                .build(), "Source account does not exist"),
                Arguments.of(new Account(1L, "o1", BigDecimal.valueOf(10), "1", new ReentrantLock()),
                        new Account(2L, "o1", BigDecimal.valueOf(10), "2", new ReentrantLock()),
                        TransferRequestDTO.builder()
                                .amount(BigDecimal.valueOf(1))
                                .sourceAccountId(2L)
                                .targetAccountId(4L)
                                .build(), "Target account does not exist")
        );
    }

}
