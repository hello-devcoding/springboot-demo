package com.ntloc.demo.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerIntegrationTest {

    public static final String API_CUSTOMERS_PATH = "/api/v1/customers";
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer
            = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16.2"));

    @Autowired
    TestRestTemplate testRestTemplate;

    @Test
    void canEstablishedConnection() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @Test
    @Rollback
    void shouldCreateCustomer() {
        //given
        CreateCustomerRequest request =
                new CreateCustomerRequest(
                        "name",
                        "email" + UUID.randomUUID() + "@gmail.com", //unique
                        "address"
                );
        //when
        ResponseEntity<Void> createCustomerResponse = testRestTemplate.exchange(
                API_CUSTOMERS_PATH,
                POST,
                new HttpEntity<>(request),
                Void.class);
        //then
        assertThat(createCustomerResponse.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        //get all customers request
        ResponseEntity<List<Customer>> allCustomersResponse = testRestTemplate.exchange(
                API_CUSTOMERS_PATH,
                GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(allCustomersResponse.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        Customer customerCreated = Objects.requireNonNull(allCustomersResponse.getBody())
                .stream()
                .filter(c -> c.getEmail().equals(request.email()))
                .findFirst()
                .orElseThrow();
        //comparison of customer we created with create customer request
        assertThat(customerCreated.getName()).isEqualTo(request.name());
        assertThat(customerCreated.getEmail()).isEqualTo(request.email());
        assertThat(customerCreated.getAddress()).isEqualTo(request.address());


    }

    @Test
    void shouldUpdateCustomer() {
        //given
        CreateCustomerRequest request =
                new CreateCustomerRequest(
                        "name",
                        "email" + UUID.randomUUID() + "@gmail.com", //unique
                        "address"
                );
        ResponseEntity<Void> createCustomerResponse = testRestTemplate.exchange(
                API_CUSTOMERS_PATH,
                POST,
                new HttpEntity<>(request),
                Void.class);
        assertThat(createCustomerResponse.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        //get all customers request
        ResponseEntity<List<Customer>> allCustomersResponse = testRestTemplate.exchange(
                API_CUSTOMERS_PATH,
                GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(allCustomersResponse.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        Long id = Objects.requireNonNull(allCustomersResponse.getBody()).stream()
                .filter(c -> c.getEmail().equals(request.email()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        String newEmail = "newEmail" + UUID.randomUUID() + "@gmail.com";
        //when
        testRestTemplate.exchange(
                        API_CUSTOMERS_PATH + "/" + id + "?email=" + newEmail,
                        PUT,
                        null,
                        Void.class)
                .getStatusCode().is2xxSuccessful();
        //getCustomerById
        ResponseEntity<Customer> customerByIdResponse = testRestTemplate.exchange(
                API_CUSTOMERS_PATH + "/" + id,
                GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(customerByIdResponse.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        Customer customerUpdated = Objects.requireNonNull(customerByIdResponse.getBody());

        assertThat(customerUpdated.getName()).isEqualTo(request.name());
        assertThat(customerUpdated.getEmail()).isEqualTo(newEmail);
        assertThat(customerUpdated.getAddress()).isEqualTo(request.address());


    }

    @Test
    void shouldDeleteCustomer() {
        //given
        CreateCustomerRequest request =
                new CreateCustomerRequest(
                        "name",
                        "email" + UUID.randomUUID() + "@gmail.com", //unique
                        "address"
                );
        CreateCustomerRequest request2 =
                new CreateCustomerRequest(
                        "name2",
                        "email2" + UUID.randomUUID() + "@gmail.com", //unique
                        "address2"
                );
        testRestTemplate.exchange(
                        API_CUSTOMERS_PATH,
                        POST,
                        new HttpEntity<>(request),
                        Void.class)
                .getStatusCode().is2xxSuccessful();

        testRestTemplate.exchange(
                        API_CUSTOMERS_PATH,
                        POST,
                        new HttpEntity<>(request2),
                        Void.class)
                .getStatusCode().is2xxSuccessful();

        //get all customers request
        ResponseEntity<List<Customer>> allCustomersResponse = testRestTemplate.exchange(
                API_CUSTOMERS_PATH,
                GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertThat(allCustomersResponse.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        //customer number 2
        Long id = Objects.requireNonNull(allCustomersResponse.getBody()).stream()
                .filter(c -> c.getEmail().equals(request2.email()))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        //when
        testRestTemplate.exchange(
                API_CUSTOMERS_PATH + "/" + id,
                DELETE,
                null,
                Void.class
        ).getStatusCode().is2xxSuccessful();
        //then
        // getCustomerByID to check that we deleted customer with id 2
        ResponseEntity<Customer> customerByIdResponse = testRestTemplate.exchange(
                API_CUSTOMERS_PATH + "/" + id,
                GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(customerByIdResponse.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}