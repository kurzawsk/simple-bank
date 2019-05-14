package com.kurzawsk.simple_bank.control;

import com.kurzawsk.simple_bank.entity.dto.TransferDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
public class TransferRepositoryTest {

    private TransferRepository transferRepository;

    @BeforeEach
    public void setUp() {
        transferRepository = new TransferRepository();
    }

    @Test
    @DisplayName("Create Transfer - Success")
    public void testCreateTransferSuccess() {
        TransferDTO transferDTO = TransferDTO.builder()
                .amount(new BigDecimal("10.00"))
                .sourceAccountId(1L)
                .targetAccountId(2L)
                .title("test")
                .build();

        TransferDTO createdTransfer = transferRepository.createTransfer(transferDTO);

        assertThat(createdTransfer.getAmount(), is(transferDTO.getAmount()));
        assertThat(createdTransfer.getSourceAccountId(), is(transferDTO.getSourceAccountId()));
        assertThat(createdTransfer.getTargetAccountId(), is(transferDTO.getTargetAccountId()));
        assertThat(createdTransfer.getTitle(), is(transferDTO.getTitle()));
        assertThat(createdTransfer.getTimestamp(), lessThanOrEqualTo(ZonedDateTime.now()));
        assertThat(createdTransfer.getId(), is(1L));
    }

    @Test
    @DisplayName("Find Transfer By Id - Success")
    public void testFindTransferByIdSuccess() {
        TransferDTO createdTransfer = createTransfer(new BigDecimal("10.00"), 1L, 2L, "test");

        TransferDTO foundTransfer = transferRepository.find(createdTransfer.getId()).get();

        assertThat(foundTransfer.getAmount(), is(createdTransfer.getAmount()));
        assertThat(foundTransfer.getSourceAccountId(), is(createdTransfer.getSourceAccountId()));
        assertThat(foundTransfer.getTargetAccountId(), is(createdTransfer.getTargetAccountId()));
        assertThat(foundTransfer.getTitle(), is(createdTransfer.getTitle()));
        assertThat(foundTransfer.getTimestamp(), lessThanOrEqualTo(ZonedDateTime.now()));
        assertThat(foundTransfer.getId(), is(1L));
    }

    @Test
    @DisplayName("Find Transfer By Id - Failure")
    public void testFindTransferByIdFailure() {
        assertThat(transferRepository.find(100L), is(Optional.empty()));
    }

    @Test
    @DisplayName("Find Transfer By Account Id")
    public void testFindByAccountId() {
        TransferDTO createdTransfer1 = createTransfer(new BigDecimal("10.00"), 1L, 2L, "test");
        TransferDTO createdTransfer2 = createTransfer(new BigDecimal("10.00"), 1L, 2L, "test");
        TransferDTO createdTransfer3 = createTransfer(new BigDecimal("10.00"), 2L, 1L, "test");
        TransferDTO createdTransfer4 = createTransfer(new BigDecimal("10.00"), 2L, 3L, "test");
        TransferDTO createdTransfer5 = createTransfer(new BigDecimal("10.00"), 3L, 2L, "test");

        List<TransferDTO> foundTransfersRelatedToAccount1 = transferRepository.findByAccount(1L);
        List<TransferDTO> foundTransfersRelatedToAccount2 = transferRepository.findByAccount(2L);
        List<TransferDTO> foundTransfersRelatedToAccount3 = transferRepository.findByAccount(3L);

        assertThat(foundTransfersRelatedToAccount1, hasSize(3));
        assertThat(foundTransfersRelatedToAccount1, containsInAnyOrder(createdTransfer1, createdTransfer2, createdTransfer3));
        assertThat(foundTransfersRelatedToAccount2, hasSize(5));
        assertThat(foundTransfersRelatedToAccount2, containsInAnyOrder(createdTransfer1, createdTransfer2, createdTransfer3, createdTransfer4, createdTransfer5));
        assertThat(foundTransfersRelatedToAccount3, hasSize(2));
        assertThat(foundTransfersRelatedToAccount3, containsInAnyOrder(createdTransfer4, createdTransfer5));
    }

