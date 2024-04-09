package com.ntloc.demo.customer;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:16.2");

    @Autowired
    TestRestTemplate testRestTemplate;

    @Test
    void canEstablishedConnection() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @Test
    @Order(1)
    void shouldCreateNewCustomerWhenCustomerIsValid() {
        //Given
        CreateCustomerRequest createCustomerRequest =
                new CreateCustomerRequest("name", "email@gmail.com", "address");
        //when
        ResponseEntity<Void> response = testRestTemplate.postForEntity("/api/v1/customers", createCustomerRequest, Void.class);
        //Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(2)
    void shouldNotCreateCustomerWhenTheEmailValidationFail() {
        //Given
        CreateCustomerRequest createCustomerRequest =
                new CreateCustomerRequest("name", "email@gmail.com", "address");
        //when
        ResponseEntity<Void> response = testRestTemplate.postForEntity("/api/v1/customers", createCustomerRequest, Void.class);
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody());

    }

    @Test
    @Order(2)
    void shouldFindAllCustomers() {
        //When
        ResponseEntity<Customer[]> customersResponse = testRestTemplate.getForEntity("/api/v1/customers", Customer[].class);
        //Then
        assertThat(customersResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(customersResponse.getBody()).isNotEmpty();
    }

}