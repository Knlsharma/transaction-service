package com.transaction_service.app.integration_and_unit;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.transaction_service.app.dto.AccountRequest;
import com.transaction_service.app.dto.AccountResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("local")
@Testcontainers
public class AccountTests {

    @ServiceConnection
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("routine_master")
            .withUsername("spring.datasource.username")
            .withPassword("spring.datasource.password")
            .withInitScript("create_sample_data.sql");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeAll
    static void beforeAll() {
        mysql.start();
    }


    @AfterAll
    static void afterAll() {
        mysql.stop();
    }



    AccountResponse accountResponse = new AccountResponse(1L, "12345678900");

    @Test
    void shouldReturnAccountLoadedViaSampleScript() throws Exception {

        mockMvc.perform(get("/accounts/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnAccountLoadedViaSampleScriptForParticular() throws Exception {

        mockMvc.perform(get("/accounts/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(accountResponse.accountId()))
                .andExpect(jsonPath("$.documentNumber").value(accountResponse.documentNumber()));
    }

    @Test
    void shouldReturnSecondAccountWhenValidIdProvided() throws Exception {
        mockMvc.perform(get("/accounts/2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(2))
                .andExpect(jsonPath("$.documentNumber").value("09876543211"));
    }

    @Test
    void shouldReturnThirdAccountWhenValidIdProvided() throws Exception {
        mockMvc.perform(get("/accounts/3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(3))
                .andExpect(jsonPath("$.documentNumber").value("55555555555"));
    }

    @Test
    void shouldReturn404WhenAccountNotFound() throws Exception {
        mockMvc.perform(get("/accounts/999"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenInvalidAccountIdProvided() throws Exception {
        mockMvc.perform(get("/accounts/0"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenNegativeAccountIdProvided() throws Exception {
        mockMvc.perform(get("/accounts/-1"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenNonNumericAccountIdProvided() throws Exception {
        mockMvc.perform(get("/accounts/abc"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // GET ENDS ENDPOINTS ABOVE COMPLETED

    // POST STARTING

    @Test
    void shouldCreateAccountWhenValidRequestProvided() throws Exception {
        AccountRequest newAccountRequest = new AccountRequest("11111111111");

        MvcResult result = mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAccountRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.documentNumber").value("11111111111"))
                .andExpect(jsonPath("$.accountId").isNotEmpty())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        AccountResponse createdAccount = objectMapper.readValue(responseJson, AccountResponse.class);
        System.out.println("Created Account: " + createdAccount);
    }

    @Test
    void shouldReturnStatusCode409WhenDuplicateDocumentNumberProvidedCaseOfConflict() throws Exception {
        AccountRequest duplicateRequest = new AccountRequest("12345678900"); // Already exists in test data

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andDo(print())
                .andExpect(status().isConflict());
//                .andExpect(response()).value("Document number already exists: 12345678900"));
    }

    @Test
    void shouldReturn400WhenEmptyDocumentNumberProvided() throws Exception {
        AccountRequest emptyDocumentRequest = new AccountRequest("");

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyDocumentRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenNullDocumentNumberProvided() throws Exception {
        AccountRequest nullDocumentRequest = new AccountRequest(null);

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nullDocumentRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenRequestBodyIsEmpty() throws Exception {
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenRequestBodyIsNull() throws Exception {
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn415WhenWrongContentTypeProvidedAsWrong() throws Exception {
        AccountRequest validRequest = new AccountRequest("22222222222");

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void shouldReturn400WhenMalformedJsonProvided() throws Exception {
        String malformedJson = "{documentNumber: '33333333333'"; // Missing closing brace

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void getNonExistingAccount_shouldReturnNotFound() throws Exception {
        MvcResult result = mockMvc.perform(get("/accounts/9999"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        assert result.getResponse().getContentAsString()
                .contains("Account not found");

    }


    // Integration starting :-

    @Test
    void shouldCreateAccountAndThenRetrieveIt() throws Exception {
        AccountRequest newAccountRequest = new AccountRequest("44444444444");

        MvcResult createResult = mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAccountRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        String createResponseJson = createResult.getResponse().getContentAsString();
        AccountResponse createdAccount = objectMapper.readValue(createResponseJson, AccountResponse.class);

        mockMvc.perform(get("/accounts/" + createdAccount.accountId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(createdAccount.accountId()))
                .andExpect(jsonPath("$.documentNumber").value("44444444444"));
    }

    @Test
    void shouldNotAllowCreatingMultipleAccountsWithSameDocumentNumber() throws Exception {
        String documentNumber = "55555555556";
        AccountRequest accountRequest = new AccountRequest(documentNumber);

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andDo(print())
                .andExpect(status().isCreated());


        MvcResult mvcResult = mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andDo(print())
                .andExpect(status().isConflict()).andReturn();

        assert mvcResult.getResponse().getContentAsString().contains("Document number already exists");


    }


    @Test
    void shouldValidateDocumentNumberLength() throws Exception {
        // taking small length but does not mean too small
        AccountRequest tooShortDocumentRequest = new AccountRequest("123");

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tooShortDocumentRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldValidateDocumentNumberFormat() throws Exception {
        // Test with invalid characters if you have format validation
        AccountRequest invalidFormatRequest = new AccountRequest("abcd1234567");

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidFormatRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldReturnCorrectResponseFormat() throws Exception {
        MvcResult result = mockMvc.perform(get("/accounts/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        AccountResponse response = objectMapper.readValue(responseJson, AccountResponse.class);

        assert response.accountId() != null;
        assert response.documentNumber() != null;
        assert !response.documentNumber().isEmpty();
    }

    @Test
    void shouldHandleConcurrentAccountCreation() throws Exception {
        String documentNumber = "66666666666";
        AccountRequest accountRequest = new AccountRequest(documentNumber);
        String requestJson = objectMapper.writeValueAsString(accountRequest);

        // Simulate concurrent requests
        Thread thread1 = new Thread(() -> {
            try {
                mockMvc.perform(post("/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                        .andDo(print());
            } catch (Exception e) {
                System.out.println("Exception was : " + e);
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                mockMvc.perform(post("/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                        .andDo(print());
            } catch (Exception e) {
                System.out.println("Exception was : " + e);
            }
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

    }


}
