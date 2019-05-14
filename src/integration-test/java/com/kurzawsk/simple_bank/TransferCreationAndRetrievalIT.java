package com.kurzawsk.simple_bank;

import com.google.common.base.Strings;
import com.kurzawsk.simple_bank.dto.AccountDTO;
import com.kurzawsk.simple_bank.dto.ErrorDTO;
import com.kurzawsk.simple_bank.dto.TransferDTO;
import com.kurzawsk.simple_bank.dto.TransferRequestDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class TransferCreationAndRetrievalIT extends BaseRestApiIT {

    private static final String ACCOUNTS = "accounts";
    private static final String TRANSFERS = "transfers";

    @Test
    public void createTransfer_AccountsExistAndSourceAccountHasEnoughBalance_ShouldCreateTransfer() {
        BigDecimal initialAccount1Balance = new BigDecimal("100.00");
        BigDecimal initialAccount2Balance = new BigDecimal("400.00");
        BigDecimal amountToTransfer = new BigDecimal("50.00");

        String account1Location = createAccountResource(createDummyAccount(initialAccount1Balance));
        String account2Location = createAccountResource(createDummyAccount(initialAccount2Balance));

        AccountDTO retrievedAccount1 = getAccountResource(account1Location);
        AccountDTO retrievedAccount2 = getAccountResource(account2Location);

        TransferRequestDTO transfer = new TransferRequestDTO()
                .setAmount(amountToTransfer)
                .setSourceAccountId(retrievedAccount1.getId())
                .setTargetAccountId(retrievedAccount2.getId())
                .setTitle("Test money transfer");


        TransferDTO retrievedTransfer = RestApiTestHelper.createAndGetResource(TRANSFERS, transfer, TransferDTO.class, spec);


        retrievedAccount1 = getAccountResource(account1Location);
        retrievedAccount2 = getAccountResource(account2Location);

        assertThat(retrievedTransfer).isEqualToIgnoringGivenFields(transfer, "id", "timestamp");
        assertThat(retrievedTransfer.getId()).isNotNull();
        assertThat(retrievedAccount1.getBalance()).isEqualByComparingTo(initialAccount1Balance.subtract(amountToTransfer));
        assertThat(retrievedAccount2.getBalance()).isEqualByComparingTo(initialAccount2Balance.add(amountToTransfer));
    }

    @Test
    public void createTransfer_AccountsExistAndSourceAccountDoesNotHaveEnoughBalance_ShouldNotCreateTransfer() {
        AccountDTO account1 = createDummyAccount(new BigDecimal("10.00"));
        AccountDTO account2 = createDummyAccount(new BigDecimal("400.00"));

        AccountDTO retrievedAccount1 = createAndGetAccountResource(account1);
        AccountDTO retrievedAccount2 = createAndGetAccountResource(account2);

        TransferRequestDTO transfer = new TransferRequestDTO()
                .setAmount(new BigDecimal("10.01"))
                .setSourceAccountId(retrievedAccount1.getId())
                .setTargetAccountId(retrievedAccount2.getId())
                .setTitle("Test money transfer");


        ErrorDTO error = given()
                .spec(spec)
                .body(transfer)
                .when()
                .post(TRANSFERS)
                .then()
                .statusCode(400)
                .extract()
                .as(ErrorDTO.class);


        assertThat(error.getMessage()).containsExactly("Not enough resources to complete a transfer");
    }

    @Test
    public void createTransfer_TargetAccountDoesNotExist_ShouldNotCreateTransfer() {
        AccountDTO account = createDummyAccount(new BigDecimal("10.00"));
        AccountDTO retrievedAccount = createAndGetAccountResource(account);

        TransferRequestDTO transfer = new TransferRequestDTO()
                .setAmount(new BigDecimal("10.01"))
                .setSourceAccountId(retrievedAccount.getId())
                .setTargetAccountId(getNextNumber())
                .setTitle("Test money transfer");


        ErrorDTO error = given()
                .spec(spec)
                .body(transfer)
                .when()
                .post(TRANSFERS)
                .then()
                .statusCode(400)
                .extract().as(ErrorDTO.class);


        assertThat(error.getMessage()).containsExactly("Provided target account: " + transfer.getTargetAccountId() + " does not exist");
    }

    @Test
    public void createTransfer_SourceAccountDoesNotExist_ShouldNotCreateTransfer() {
        AccountDTO account = createDummyAccount(new BigDecimal("10.00"));
        AccountDTO retrievedAccount = createAndGetAccountResource(account);

        TransferRequestDTO transfer = new TransferRequestDTO()
                .setAmount(new BigDecimal("10.01"))
                .setSourceAccountId(getNextNumber())
                .setTargetAccountId(retrievedAccount.getId())
                .setTitle("Test money transfer");


        ErrorDTO error = given()
                .spec(spec)
                .body(transfer)
                .when()
                .post(TRANSFERS)
                .then()
                .statusCode(400)
                .extract().as(ErrorDTO.class);


        assertThat(error.getMessage()).containsExactly("Provided source account: " + transfer.getSourceAccountId() + " does not exist");
    }

    @Test
    public void createTransfer_SourceAndTargetAccountsDoNotExist_ShouldNotCreateTransfer() {
        TransferRequestDTO transfer = new TransferRequestDTO()
                .setAmount(new BigDecimal("10.01"))
                .setSourceAccountId(getNextNumber())
                .setTargetAccountId(getNextNumber())
                .setTitle("Test money transfer");


        ErrorDTO error = given()
                .spec(spec)
                .body(transfer)
                .when()
                .post(TRANSFERS)
                .then()
                .statusCode(400)
                .extract().as(ErrorDTO.class);


        assertThat(error.getMessage()).containsExactlyInAnyOrder("Provided source account: " + transfer.getSourceAccountId() + " does not exist",
                "Provided target account: " + transfer.getTargetAccountId() + " does not exist");
    }

    @Test
    public void createTransfer_AllTransferAttributesNull_ShouldNotCreateTransfer() {
        TransferRequestDTO transfer = new TransferRequestDTO();

        ErrorDTO error = given()
                .spec(spec)
                .body(transfer)
                .when()
                .post(TRANSFERS)
                .then()
                .statusCode(400)
                .extract().as(ErrorDTO.class);

        assertThat(error.getMessage()).containsExactlyInAnyOrder("Target account id must not be null",
                "Source account id must not be null",
                "Transfer title must not be null");
    }

    @Test
    public void createTransfer_SourceAndTargetAreTheSame_ShouldNotCreateTransfer() {
        AccountDTO account = createDummyAccount(new BigDecimal("10.00"));
        AccountDTO retrievedAccount = createAndGetAccountResource(account);

        TransferRequestDTO transfer = new TransferRequestDTO()
                .setAmount(new BigDecimal("5.44"))
                .setSourceAccountId(retrievedAccount.getId())
                .setTargetAccountId(retrievedAccount.getId())
                .setTitle("Test money transfer");


        ErrorDTO error = given()
                .spec(spec)
                .body(transfer)
                .when()
                .post(TRANSFERS)
                .then()
                .statusCode(400)
                .extract().as(ErrorDTO.class);


        assertThat(error.getMessage()).containsExactly("Cannot transfer money from/to the same account: " + retrievedAccount.getId());
    }

    @Test
    public void createTransfer_NegativeTransferAmount_ShouldNotCreateTransfer() {
        AccountDTO account1 = createDummyAccount(new BigDecimal("10.00"));
        AccountDTO account2 = createDummyAccount(new BigDecimal("400.00"));

        AccountDTO retrievedAccount1 = createAndGetAccountResource(account1);
        AccountDTO retrievedAccount2 = createAndGetAccountResource(account2);

        TransferRequestDTO transfer = new TransferRequestDTO()
                .setAmount(new BigDecimal("-0.01"))
                .setSourceAccountId(retrievedAccount1.getId())
                .setTargetAccountId(retrievedAccount2.getId())
                .setTitle("Test money transfer");


        ErrorDTO error = given()
                .spec(spec)
                .body(transfer)
                .when()
                .post(TRANSFERS)
                .then()
                .statusCode(400)
                .extract().as(ErrorDTO.class);


        assertThat(error.getMessage()).containsExactly("Please provide correct amount to transfer");
    }

    @Test
    public void createTransfer_NullTitle_ShouldNotCreateTransfer() {
        AccountDTO account1 = createDummyAccount(new BigDecimal("10.00"));
        AccountDTO account2 = createDummyAccount(new BigDecimal("400.00"));

        AccountDTO retrievedAccount1 = createAndGetAccountResource(account1);
        AccountDTO retrievedAccount2 = createAndGetAccountResource(account2);

        TransferRequestDTO transfer = new TransferRequestDTO()
                .setAmount(new BigDecimal("5.00"))
                .setSourceAccountId(retrievedAccount1.getId())
                .setTargetAccountId(retrievedAccount2.getId());


        ErrorDTO error = given()
                .spec(spec)
                .body(transfer)
                .when()
                .post(TRANSFERS)
                .then()
                .statusCode(400)
                .extract().as(ErrorDTO.class);


        assertThat(error.getMessage()).containsExactly("Transfer title must not be null");
    }

    @Test
    public void createTransfer_TitleShorterThan2_ShouldNotCreateTransfer() {
        AccountDTO account1 = createDummyAccount(new BigDecimal("10.00"));
        AccountDTO account2 = createDummyAccount(new BigDecimal("400.00"));

        AccountDTO retrievedAccount1 = createAndGetAccountResource(account1);
        AccountDTO retrievedAccount2 = createAndGetAccountResource(account2);

        TransferRequestDTO transfer = new TransferRequestDTO()
                .setAmount(new BigDecimal("5.00"))
                .setSourceAccountId(retrievedAccount1.getId())
                .setTargetAccountId(retrievedAccount2.getId())
                .setTitle("a");


        ErrorDTO error = given()
                .spec(spec)
                .body(transfer)
                .when()
                .post(TRANSFERS)
                .then()
                .statusCode(400)
                .extract().as(ErrorDTO.class);


        assertThat(error.getMessage()).containsExactly("Transfer title must not be shorter than 2 and longer than 200 characters");
    }

    @Test
    public void createTransfer_TitleLongerThan200_ShouldNotCreateTransfer() {
        AccountDTO account1 = createDummyAccount(new BigDecimal("10.00"));
        AccountDTO account2 = createDummyAccount(new BigDecimal("400.00"));

        AccountDTO retrievedAccount1 = createAndGetAccountResource(account1);
        AccountDTO retrievedAccount2 = createAndGetAccountResource(account2);

        TransferRequestDTO transfer = new TransferRequestDTO()
                .setAmount(new BigDecimal("5.00"))
                .setSourceAccountId(retrievedAccount1.getId())
                .setTargetAccountId(retrievedAccount2.getId())
                .setTitle(Strings.repeat("n", 201));


        ErrorDTO error = given()
                .spec(spec)
                .body(transfer)
                .when()
                .post(TRANSFERS)
                .then()
                .statusCode(400)
                .extract().as(ErrorDTO.class);


        assertThat(error.getMessage()).containsExactly("Transfer title must not be shorter than 2 and longer than 200 characters");
    }

    @Test
    public void createTransferWithInvalidBody_ShouldNotCreateTransfer() {
        ErrorDTO error = given()
                .spec(spec)
                .body("{ not a valid json ")
                .when()
                .post(TRANSFERS)
                .then()
                .statusCode(400).extract().as(ErrorDTO.class);

        assertThat(error.getMessage()).isNotEmpty();
    }

    @Test
    public void createThreeTransfersBetweenTwoAccounts_ShouldReturnTwoTransfersWhenFilterByIncomingAndOneWhenFilterByOutgoing() {
        AccountDTO account1 = createDummyAccount(new BigDecimal("100.00"));
        AccountDTO account2 = createDummyAccount(new BigDecimal("400.00"));

        AccountDTO retrievedAccount1 = createAndGetAccountResource(account1);
        AccountDTO retrievedAccount2 = createAndGetAccountResource(account2);

        TransferDTO outgoingTransfer1 = createAndGetTransferResource(retrievedAccount1.getId(), retrievedAccount2.getId(), new BigDecimal("50.00"), "Test money transfer");
        TransferDTO incomingTransfer1 = createAndGetTransferResource(retrievedAccount2.getId(), retrievedAccount1.getId(), new BigDecimal("12.00"), "Test money transfer");
        TransferDTO incomingTransfer2 = createAndGetTransferResource(retrievedAccount2.getId(), retrievedAccount1.getId(), new BigDecimal("12.00"), "Test money transfer");

        List<TransferDTO> incomingTransferSearchResult = given()
                .spec(spec)
                .when()
                .queryParam("type", "INCOMING")
                .get(ACCOUNTS + "/" + retrievedAccount1.getId() + "/" + TRANSFERS)
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath().getList(".", TransferDTO.class);

        List<TransferDTO> outgoingTransferSearchResult = given()
                .spec(spec)
                .when()
                .queryParam("type", "OUTGOING")
                .get(ACCOUNTS + "/" + retrievedAccount1.getId() + "/" + TRANSFERS)
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath().getList(".", TransferDTO.class);

        assertThat(incomingTransferSearchResult).containsExactlyInAnyOrder(incomingTransfer1, incomingTransfer2);
        assertThat(outgoingTransferSearchResult).containsExactly(outgoingTransfer1);
    }

    private AccountDTO createDummyAccount(BigDecimal balance) {
        return new AccountDTO()
                .setBalance(balance)
                .setNumber("ACC_" + getNextNumber())
                .setOwner("TEST USER");
    }

    private TransferDTO createAndGetTransferResource(Long sourceAccountId, Long targetAccountId, BigDecimal amount, String title) {
        TransferRequestDTO transfer = new TransferRequestDTO()
                .setAmount(amount)
                .setSourceAccountId(sourceAccountId)
                .setTargetAccountId(targetAccountId)
                .setTitle(title);
        return createAndGetTransferResource(transfer);
    }

    private TransferDTO createAndGetTransferResource(TransferRequestDTO transfer) {
        return RestApiTestHelper.createAndGetResource(TRANSFERS, transfer, TransferDTO.class, spec);
    }

    private AccountDTO createAndGetAccountResource(AccountDTO account) {
        return RestApiTestHelper.createAndGetResource(ACCOUNTS, account, AccountDTO.class, spec);
    }

    private String createAccountResource(AccountDTO account) {
        return RestApiTestHelper.createResource(ACCOUNTS, account, spec);
    }

    private AccountDTO getAccountResource(String location) {
        return RestApiTestHelper.getResource(location, AccountDTO.class, spec);
    }
}
