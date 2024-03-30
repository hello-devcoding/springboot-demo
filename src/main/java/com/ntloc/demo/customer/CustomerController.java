package com.ntloc.demo.customer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/customers")
public class CustomerController {

    @GetMapping
    public List<Customer> getCustomers() {
        return List.of(new Customer(1L,"Bob","bob@gmail.com","US"));
    }

}
