package com.example.finalprojectthirdphase.repository;

import com.example.finalprojectthirdphase.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;


public interface CustomerRepository extends JpaRepository<Customer, Integer>, JpaSpecificationExecutor<Customer> {

    Optional<Customer> findByUsername(String username);

    Optional<Customer> findByPhoneNumber(String phoneNumber);

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByPostalCode(String postalCode);

    Optional<Customer> findByUsernameAndPassword(String username, String password);

}
