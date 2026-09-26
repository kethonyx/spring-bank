package com.dimash.springbank;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BankFlowIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Test
    void protectedEndpointsRequireToken() throws Exception {
        mvc.perform(get("/accounts")).andExpect(status().isUnauthorized());
        mvc.perform(get("/accounts").header("Authorization", "Bearer garbage")).andExpect(status().isUnauthorized());
    }

    @Test
    void loginDoesNotRevealWhetherEmailExists() throws Exception {
        String email = register();
        login(email, "wrong-password").andExpect(status().isUnauthorized());
        login("nobody-" + UUID.randomUUID() + "@x.com", "password123").andExpect(status().isUnauthorized());
    }

    @Test
    void duplicateEmailIsRejected() throws Exception {
        String email = register();
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(userJson(email.toUpperCase())))
                .andExpect(status().isConflict());
    }

    @Test
    void depositAndTransferBetweenUsers() throws Exception {
        String alice = token(register());
        String bob = token(register());
        long aliceAcc = createAccount(alice, "USD");
        long bobAcc = createAccount(bob, "USD");

        deposit(alice, aliceAcc, "100.00").andExpect(status().isOk()).andExpect(jsonPath("$.balance").value(100.00));

        transfer(alice, aliceAcc, bobAcc, "30.50")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(30.50))
                .andExpect(jsonPath("$.currency").value("USD"));

        mvc.perform(get("/accounts").header("Authorization", alice))
                .andExpect(jsonPath("$[0].balance").value(69.50));
        mvc.perform(get("/accounts").header("Authorization", bob))
                .andExpect(jsonPath("$[0].balance").value(30.50));
        mvc.perform(get("/transactions").header("Authorization", bob))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    void cannotSpendFromSomeoneElsesAccount() throws Exception {
        String alice = token(register());
        String mallory = token(register());
        long aliceAcc = createAccount(alice, "USD");
        long malloryAcc = createAccount(mallory, "USD");
        deposit(alice, aliceAcc, "100.00");

        transfer(mallory, aliceAcc, malloryAcc, "100.00").andExpect(status().isNotFound());
        deposit(mallory, aliceAcc, "5.00").andExpect(status().isNotFound());

        mvc.perform(get("/accounts").header("Authorization", alice))
                .andExpect(jsonPath("$[0].balance").value(100.00));
    }

    @Test
    void transferRules() throws Exception {
        String alice = token(register());
        long usd = createAccount(alice, "USD");
        long usd2 = createAccount(alice, "USD");
        long eur = createAccount(alice, "EUR");
        deposit(alice, usd, "10.00");

        transfer(alice, usd, usd2, "10.01").andExpect(status().isUnprocessableContent());
        transfer(alice, usd, usd, "1.00").andExpect(status().isUnprocessableContent());
        transfer(alice, usd, eur, "1.00").andExpect(status().isUnprocessableContent());
        transfer(alice, usd, usd2, "-5").andExpect(status().isBadRequest());
        transfer(alice, usd, usd2, "1.001").andExpect(status().isBadRequest());
        transfer(alice, usd, 999_999L, "1.00").andExpect(status().isNotFound());
    }

    @Test
    void invalidCurrencyIsRejected() throws Exception {
        String alice = token(register());
        mvc.perform(post("/accounts").header("Authorization", alice)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"currency\":\"dollars\"}"))
                .andExpect(status().isBadRequest());
    }

    // --- helpers ---

    private static String userJson(String email) {
        return """
                {"username":"user","email":"%s","password":"password123"}""".formatted(email);
    }

    private String register() throws Exception {
        String email = "user-" + UUID.randomUUID() + "@example.com";
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(userJson(email)))
                .andExpect(status().isCreated());
        return email;
    }

    private ResultActions login(String email, String password) throws Exception {
        return mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"%s\",\"password\":\"%s\"}".formatted(email, password)));
    }

    private String token(String email) throws Exception {
        String body = login(email, "password123").andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + JsonPath.read(body, "$.token");
    }

    private long createAccount(String auth, String currency) throws Exception {
        String body = mvc.perform(post("/accounts").header("Authorization", auth)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"currency\":\"%s\"}".formatted(currency)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return ((Number) JsonPath.read(body, "$.id")).longValue();
    }

    private ResultActions deposit(String auth, long accountId, String amount) throws Exception {
        return mvc.perform(post("/accounts/deposit").header("Authorization", auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountId\":%d,\"amount\":%s}".formatted(accountId, amount)));
    }

    private ResultActions transfer(String auth, long from, long to, String amount) throws Exception {
        return mvc.perform(post("/transactions/transfer").header("Authorization", auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"senderAccountId\":%d,\"receiverAccountId\":%d,\"amount\":%s}".formatted(from, to, amount)));
    }
}
