package com.example.finalprojectthirdphase.repository;

import com.example.finalprojectthirdphase.entity.Expert;
import com.example.finalprojectthirdphase.entity.Offer;
import com.example.finalprojectthirdphase.entity.Order;
import com.example.finalprojectthirdphase.entity.enums.OfferCondition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfferRepository extends JpaRepository<Offer, Integer> {
    List<Offer> findByExpert(Expert expert);

    List<Offer> findByOrder(Order order);

    Optional<Offer> findByOrderAndExpert(Order order, Expert expert);

    List<Offer> findByOrderAndOfferCondition(Order order, OfferCondition offerCondition);

    List<Offer> findByOfferCondition(OfferCondition offerCondition);
}
