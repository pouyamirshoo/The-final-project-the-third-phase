package com.example.finalprojectthirdphase.service;

import com.example.finalprojectthirdphase.entity.Expert;
import com.example.finalprojectthirdphase.entity.Offer;
import com.example.finalprojectthirdphase.entity.Order;
import com.example.finalprojectthirdphase.entity.SubDuty;
import com.example.finalprojectthirdphase.entity.enums.OfferCondition;
import com.example.finalprojectthirdphase.entity.enums.OrderCondition;
import com.example.finalprojectthirdphase.exception.DuplicateInformationException;
import com.example.finalprojectthirdphase.exception.NotFoundException;
import com.example.finalprojectthirdphase.exception.WrongConditionException;
import com.example.finalprojectthirdphase.exception.WrongInputPriceException;
import com.example.finalprojectthirdphase.repository.OfferRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Comparator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OfferService {

    private final OfferRepository offerRepository;

    public Offer saveOffer(Offer offer, SubDuty subDuty, Order order) {
        if (offer.getOfferPrice() < subDuty.getPrice())
            throw new WrongInputPriceException("offer price can not be less than default price");
        if (offerRepository.findByOrderAndExpert(offer.getOrder(), offer.getExpert()).isPresent())
            throw new DuplicateInformationException("an order is exist by this expert for this order");
        if (order.getOrderCondition() != OrderCondition.RECEIVING_OFFERS &&
                order.getOrderCondition() != OrderCondition.WAIT_FOR_ACCEPT)
            throw new WrongConditionException("can not send offer for this order");
        return offerRepository.save(offer);
    }

    public Offer findById(int id) {
        return offerRepository.findById(id).orElseThrow(() ->
                new NotFoundException("offer with id " + id + " not founded"));
    }

    public List<Offer> findExpertOffers(Expert expert) {
        List<Offer> offers = offerRepository.findByExpert(expert);
        if (offers.isEmpty())
            throw new NullPointerException("this expert has no offer");
        return offers;
    }

    public List<Offer> findOrderOffers(Order order) {
        List<Offer> offers = offerRepository.findByOrder(order);
        if (offers.isEmpty())
            throw new NullPointerException("this order has no offer");
        return offers;
    }

    public List<Offer> setOffersByExpertRate(Order order) {
        List<Offer> offers = findOrderOffers(order);
        return offers.stream().sorted(Comparator.comparing(a -> a.getExpert().getRate())).collect(Collectors.toList());
    }

    public List<Offer> setOffersByPrice(Order order) {
        List<Offer> offers = findOrderOffers(order);
        return offers.stream().sorted(Comparator.comparingInt(Offer::getOfferPrice)).collect(Collectors.toList());
    }

    public List<Offer> findByOfferCondition(OfferCondition offerCondition) {
        List<Offer> offers = offerRepository.findByOfferCondition(offerCondition);
        if (offers.isEmpty())
            throw new NullPointerException("no offer by this condition");
        return offers;
    }

    public void updateOfferCondition(OfferCondition offerCondition, Offer offer) {
        offer.setOfferCondition(offerCondition);
        offerRepository.save(offer);
    }

    public List<Offer> findByOrderAndOfferCondition(Order order, OfferCondition offerCondition) {
        List<Offer> offers = offerRepository.findByOrderAndOfferCondition(order, offerCondition);
        if (offers.isEmpty())
            throw new NullPointerException("no offer by this condition");
        return offers;
    }

    public List<Offer> findAll(Specification<Offer> offerSpecification) {
        return offerRepository.findAll(offerSpecification);
    }

    public void rejectOtherOffers(Order order) {
        List<Offer> offers = offerRepository.findByOrder(order);
        for (Offer offer : offers) {
            if (offer.getOfferCondition() != OfferCondition.ACCEPTED) {
                updateOfferCondition(OfferCondition.REJECTED, offer);
            }
        }
    }
}
