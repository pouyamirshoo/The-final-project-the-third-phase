package com.example.finalprojectthirdphase.repository;


import com.example.finalprojectthirdphase.entity.Customer;
import com.example.finalprojectthirdphase.entity.Order;
import com.example.finalprojectthirdphase.entity.SubDuty;
import com.example.finalprojectthirdphase.entity.enums.OrderCondition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByCustomer(Customer customer);

    List<Order> findByOrderCondition(OrderCondition orderCondition);

    List<Order> findBySubDuty(SubDuty subDuty);
}
