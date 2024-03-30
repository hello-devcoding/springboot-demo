package com.ntloc.demo.customer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<Customer> getCustomers() {
        return customerService.getCustomers();
    }

    @GetMapping(path = "/{id}")
    public Customer getCustomerByID(@PathVariable("id") Long id) {
        return customerService.getCustomerByID(id);
    }

    @GetMapping(path = "/get-all")
    public List<Customer> getAllCustomer() {
        return customerService.getAllCustomer();
    }

    @PostMapping
    public void createCustomer(@RequestBody CreateCustomerRequest createCustomerRequest) {
        log.info("Received create new customer {}", createCustomerRequest);
        customerService.createCustomer(createCustomerRequest);
    }

    @PutMapping(path = "/{id}")
    public void updateCustomer(@PathVariable("id") Long id,
                               @RequestParam String name,
                               @RequestParam String email,
                               @RequestParam String address) {
        customerService.updateCustomer(id, name, email, address);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }
}
