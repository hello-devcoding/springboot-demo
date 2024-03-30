package com.ntloc.demo.customer;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getCustomers() {
        return customerRepository.findAll().stream()
                .filter(customer -> !customer.getIsDelete())
                .collect(Collectors.toList());
    }

    public void createCustomer(CreateCustomerRequest createCustomerRequest) {
        Optional<Customer> customerByEmail = customerRepository.findByEmail(createCustomerRequest.email());
        if (customerByEmail.isPresent()) {
            throw new RuntimeException("The email already existing");
        }
        Customer customer = Customer.create(createCustomerRequest.name(),
                createCustomerRequest.email(),
                createCustomerRequest.address());
        customerRepository.save(customer);
    }

    public void updateCustomer(Long id, String name, String email, String address) {
        Customer customer = customerRepository.findById(id).orElseThrow(() ->
                new RuntimeException(String.format("Customer with id %s was not found", id)));
        if (!Objects.equals(customer.getName(), name)) {
            customer.setName(name);
        }
        if (!Objects.equals(customer.getAddress(), address)) {
            customer.setAddress(address);
        }
        if (!Objects.equals(customer.getEmail(), email)) {
            Optional<Customer> customerByEmail = customerRepository.findByEmail(email);
            if (customerByEmail.isPresent()) {
                throw new RuntimeException("The email already existing");
            }
            customer.setEmail(email);
        }
        customerRepository.save(customer);

    }

    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() ->
                new RuntimeException(String.format("Customer with id %s was not found to delete", id)));
        customer.setIsDelete(Boolean.TRUE);
        customerRepository.save(customer);
    }

    public List<Customer> getAllCustomer() {
        return customerRepository.findAll();
    }

    public Customer getCustomerByID(Long id) {
        return customerRepository.findById(id).orElseThrow(()->
                new RuntimeException(String.format("Customer with id %s was not found", id)));
    }
}
