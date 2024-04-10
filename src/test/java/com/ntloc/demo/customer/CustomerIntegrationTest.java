package com.ntloc.demo.customer;

import org.junit.jupiter.api.Test;
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
import static org.springframework.http.HttpMethod.DELETE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class CustomerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:16.2");

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    CustomerRepository customerRepository;

    @Test
    void canEstablishedConnection() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @Test
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
    void shouldFindAllCustomers() {
        //When
        ResponseEntity<Customer[]> customersResponse = testRestTemplate.getForEntity("/api/v1/customers", Customer[].class);
        //Then
        assertThat(customersResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(customersResponse.getBody()).isNotEmpty();
    }

    @Test
    void shouldDeleteCustomer() {
        //When
        ResponseEntity<Void> response = testRestTemplate.exchange("/api/v1/customers/1", DELETE, null, Void.class);
        //Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(customerRepository.existsById(1L)).isFalse();
    }

}