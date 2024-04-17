package com.ntloc.demo.customer;

import com.ntloc.demo.exception.CustomerEmailUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    CustomerService underTest;
    @Mock
    CustomerRepository customerRepository;
    @Captor
    ArgumentCaptor<Customer> customerArgumentCaptor;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerRepository);
    }

    @Test
    void shouldGetAllCustomers() {
        //given
        //when
        underTest.getCustomers();
        //then
        verify(customerRepository).findAll();
    }

    @Test
    void shouldCreateCustomer() {
        //given
        CreateCustomerRequest createCustomerRequest =
                new CreateCustomerRequest(
                        "leon",
                        "leon@gmail.com",
                        "US");
        //when
        underTest.createCustomer(createCustomerRequest);
        //then
        verify(customerRepository).save(customerArgumentCaptor.capture());
        Customer customerCaptured = customerArgumentCaptor.getValue();

        assertThat(customerCaptured.getName()).isEqualTo(createCustomerRequest.name());
        assertThat(customerCaptured.getEmail()).isEqualTo(createCustomerRequest.email());
        assertThat(customerCaptured.getAddress()).isEqualTo(createCustomerRequest.address());

    }

    @Test
    void shouldNotCreateCustomerAndThrowExceptionWhenEmailUnavailable() {
        //given
        CreateCustomerRequest createCustomerRequest =
                new CreateCustomerRequest(
                        "leon",
                        "leon@gmail.com",
                        "US");
        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.of(new Customer()));
        //when
        //then
        assertThatThrownBy(()->
                underTest.createCustomer(createCustomerRequest))
                .isInstanceOf(CustomerEmailUnavailableException.class)
                .hasMessageContaining("The email " + createCustomerRequest.email() + " unavailable.");

    }

    @Test
    @Disabled
    void updateCustomer() {
    }

    @Test
    @Disabled
    void deleteCustomer() {
    }
}