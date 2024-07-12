package com.example.finalprojectthirdphase.service;


import com.example.finalprojectthirdphase.entity.*;
import com.example.finalprojectthirdphase.entity.enums.OfferCondition;
import com.example.finalprojectthirdphase.entity.enums.OrderCondition;
import com.example.finalprojectthirdphase.exception.NotFoundException;
import com.example.finalprojectthirdphase.exception.WrongDateInsertException;
import com.example.finalprojectthirdphase.exception.WrongInputPriceException;
import com.example.finalprojectthirdphase.repository.OrderRepository;
import com.example.finalprojectthirdphase.validation.CreatAndValidationDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CreatAndValidationDate creatAndValidationDate;
    private final OfferService offerService;
    private final ExpertService expertService;

    public Order saveOrder(Order order, SubDuty subDuty) {
        DateTime needExpert = new DateTime(order.getNeedExpert());
        if (needExpert.isBeforeNow())
            throw new WrongDateInsertException("date can not be before today");
        if (order.getOrderPrice() < subDuty.getPrice())
            throw new WrongInputPriceException("order price can not be less than default price");
        return orderRepository.save(order);
    }

    public List<Order> findCustomerOrders(Customer customer) {
        List<Order> orders = orderRepository.findByCustomer(customer);
        if (orders.isEmpty())
            throw new NullPointerException("no order for this customer");
        return orders;
    }

    public Order findById(int id) {
        return orderRepository.findById(id).orElseThrow(() ->
                new NotFoundException("order with id " + id + " not founded"));
    }

    public List<Order> findByOrderCondition(OrderCondition orderCondition) {
        List<Order> orders = orderRepository.findByOrderCondition(orderCondition);
        if (orders.isEmpty())
            throw new NullPointerException("there is no order with " + orderCondition + " now");
        return orders;
    }

    public List<Order> findSubDutyOrders(SubDuty subDuty) {
        List<Order> orders = orderRepository.findBySubDuty(subDuty);
        if (orders.isEmpty())
            throw new NullPointerException("this subDuty do not have any order yet");
        return orders;
    }

    public Order updateOrderCondition(OrderCondition orderCondition, Order order) {
        order.setOrderCondition(orderCondition);
        return orderRepository.save(order);
    }

    public void makeOrderConditionWaitForAccept(Order order) {
        List<Offer> offers = offerService.findOrderOffers(order);
        if (!offers.isEmpty())
            updateOrderCondition(OrderCondition.WAIT_FOR_ACCEPT, order);
    }

    public Order makeOrderOngoing(Order order) {
        DateTime needExpert = new DateTime(order.getNeedExpert());
        List<Offer> offers = offerService.findByOrderAndOfferCondition(order, OfferCondition.ACCEPTED);
        if (needExpert.isAfterNow())
            throw new WrongDateInsertException("order can not be ongoing before need expert time");
        offerService.updateOfferCondition(OfferCondition.ONGOING, offers.get(0));
        return updateOrderCondition(OrderCondition.ONGOING, order);
    }

    public Order makeOrderDone(Order order) {
        DateTime needExpert = new DateTime(order.getNeedExpert());
        List<Offer> offers = offerService.findByOrderAndOfferCondition(order, OfferCondition.ONGOING);
        DateTime orderEndTime = creatAndValidationDate.creatPlusDaysDate(needExpert, offers.get(0).getTakeLong());
        if (orderEndTime.isAfterNow())
            throw new WrongDateInsertException("order can not be done before order end time");
        if (orderEndTime.isBeforeNow()) {
            Expert expert = offers.get(0).getExpert();
            int negativePoint = new DateTime(new Date(System.currentTimeMillis())).getDayOfWeek() - orderEndTime.getDayOfWeek();
            int newRate = expert.getRate() - negativePoint;
            expert.setRate(newRate);
            expertService.forceSave(expert);
        }
        offerService.updateOfferCondition(OfferCondition.DONE, offers.get(0));
        return updateOrderCondition(OrderCondition.DONE, order);
    }

    public void removeOrder(int id) {
        Order order = findById(id);
        orderRepository.delete(order);
    }

    public List<Order> findAll(Specification<Order> orderSpecification) {
        return orderRepository.findAll(orderSpecification);
    }
}
