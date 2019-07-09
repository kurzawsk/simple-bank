package com.kurzawsk.simple_bank;

import com.google.common.base.Strings;
import com.kurzawsk.simple_bank.dto.AccountDTO;
import com.kurzawsk.simple_bank.dto.ErrorDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class AccountCreationAndRetrievalIT extends BaseRestApiIT {

    private static final String ACCOUNTS = "accounts";

    @Test
    public void createAccount_AccountNumberIsUnique_ShouldCreateAccount() {
        AccountDTO account = createDummyAccount();

        AccountDTO retrievedAccount = RestApiTestHelper.createAndGetResource(ACCOUNTS, account, AccountDTO.class, spec);

        assertThat(retrievedAccount).isEqualToIgnoringGivenFields(account, "id");
        assertThat(retrievedAccount.getId()).isNotNull();
    }

    @Test
    public void createAccount_AccountNumberIsNotUnique_ShouldNotCreateAccountAndGiveAReason() {
        AccountDTO account = createDummyAccount();
        String number = account.getNumber();
        createAccountResource(account);

        ErrorDTO error = given(spec)
                .body(account)
                .when()
                .post(ACCOUNTS)
                .then()
                .statusCode(409)
                .extract().as(ErrorDTO.class);

        assertThat(error.getMessage()).isNotEmpty();
        assertThat(error.getMessage()[0]).isEqualTo("Account with number: " + number + " already exists");
    }

    @Test
    public void findAccountByNumber_AccountExists_ShouldReturnAccountWithAGivenNumber() {
        AccountDTO account = createDummyAccount();
        String number = account.getNumber();
        createAccountResource(account);

        AccountDTO retrievedAccount = given()
                .spec(spec)
                .when()
                .queryParam("number", number)
                .get(ACCOUNTS).then()
                .statusCode(200)
                .extract().as(AccountDTO.class);

        assertThat(retrievedAccount).isEqualToIgnoringGivenFields(account, "id");
    }

    @Test
    public void findAccountByNumber_AccountDoesNotExist_ShouldReturnEmptyResult() {
        String nonExistingAccountNumber = getNextAccountNumber();
        AccountDTO account = createDummyAccount();
        createAccountResource(account);

        ErrorDTO error = given()
                .spec(spec)
                .when()
                .queryParam("number", nonExistingAccountNumber)
                .get(ACCOUNTS)
                .then()
                .statusCode(404)
                .extract().as(ErrorDTO.class);

        assertThat(error.getMessage()).isNotEmpty();
        assertThat(error.getMessage()[0]).isEqualTo("Account with number: " + nonExistingAccountNumber + " does not exist");
    }

    @Test
    public void createAccountWithNoData_ShouldNotCreateAccountAndGiveAReason() {
        AccountDTO account = new AccountDTO();

        ErrorDTO error = given()
                .spec(spec)
                .body(account)
                .when()
                .post(ACCOUNTS)
                .then()
                .statusCode(400).extract().as(ErrorDTO.class);

        assertThat(error.getMessage()).isNotEmpty();
        assertThat(error.getMessage()).containsExactlyInAnyOrder("Account owner name must not be null", "Account number must not be null", "Account balance must not be null");
    }

    @Test
    public void createAccountWithOnlyBalanceAttribute_ShouldNotCreateAccountAndGiveAReason() {
        AccountDTO account = new AccountDTO()
                .setBalance(new BigDecimal("100.00"));

        ErrorDTO error = given()
                .spec(spec)
                .body(account)
                .when()
                .post(ACCOUNTS)
                .then()
                .statusCode(400).extract().as(ErrorDTO.class);

        assertThat(error.getMessage()).isNotEmpty();
        assertThat(error.getMessage()).containsExactlyInAnyOrder("Account owner name must not be null", "Account number must not be null");
    }

    @Test
    public void createAccountWithOnlyOwnerAttribute_ShouldNotCreateAccountAndGiveAReason() {
        AccountDTO account = new AccountDTO()
                .setOwner("TEST");

        ErrorDTO error = given()
                .spec(spec)
                .body(account)
                .when()
                .post(ACCOUNTS)
                .then()
                .statusCode(400).extract().as(ErrorDTO.class);

        assertThat(error.getMessage()).isNotEmpty();
        assertThat(error.getMessage()).containsExactlyInAnyOrder("Account balance must not be null", "Account number must not be null");
    }

    @Test
    public void createAccountWithOnlyNumberAttribute_ShouldNotCreateAccountAndGiveAReason() {
        AccountDTO account = new AccountDTO()
                .setNumber(getNextAccountNumber());

        ErrorDTO error = given()
                .spec(spec)
                .body(account)
                .when()
                .post(ACCOUNTS)
                .then()
                .statusCode(400).extract().as(ErrorDTO.class);

        assertThat(error.getMessage()).isNotEmpty();
        assertThat(error.getMessage()).containsExactlyInAnyOrder("Account balance must not be null", "Account owner name must not be null");
    }

    @Test
    public void createAccountWithBalanceMissing_ShouldNotCreateAccountAndGiveAReason() {
        AccountDTO account = new AccountDTO()
                .setNumber(getNextAccountNumber())
                .setOwner("TEST");

        ErrorDTO error = given()
                .spec(spec)
                .body(account)
                .when()
                .post(ACCOUNTS)
                .then()
                .statusCode(400).extract().as(ErrorDTO.class);

        assertThat(error.getMessage()).isNotEmpty();
        assertThat(error.getMessage()).containsExactlyInAnyOrder("Account balance must not be null");
    }

    @Test
    public void createAccountWithOwnerMissing_ShouldNotCreateAccountAndGiveAReason() {
        AccountDTO account = new AccountDTO()
                .setNumber(getNextAccountNumber())
                .setBalance(new BigDecimal("100.00"));

        ErrorDTO error = given()
                .spec(spec)
                .body(account)
                .when()
                .post(ACCOUNTS)
                .then()
                .statusCode(400).extract().as(ErrorDTO.class);

        assertThat(error.getMessage()).isNotEmpty();
        assertThat(error.getMessage()).containsExactlyInAnyOrder("Account owner name must not be null");
    }

    @Test
    public void createAccountWithNumberMissing_ShouldNotCreateAccountAndGiveAReason() {
        AccountDTO account = new AccountDTO()
                .setOwner("TEST")
                .setBalance(new BigDecimal("100.00"));

        ErrorDTO error = given()
                .spec(spec)
                .body(account)
                .when()
                .post(ACCOUNTS)
                .then()
                .statusCode(400).extract().as(ErrorDTO.class);

        assertThat(error.getMessage()).isNotEmpty();
        assertThat(error.getMessage()).containsExactlyInAnyOrder("Account number must not be null");
    }

    @Test
    public void createAccountWithNumberTooShort_ShouldNotCreateAccountAndGiveAReason() {
        AccountDTO account = new AccountDTO()
                .setOwner("TEST")
                .setNumber("n")
                .setBalance(new BigDecimal("100.00"));

        ErrorDTO error = given()
                .spec(spec)
                .body(account)
                .when()
                .post(ACCOUNTS)
                .then()
                .statusCode(400).extract().as(ErrorDTO.class);

        assertThat(error.getMessage()).isNotEmpty();
        assertThat(error.getMessage()).containsExactlyInAnyOrder("Account number must not be shorter than 2 and longer than 50 characters");
    }

    @Test
    public void createAccountWithNumberTooLong_ShouldNotCreateAccountAndGiveAReason() {
        AccountDTO account = new AccountDTO()
                .setOwner("TEST")
                .setNumber(Strings.repeat("n", 51))
                .setBalance(new BigDecimal("100.00"));

        ErrorDTO error = given()
                .spec(spec)
                .body(account)
                .when()
                .post(ACCOUNTS)
                .then()
                .statusCode(400).extract().as(ErrorDTO.class);

        assertThat(error.getMessage()).isNotEmpty();
        assertThat(error.getMessage()).containsExactlyInAnyOrder("Account number must not be shorter than 2 and longer than 50 characters");
    }

    @Test
    public void createAccountWithOwnerTooShort_ShouldNotCreateAccountAndGiveAReason() {
        AccountDTO account = new AccountDTO()
                .setOwner("n")
                .setNumber(getNextAccountNumber())
                .setBalance(new BigDecimal("100.00"));

        ErrorDTO error = given()
                .spec(spec)
                .body(account)
                .when()
                .post(ACCOUNTS)
                .then()
                .statusCode(400).extract().as(ErrorDTO.class);

        assertThat(error.getMessage()).isNotEmpty();
        assertThat(error.getMessage()).containsExactlyInAnyOrder("Account owner name must not be shorter than 2 and longer than 100 characters");
    }

    @Test
    public void createAccountWithOwnerTooLong_ShouldNotCreateAccountAndGiveAReason() {
        AccountDTO account = new AccountDTO()
                .setOwner(Strings.repeat("n", 101))
                .setNumber(getNextAccountNumber())
                .setBalance(new BigDecimal("100.00"));

        ErrorDTO error = given()
                .spec(spec)
                .body(account)
                .when()
                .post(ACCOUNTS)
                .then()
                .statusCode(400).extract().as(ErrorDTO.class);

        assertThat(error.getMessage()).isNotEmpty();
        assertThat(error.getMessage()).containsExactlyInAnyOrder("Account owner name must not be shorter than 2 and longer than 100 characters");
    }

    @Test
    public void createAccountWithInvalidBody_ShouldNotCreateAccountAndGiveAReason() {
        ErrorDTO error = given()
                .spec(spec)
                .body("{ not a valid json ")
                .when()
                .post(ACCOUNTS)
                .then()
                .statusCode(400).extract().as(ErrorDTO.class);

        assertThat(error.getMessage()).isNotEmpty();
    }

    private AccountDTO createDummyAccount() {
        return new AccountDTO()
                .setBalance(new BigDecimal("100.00"))
                .setNumber(getNextAccountNumber())
                .setOwner("TEST USER");
    }

    private String createAccountResource(AccountDTO account) {
        return RestApiTestHelper.createResource(ACCOUNTS, account, spec);
    }

    private String getNextAccountNumber() {
        return "ACCOUNT_" + String.valueOf(getNextNumber());
    }
}
