package com.ntloc.demo.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:16.2");
    @Autowired
    CustomerRepository underTest;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
        List<Customer> customers = List.of(Customer.create("leon", "leon@gmail.com", "US"));
        underTest.saveAll(customers);
        System.out.println("Customer: " + customers);

    }

    @Test
    void canEstablishedConnection() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @Test
    void shouldFindCustomerByEmail() {
        // Given
        // When
        Optional<Customer> customerByEmail = underTest.findByEmail("leon@gmail.com");
        // Then
        assertThat(customerByEmail).isPresent();
    }

    @Test
    void shouldNotReturnWhenFindCustomerByEmail() {
        // Given
        // When
        Optional<Customer> customerByEmail = underTest.findByEmail("leo@gmail.com");
        // Then
        assertThat(customerByEmail).isNotPresent();
    }
}