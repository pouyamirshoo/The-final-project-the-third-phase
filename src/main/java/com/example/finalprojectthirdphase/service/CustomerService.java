package com.example.finalprojectthirdphase.service;

import com.example.finalprojectthirdphase.entity.*;
import com.example.finalprojectthirdphase.entity.enums.OfferCondition;
import com.example.finalprojectthirdphase.entity.enums.OrderCondition;
import com.example.finalprojectthirdphase.entity.enums.Role;
import com.example.finalprojectthirdphase.exception.DuplicateInformationException;
import com.example.finalprojectthirdphase.exception.LowBalanceException;
import com.example.finalprojectthirdphase.exception.NotFoundException;
import com.example.finalprojectthirdphase.exception.NotMatchPasswordException;
import com.example.finalprojectthirdphase.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final ExpertService expertService;
    private final OrderService orderService;
    private final OfferService offerService;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;

    public void saveCustomer(Customer customer) {
        if (customerRepository.findByUsername(customer.getUsername()).isPresent()) {
            log.error("duplicate username can not insert");
            throw new DuplicateInformationException("duplicate username can not insert");
        }
        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            log.error("duplicate email can not insert");
            throw new DuplicateInformationException("duplicate email can not insert");
        }
        if (customerRepository.findByPhoneNumber(customer.getPhoneNumber()).isPresent()) {
            log.error("duplicate phoneNumber can not insert");
            throw new DuplicateInformationException("duplicate phoneNumber can not insert");
        }
        if (customerRepository.findByPostalCode(customer.getPostalCode()).isPresent()) {
            log.error("duplicate postalCode can not insert");
            throw new DuplicateInformationException("duplicate postalCode can not insert");
        }
        customer.setRole(Role.ROLE_CUSTOMER);
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));

        String token = UUID.randomUUID().toString();
        customer.setVerificationToken(token);
        customerRepository.save(customer);

        String confirmationUrl = "http://localhost:8080/verify-email?token=" + token;
        emailService.sendEmail(customer.getEmail(), "Email Verification", "Click the link to verify your email: " + confirmationUrl);

    }

    public Customer findById(int id) {
        return customerRepository.findById(id).orElseThrow(() ->
                new NotFoundException("customer with id " + id + " not founded"));
    }

    public Customer findByToken(String token) {
        return customerRepository.findByVerificationToken(token).orElse(null);
    }

    public Customer findByUsername(String username) {
        return customerRepository.findByUsername(username).orElse(null);
    }

    public Optional<Customer> findByUsernameOP(String username) {
        return customerRepository.findByUsername(username);
    }

    public Customer singInCustomer(String username, String password) {
        Customer customer = findByUsername(username);
        String enCodePassword = customer.getPassword();
        if (!passwordEncoder.matches(password, enCodePassword)) {
            throw new NotFoundException("wrong username or password");
        }
        return customer;

    }

    public List<Customer> findAll(Specification<Customer> spec) {
        return customerRepository.findAll(spec);
    }

    public Customer UpdatePassword(String oldPassword, String newPassword, String confirmPassword,
                                   Customer customer) {
        String enCodePassword = customer.getPassword();
        if (!passwordEncoder.matches(oldPassword, enCodePassword))
            throw new NotMatchPasswordException("wrong password entered");
        if (!newPassword.equals(confirmPassword))
            throw new NotMatchPasswordException("different password entered");
        customer.setPassword(passwordEncoder.encode(newPassword));
        return customerRepository.save(customer);
    }

    public Customer addBalance(Customer customer, int amount) {
        customer.setCustomerBalance(amount);
        return customerRepository.save(customer);
    }

    public Customer payOrderPriceFromBalance(Order order) {
        Customer customer = order.getCustomer();
        Offer offer = offerService.findByOrderAndOfferCondition(order, OfferCondition.DONE).get(0);
        if (offer.getOfferPrice() > customer.getCustomerBalance())
            throw new LowBalanceException("you do not have enough balance credit");
        return balanceTransaction(customer, offer, order);
    }

    public Customer balanceTransaction(Customer customer, Offer offer, Order order) {
        customer.setCustomerBalance(customer.getCustomerBalance() - offer.getOfferPrice());
        Expert expert = offer.getExpert();
        orderService.updateOrderCondition(OrderCondition.PAID, order);
        int balance = expert.getBalance();
        expert.setBalance(((offer.getOfferPrice() * 70) / 100) + balance);
        expertService.forceSave(expert);
        return customerRepository.save(customer);
    }

    public void removeCustomer(int id) {
        Customer customer = findById(id);
        customerRepository.delete(customer);
    }

    public Map<Integer, Integer> findAllByOrderCount() {
        List<Customer> customers = customerRepository.findAll();
        Map<Integer, Integer> orderCountById = new HashMap<>();
        for (Customer customer : customers) {
            orderCountById.put(customer.getId(), customer.getOrders().size());
        }
        return orderCountById;
    }

    public Map<Integer, Integer> findByDoneOrderCount() {
        List<Order> orders = orderService.findByOrderCondition(OrderCondition.DONE);
        Map<Integer, Integer> orderDoneCountById = new HashMap<>();
        for (Order order : orders) {
            if (orderDoneCountById.containsKey(order.getCustomer().getId())) {
                orderDoneCountById.replace(order.getCustomer().getId(), orderDoneCountById.get(order.getCustomer().getId()) + 1);
            }
            orderDoneCountById.put(order.getCustomer().getId(), 1);
        }
        return orderDoneCountById;
    }

    public void forceSave(Customer customer) {
        customerRepository.save(customer);
    }
}
