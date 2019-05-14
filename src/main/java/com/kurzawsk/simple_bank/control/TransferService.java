package com.kurzawsk.simple_bank.control;

import com.google.common.collect.Lists;
import com.kurzawsk.simple_bank.control.exception.MultiIllegalArgumentException;
import com.kurzawsk.simple_bank.control.exception.OperationTimeoutException;
import com.kurzawsk.simple_bank.entity.domain.Account;
import com.kurzawsk.simple_bank.entity.dto.TransferDTO;
import com.kurzawsk.simple_bank.entity.dto.TransferRequestDTO;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class TransferService {

    private static final int TRANSFER_LOCK_AWAIT_TIMEOUT = 5000;
    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;

    @Inject
    public TransferService(AccountRepository accountRepository, TransferRepository transferRepository) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
    }

    public TransferDTO transfer(TransferRequestDTO transferRequestDTO) throws InterruptedException {
        validateAccounts(transferRequestDTO.getSourceAccountId(), transferRequestDTO.getTargetAccountId());
        Account source = accountRepository.find(transferRequestDTO.getSourceAccountId()).get();
        Account target = accountRepository.find(transferRequestDTO.getTargetAccountId()).get();

        Lock lock1 = source.getId() > target.getId() ? source.getLock() : target.getLock();
        Lock lock2 = source.getId() > target.getId() ? target.getLock() : source.getLock();

        if (lock1.tryLock(TRANSFER_LOCK_AWAIT_TIMEOUT, TimeUnit.MILLISECONDS)) {
            try {
                if (lock2.tryLock(TRANSFER_LOCK_AWAIT_TIMEOUT, TimeUnit.MILLISECONDS)) {
                    try {
                        if (source.getBalance().compareTo(transferRequestDTO.getAmount()) < 0) {
                            throw new IllegalArgumentException("Not enough resources to complete a transfer");
                        }
                        return transferDo(source, target, transferRequestDTO);
                    } finally {
                        lock2.unlock();
                    }
                } else {
                    throw new OperationTimeoutException(TRANSFER_LOCK_AWAIT_TIMEOUT, TimeUnit.MILLISECONDS);
                }
            } finally {
                lock1.unlock();
            }
        } else {
            throw new OperationTimeoutException(TRANSFER_LOCK_AWAIT_TIMEOUT, TimeUnit.MILLISECONDS);
        }
    }

    public TransferDTO find(long id) {
        return transferRepository.find(id)
                .orElseThrow(() -> new NotFoundException("Transfer with id: " + id + " does not exist"));
    }

    public List<TransferDTO> findByAccount(long accountId) {
        return transferRepository.findByAccount(accountId);
    }

    public List<TransferDTO> findBySourceAccount(long accountId) {
        return transferRepository.findBySourceAccount(accountId);
    }

    public List<TransferDTO> findByTargetAccount(long accountId) {
        return transferRepository.findByTargetAccount(accountId);
    }

    private void validateAccounts(long sourceId, long targetId) {
        List<String> messages = Lists.newArrayList();
        if (!accountRepository.find(sourceId).isPresent()) {
            messages.add("Provided source account: " + sourceId + " does not exist");
        }
        if (!accountRepository.find(targetId).isPresent()) {
            messages.add("Provided target account: " + targetId + " does not exist");
        }
        if (sourceId == targetId) {
            messages.add("Cannot transfer money from/to the same account: " + sourceId);
        }
        if (!messages.isEmpty()) {
            throw new MultiIllegalArgumentException(messages.toArray(new String[messages.size()]));
        }
    }

    private TransferDTO transferDo(Account source, Account target, TransferRequestDTO transferRequestDTO) {
        source.setBalance(source.getBalance().subtract(transferRequestDTO.getAmount()));
        target.setBalance(target.getBalance().add(transferRequestDTO.getAmount()));

        TransferDTO transfer = TransferDTO.builder()
                .sourceAccountId(source.getId())
                .targetAccountId(target.getId())
                .title(transferRequestDTO.getTitle())
                .amount(transferRequestDTO.getAmount())
                .build();

        return transferRepository.createTransfer(transfer);
    }
}