    @Test
    @DisplayName("Find Transfer By Account Source Id")
    public void testFindBySourceAccountId() {
        TransferDTO createdTransfer1 = createTransfer(new BigDecimal("10.00"), 1L, 2L, "test");
        TransferDTO createdTransfer2 = createTransfer(new BigDecimal("10.00"), 1L, 2L, "test");
        TransferDTO createdTransfer3 = createTransfer(new BigDecimal("10.00"), 2L, 1L, "test");
        TransferDTO createdTransfer4 = createTransfer(new BigDecimal("10.00"), 2L, 3L, "test");
        TransferDTO createdTransfer5 = createTransfer(new BigDecimal("10.00"), 3L, 2L, "test");

        List<TransferDTO> foundTransfersRelatedToAccount1 = transferRepository.findBySourceAccount(1L);
        List<TransferDTO> foundTransfersRelatedToAccount2 = transferRepository.findBySourceAccount(2L);
        List<TransferDTO> foundTransfersRelatedToAccount3 = transferRepository.findBySourceAccount(3L);

        assertThat(foundTransfersRelatedToAccount1, hasSize(2));
        assertThat(foundTransfersRelatedToAccount1, containsInAnyOrder(createdTransfer1, createdTransfer2));
        assertThat(foundTransfersRelatedToAccount2, hasSize(2));
        assertThat(foundTransfersRelatedToAccount2, containsInAnyOrder(createdTransfer3, createdTransfer4));
        assertThat(foundTransfersRelatedToAccount3, hasSize(1));
        assertThat(foundTransfersRelatedToAccount3, containsInAnyOrder(createdTransfer5));
    }

    @Test
    @DisplayName("Find Transfer By Target Source Id")
    public void testFindByTargetAccountId() {
        TransferDTO createdTransfer1 = createTransfer(new BigDecimal("10.00"), 1L, 2L, "test");
        TransferDTO createdTransfer2 = createTransfer(new BigDecimal("10.00"), 1L, 2L, "test");
        TransferDTO createdTransfer3 = createTransfer(new BigDecimal("10.00"), 2L, 1L, "test");
        TransferDTO createdTransfer4 = createTransfer(new BigDecimal("10.00"), 2L, 3L, "test");
        TransferDTO createdTransfer5 = createTransfer(new BigDecimal("10.00"), 3L, 2L, "test");

        List<TransferDTO> foundTransfersRelatedToAccount1 = transferRepository.findByTargetAccount(1L);
        List<TransferDTO> foundTransfersRelatedToAccount2 = transferRepository.findByTargetAccount(2L);
        List<TransferDTO> foundTransfersRelatedToAccount3 = transferRepository.findByTargetAccount(3L);

        assertThat(foundTransfersRelatedToAccount1, hasSize(1));
        assertThat(foundTransfersRelatedToAccount1, containsInAnyOrder(createdTransfer3));
        assertThat(foundTransfersRelatedToAccount2, hasSize(3));
        assertThat(foundTransfersRelatedToAccount2, containsInAnyOrder(createdTransfer1, createdTransfer2, createdTransfer5));
        assertThat(foundTransfersRelatedToAccount3, hasSize(1));
        assertThat(foundTransfersRelatedToAccount3, containsInAnyOrder(createdTransfer4));
    }


    private TransferDTO createTransfer(BigDecimal amount, long sourceAccountId, long targetAccountId, String title) {
        TransferDTO transferDTO = TransferDTO.builder()
                .amount(amount)
                .sourceAccountId(sourceAccountId)
                .targetAccountId(targetAccountId)
                .title(title)
                .build();

        return transferRepository.createTransfer(transferDTO);
    }
}
