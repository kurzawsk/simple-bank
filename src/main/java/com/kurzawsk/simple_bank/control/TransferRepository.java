package com.kurzawsk.simple_bank.control;

import com.google.common.collect.Maps;
import com.kurzawsk.simple_bank.entity.dto.TransferDTO;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class TransferRepository {

    private AtomicLong idGenerator = new AtomicLong(0);
    private Map<Long, TransferDTO> transfers = Maps.newConcurrentMap();

    public TransferDTO createTransfer(TransferDTO transfer) {
        TransferDTO createdTransfer = TransferDTO.builder()
                .id(idGenerator.incrementAndGet())
                .amount(transfer.getAmount())
                .sourceAccountId(transfer.getSourceAccountId())
                .targetAccountId(transfer.getTargetAccountId())
                .timestamp(ZonedDateTime.now())
                .title(transfer.getTitle())
                .build();

        transfers.put(createdTransfer.getId(), createdTransfer);
        return createdTransfer;
    }

    public Optional<TransferDTO> find(long id) {
        return Optional.ofNullable(transfers.get(id));
    }

    public List<TransferDTO> findByAccount(long accountId) {
        return transfers.values()
                .stream()
                .filter(t -> t.getSourceAccountId() == accountId || t.getTargetAccountId() == accountId)
                .collect(Collectors.toList());
    }

    public List<TransferDTO> findBySourceAccount(long accountId) {
        return transfers.values()
                .stream()
                .filter(t -> t.getSourceAccountId() == accountId)
                .collect(Collectors.toList());
    }

    public List<TransferDTO> findByTargetAccount(long accountId) {
        return transfers.values()
                .stream()
                .filter(t -> t.getTargetAccountId() == accountId)
                .collect(Collectors.toList());
    }
}
