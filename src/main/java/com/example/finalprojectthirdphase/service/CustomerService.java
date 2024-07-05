package com.example.finalprojectthirdphase.service;

import com.example.finalprojectthirdphase.entity.Customer;
import com.example.finalprojectthirdphase.entity.Expert;
import com.example.finalprojectthirdphase.entity.Offer;
import com.example.finalprojectthirdphase.entity.Order;
import com.example.finalprojectthirdphase.entity.enums.OfferCondition;
import com.example.finalprojectthirdphase.entity.enums.OrderCondition;
import com.example.finalprojectthirdphase.exception.DuplicateInformationException;
import com.example.finalprojectthirdphase.exception.LowBalanceException;
import com.example.finalprojectthirdphase.exception.NotFoundException;
import com.example.finalprojectthirdphase.exception.NotMatchPasswordException;
import com.example.finalprojectthirdphase.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final ExpertService expertService;
    private final OrderService orderService;
    private final OfferService offerService;

    public Customer saveCustomer(Customer customer) {
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
        customerRepository.save(customer);
        return customer;
    }

    public Customer findById(int id) {
        return customerRepository.findById(id).orElseThrow(() ->
                new NotFoundException("customer with id " + id + " not founded"));
    }

    public Customer singInCustomer(String username, String password) {
        return customerRepository.findByUsernameAndPassword(username, password).orElseThrow(() ->
                new NotFoundException("wrong username or password"));
    }

    public List<Customer> findAll(Specification<Customer> spec) {
        return customerRepository.findAll(spec);
    }

    public Customer UpdatePassword(String oldPassword, String newPassword, String confirmPassword,
                                   Customer customer) {
        if (!customer.getPassword().equals(oldPassword))
            throw new NotMatchPasswordException("wrong password entered");
        if (!newPassword.equals(confirmPassword))
            throw new NotMatchPasswordException("different password entered");
        customer.setPassword(newPassword);
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
}
