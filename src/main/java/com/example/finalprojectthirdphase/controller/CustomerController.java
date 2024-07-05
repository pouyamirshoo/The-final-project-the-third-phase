package com.example.finalprojectthirdphase.controller;

import com.example.finalprojectthirdphase.dto.*;
import com.example.finalprojectthirdphase.entity.*;
import com.example.finalprojectthirdphase.entity.enums.OfferCondition;
import com.example.finalprojectthirdphase.entity.enums.OrderCondition;
import com.example.finalprojectthirdphase.mapper.CommentsMapper;
import com.example.finalprojectthirdphase.mapper.CustomerMapper;
import com.example.finalprojectthirdphase.mapper.OfferMapper;
import com.example.finalprojectthirdphase.mapper.OrderMapper;
import com.example.finalprojectthirdphase.service.*;
import com.example.finalprojectthirdphase.validation.CreatAndValidationDate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class CustomerController {

    private final CustomerService customerService;
    private final OrderService orderService;
    private final SubDutyService subDutyService;
    private final OfferService offerService;
    private final CommentsService commentsService;
    private final CreatAndValidationDate creatAndValidationDate;

    @PostMapping("registerCustomer")
    public ResponseEntity<CustomerReturn> registerCustomer(@Valid @RequestBody CustomerSaveRequest request) {
        Customer mappedCustomer = CustomerMapper.INSTANCE.customerSaveRequestToModel(request);
        Customer savedCustomer = customerService.saveCustomer(mappedCustomer);
        return new ResponseEntity<>(CustomerMapper.INSTANCE.modelCustomerToSaveResponse(savedCustomer),
                HttpStatus.CREATED);
    }

    @GetMapping("customer_SignIn")
    public ResponseEntity<CustomerReturn> customerSignIn(@RequestParam String username, String password) {
        Customer customer = customerService.singInCustomer(username, password);
        return new ResponseEntity<>(CustomerMapper.INSTANCE.modelCustomerToSaveResponse(customer),
                HttpStatus.FOUND);
    }

    @GetMapping("find_By_CustomerId")
    public ResponseEntity<CustomerReturn> findByCustomerId(@RequestParam int id) {
        Customer customer = customerService.findById(id);
        return new ResponseEntity<>(CustomerMapper.INSTANCE.modelCustomerToSaveResponse(customer),
                HttpStatus.FOUND);
    }

    @GetMapping("update_Customer_Password")
    public String updateCustomerPassword(@RequestParam int id, String oldPassword, String newPassword, String confirmPassword) {
        Customer customer = customerService.findById(id);
        Customer updatedCustomer = customerService.UpdatePassword(oldPassword, newPassword, confirmPassword, customer);
        return updatedCustomer.getPassword();
    }

    @PostMapping("make_Order")
    public ResponseEntity<OrderReturn> makeOrder(@Valid @RequestBody OrderSaveRequest orderSaveRequest) {
        Date creatDate = creatAndValidationDate.currentTime().toDate();
        Date needExpert = creatAndValidationDate.insertDate(orderSaveRequest.needExpertDate()).toDate();
        Order mappedOrder = OrderMapper.INSTANCE.orderSaveRequestToModel(orderSaveRequest);
        mappedOrder.setDateCreatOrder(creatDate);
        mappedOrder.setNeedExpert(needExpert);
        SubDuty subDuty = subDutyService.findById(orderSaveRequest.subDuty().getId());
        Order savedOrder = orderService.saveOrder(mappedOrder, subDuty);
        return new ResponseEntity<>(OrderMapper.INSTANCE.modelOrderToSaveResponse(savedOrder),
                HttpStatus.CREATED);
    }

    @GetMapping("allOrders_OfOneCustomer")
    public List<OrderReturn> allOrdersOfOneCustomer(@RequestParam int id) {
        Customer customer = customerService.findById(id);
        List<Order> orders = orderService.findCustomerOrders(customer);
        return OrderMapper.INSTANCE.listOrderToSaveResponse(orders);
    }

    @GetMapping("find_By_OrderId")
    public ResponseEntity<OrderReturn> findByOrderId(@RequestParam int id) {
        Order order = orderService.findById(id);
        return new ResponseEntity<>(OrderMapper.INSTANCE.modelOrderToSaveResponse(order),
                HttpStatus.FOUND);
    }

    @GetMapping("findBy_OrderCondition")
    public List<OrderReturn> findByOrderCondition(@RequestParam OrderCondition orderCondition) {
        List<Order> orders = orderService.findByOrderCondition(orderCondition);
        return OrderMapper.INSTANCE.listOrderToSaveResponse(orders);
    }

    @GetMapping("findBy_SubDutyOrders")
    public List<OrderReturn> findBySubDutyOrders(@RequestParam int id) {
        SubDuty subDuty = subDutyService.findById(id);
        List<Order> orders = orderService.findSubDutyOrders(subDuty);
        return OrderMapper.INSTANCE.listOrderToSaveResponse(orders);
    }

    @GetMapping("show_offers_ByExpertRate")
    public List<OfferReturn> showOffersByExpertRate(@RequestParam int id) {
        Order order = orderService.findById(id);
        List<Offer> offers = offerService.setOffersByExpertRate(order);
        Collections.reverse(offers);
        return OfferMapper.INSTANCE.listOfferToSaveResponse(offers);
    }

    @GetMapping("show_offers_ByPrice")
    public List<OfferReturn> showOffersByPrice(@RequestParam int id) {
        Order order = orderService.findById(id);
        List<Offer> offers = offerService.setOffersByPrice(order);
        return OfferMapper.INSTANCE.listOfferToSaveResponse(offers);
    }

    @GetMapping("accept_Offer")
    public OrderReturn acceptOffer(@RequestParam int orderId, int offerId) {
        Order order = orderService.findById(orderId);
        Offer offer = offerService.findById(offerId);
        offerService.updateOfferCondition(OfferCondition.ACCEPTED, offer);
        Order acceptedOrder = orderService.updateOrderCondition(OrderCondition.ACCEPTED, order);
        offerService.rejectOtherOffers(order);
        return OrderMapper.INSTANCE.modelOrderToSaveResponse(acceptedOrder);
    }

    @GetMapping("make_Order_OnGoing")
    public OrderReturn makeOrderOnGoing(@RequestParam int id) {
        Order order = orderService.findById(id);
        Order onGoingOrder = orderService.makeOrderOngoing(order);
        return OrderMapper.INSTANCE.modelOrderToSaveResponse(onGoingOrder);
    }

    @GetMapping("make_Order_Done")
    public OrderReturn makeOrderDone(@RequestParam int id) {
        Order order = orderService.findById(id);
        Order doneOrder = orderService.makeOrderDone(order);
        return OrderMapper.INSTANCE.modelOrderToSaveResponse(doneOrder);
    }

    @PostMapping("save_Comments")
    public ResponseEntity<CommentsReturn> saveComments(@Valid @RequestBody CommentsSaveRequest commentsSaveRequest) {
        Comments mappedComments = CommentsMapper.INSTANCE.INSTANCE.commentsSaveRequestToModel(commentsSaveRequest);
        Comments savedComments = commentsService.saveComment(mappedComments);
        commentsService.addRateToExpert(savedComments);
        return new ResponseEntity<>(CommentsMapper.INSTANCE.modelCommentsToSaveResponse(savedComments),
                HttpStatus.CREATED);
    }

    @GetMapping("add_Balance")
    public ResponseEntity<CustomerReturn> addBalance(@RequestParam int id, int price) {
        Customer customer = customerService.findById(id);
        Customer updatedCustomer = customerService.addBalance(customer, price);
        return new ResponseEntity<>(CustomerMapper.INSTANCE.modelCustomerToSaveResponse(updatedCustomer),
                HttpStatus.OK);
    }

    @GetMapping("payBy_Balance")
    public ResponseEntity<CustomerReturn> payByBalance(@RequestParam int id) {
        Order order = orderService.findById(id);
        Customer customer = customerService.payOrderPriceFromBalance(order);
        return new ResponseEntity<>(CustomerMapper.INSTANCE.modelCustomerToSaveResponse(customer),
                HttpStatus.OK);
    }

    @DeleteMapping("delete_customer")
    public String removeCustomer(@RequestParam int id) {
        customerService.removeCustomer(id);
        return "customer and All relations have been deleted";
    }

    @DeleteMapping("delete_order")
    public String removeOrder(@RequestParam int id) {
        orderService.removeOrder(id);
        return "order and All relations have been deleted";
    }
}
