package com.transaction_service.app.integration_and_unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transaction_service.app.dto.TransactionRequest;
import com.transaction_service.app.dto.TransactionResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("local")
@Testcontainers
public class TransactionTests {

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

    @Test
    void shouldCreateCashPurchaseTransaction() throws Exception {
        TransactionRequest request = new TransactionRequest(1L, 1, new BigDecimal("25.50"));

        MvcResult result = mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.operationTypeId").value(1))
                .andExpect(jsonPath("$.amount").value(-25.50))
                .andExpect(jsonPath("$.transactionId").isNotEmpty())
                .andExpect(jsonPath("$.eventDate").isNotEmpty())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        TransactionResponse response = objectMapper.readValue(responseJson, TransactionResponse.class);
        System.out.println("Created CASH_PURCHASE Transaction: " + response);
    }

    @Test
    void shouldCreateWithdrawalTransaction() throws Exception {
        TransactionRequest request = new TransactionRequest(3L, 3, new BigDecimal("50.00"));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value(3))
                .andExpect(jsonPath("$.operationTypeId").value(3))
                .andExpect(jsonPath("$.amount").value(-50.00))
                .andExpect(jsonPath("$.transactionId").isNotEmpty());
    }

    @Test
    void createTransactionForNonExistingAccountThenShouldReturnNotFound() throws Exception {
        TransactionRequest request = new TransactionRequest(999L, 1, new BigDecimal("50.00"));

        MvcResult result = mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        assert result.getResponse().getContentAsString().contains("Account not found");
    }


    @Test
    void shouldCreateInstallmentPurchaseTransaction() throws Exception {
        TransactionRequest request = new TransactionRequest(2L, 2, new BigDecimal("100.75"));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value(2))
                .andExpect(jsonPath("$.operationTypeId").value(2))
                .andExpect(jsonPath("$.amount").value(-100.75))
                .andExpect(jsonPath("$.transactionId").isNotEmpty());
    }

    @Test
    void shouldCreatePaymentTransaction() throws Exception {
        TransactionRequest request = new TransactionRequest(1L, 4, new BigDecimal("200.00"));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.operationTypeId").value(4))
                .andExpect(jsonPath("$.amount").value(200.00)) // Should be positive for payments
                .andExpect(jsonPath("$.transactionId").isNotEmpty());
    }

    @Test
    void shouldReturn400WhenAccountIdIsNull() throws Exception {
        TransactionRequest request = new TransactionRequest(null, 1, new BigDecimal("50.00"));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenOperationTypeIdIs0() throws Exception {
        TransactionRequest request = new TransactionRequest(1L, 0, new BigDecimal("50.00"));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenAmountIsNull() throws Exception {
        TransactionRequest request = new TransactionRequest(1L, 1, null);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenAmountIsZero() throws Exception {
        TransactionRequest request = new TransactionRequest(1L, 1, BigDecimal.ZERO);

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenAmountIsNegative() throws Exception {
        TransactionRequest request = new TransactionRequest(1L, 1, new BigDecimal("-50.00"));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenInvalidOperationTypeProvided() throws Exception {
        TransactionRequest request = new TransactionRequest(1L, 99, new BigDecimal("50.00")); // Invalid operation type

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenAccountDoesNotExist() throws Exception {
        TransactionRequest request = new TransactionRequest(999L, 1, new BigDecimal("50.00"));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenRequestBodyIsEmpty() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenRequestBodyIsNull() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn415WhenWrongContentTypeProvided() throws Exception {
        TransactionRequest request = new TransactionRequest(1L, 1, new BigDecimal("50.00"));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void shouldReturn400WhenMalformedJsonProvided() throws Exception {
        String malformedJson = "{accountId: 1, operationTypeId: 1";

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateTransactionWithVeryLargeAmount() throws Exception {
        TransactionRequest request = new TransactionRequest(1L, 4, new BigDecimal("99999999.99"));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(new BigDecimal("99999999.99")));
    }

    @Test
    void shouldCreateTransactionWithVerySmallAmount() throws Exception {
        TransactionRequest request = new TransactionRequest(1L, 1, new BigDecimal("0.01"));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value( new BigDecimal("-0.01")));
    }


    @Test
    void shouldCreateMultipleTransactionsForSameAccount() throws Exception {
        Long accountId = 1L;

        // Create first transaction
        TransactionRequest request1 = new TransactionRequest(accountId, 1, new BigDecimal("50.00"));
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andDo(print())
                .andExpect(status().isCreated());

        // Create second transaction
        TransactionRequest request2 = new TransactionRequest(accountId, 4, new BigDecimal("100.00"));
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void shouldCreateTransactionsForDifferentAccounts() throws Exception {
        // Transaction for account 1
        TransactionRequest request1 = new TransactionRequest(1L, 1, new BigDecimal("25.00"));
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andDo(print())
                .andExpect(status().isCreated());

        // Transaction for account 2
        TransactionRequest request2 = new TransactionRequest(2L, 2, new BigDecimal("75.00"));
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andDo(print())
                .andExpect(status().isCreated());

        // Transaction for account 3
        TransactionRequest request3 = new TransactionRequest(3L, 4, new BigDecimal("150.00"));
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request3)))
                .andDo(print())
                .andExpect(status().isCreated());
    }


    @Test
    void shouldReturnCorrectResponseFormatForTransaction() throws Exception {
        TransactionRequest request = new TransactionRequest(1L, 1, new BigDecimal("30.00"));

        MvcResult result = mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        TransactionResponse response = objectMapper.readValue(responseJson, TransactionResponse.class);

        assert response.transactionId() != null;
        assert response.accountId() != null;
        assert response.operationTypeId() != 0;
        assert response.amount() != null;
        assert response.eventDate() != null;

        assert response.accountId().equals(1L);
        assert response.operationTypeId() == 1;
        assert response.amount().equals(new BigDecimal("-30.00"));
    }


    @Test
    void shouldValidateAllOperationTypes() throws Exception {
        Long accountId = 1L;
        BigDecimal amount = new BigDecimal("50.00");

        // Test all valid operation types
        for (int operationType = 1; operationType <= 4; operationType++) {
            TransactionRequest request = new TransactionRequest(accountId, operationType, amount);

            mockMvc.perform(post("/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.operationTypeId").value(operationType));
        }
    }

    @Test
    void shouldRejectInvalidOperationTypes() throws Exception {
        Long accountId = 1L;
        BigDecimal amount = new BigDecimal("50.00");

        // Test invalid operation types
        int[] invalidOperationTypes = {0, -1, 5, 10, 100};

        for (int operationType : invalidOperationTypes) {
            TransactionRequest request = new TransactionRequest(accountId, operationType, amount);

            mockMvc.perform(post("/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }


    @Test
    void shouldHandleConcurrentTransactionCreation() throws Exception {
        TransactionRequest request = new TransactionRequest(1L, 1, new BigDecimal("25.00"));
        String requestJson = objectMapper.writeValueAsString(request);

        // Simulate concurrent requests
        Thread thread1 = new Thread(() -> {
            try {
                mockMvc.perform(post("/transactions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                        .andDo(print())
                        .andExpect(status().isCreated());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                mockMvc.perform(post("/transactions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                        .andDo(print())
                        .andExpect(status().isCreated());
            } catch (Exception e) {
                System.out.println("Exception was : " + e);
            }
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

    }


    @Test
    void shouldCreateTransactionForExistingAccounts() throws Exception {
        // Test transaction creation for all accounts from test data
        Long[] accountIds = {1L, 2L, 3L};

        for (Long accountId : accountIds) {
            TransactionRequest request = new TransactionRequest(accountId, 1, new BigDecimal("15.00"));

            mockMvc.perform(post("/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.accountId").value(accountId));
        }
    }

    @Test
    void createTransactionWithInvalidOperationTypeThenShouldReturnBadRequest() throws Exception {
        TransactionRequest request = new TransactionRequest(1L, 5, new BigDecimal("50.00"));

        MvcResult result = mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        assert result.getResponse().getContentAsString().contains("VALIDATION_ERROR");
    }


    @Test
    void createCashPurchaseWithPositiveAmountThenShouldConvertToNegative() throws Exception {
        TransactionRequest request = new TransactionRequest(1L, 1, new BigDecimal("50.00"));

        MvcResult result = mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        TransactionResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                TransactionResponse.class
        );

        assert response.amount().compareTo(BigDecimal.ZERO) < 0;
    }

    @Test
    void createPaymentWithPositiveAmountThenShouldRemainPositive() throws Exception {
        TransactionRequest request = new TransactionRequest(1L, 4, new BigDecimal("100.00"));

        MvcResult result = mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        TransactionResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                TransactionResponse.class
        );

        // Should be positive for payment operations
        assert response.amount().compareTo(BigDecimal.ZERO) > 0;
    }









}
