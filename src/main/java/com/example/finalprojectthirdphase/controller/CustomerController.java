package com.example.finalprojectthirdphase.controller;

import com.example.finalprojectthirdphase.dto.*;
import com.example.finalprojectthirdphase.entity.*;
import com.example.finalprojectthirdphase.entity.enums.OfferCondition;
import com.example.finalprojectthirdphase.entity.enums.OrderCondition;
import com.example.finalprojectthirdphase.exception.NotMatchPasswordException;
import com.example.finalprojectthirdphase.mapper.CommentsMapper;
import com.example.finalprojectthirdphase.mapper.CustomerMapper;
import com.example.finalprojectthirdphase.mapper.OfferMapper;
import com.example.finalprojectthirdphase.mapper.OrderMapper;
import com.example.finalprojectthirdphase.service.*;
import com.example.finalprojectthirdphase.validation.CreatAndValidationDate;
import com.example.finalprojectthirdphase.validation.TakeAndCheckImage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final TakeAndCheckImage takeAndCheckImage;
    private final CommentsService commentsService;
    private final CreatAndValidationDate creatAndValidationDate;

    @PostMapping("registerCustomer")
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody CustomerSaveRequest request) {
        String password = request.password();
        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@#!%&*])[A-Za-z0-9@#!%&*]{8}$")) {
            throw new NotMatchPasswordException("password has to be 8 size and must contain at least 1 lower and upper case and 1 digit and 1 char");
        }
        Customer mappedCustomer = CustomerMapper.INSTANCE.customerSaveRequestToModel(request);
        customerService.saveCustomer(mappedCustomer);
        return ResponseEntity.ok("Verify email by the link sent on your email address");
    }

    @RequestMapping(value = "/verify-email", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> confirmUserAccount(@RequestParam("token") String confirmationToken) {

        Customer customer = customerService.findByToken(confirmationToken);

        if (customer == null) {
            return ResponseEntity.badRequest().body("Error: Couldn't verify email");
        }
        if (customer.isEnabled()) {
            return ResponseEntity.badRequest().body("Error: already verified email");
        }
        customer.setEnabled(true);
        customerService.forceSave(customer);
        return ResponseEntity.ok("Email verified successfully!");
    }

    @GetMapping("customer_SignIn")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<CustomerReturn> customerSignIn(@RequestParam String username, String password) {
        Customer customer = customerService.singInCustomer(username, password);
        return new ResponseEntity<>(CustomerMapper.INSTANCE.modelCustomerToSaveResponse(customer),
                HttpStatus.FOUND);
    }

    @PatchMapping("update_Customer_Password")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public String updateCustomerPassword(@RequestParam int id, String oldPassword, String newPassword, String confirmPassword) {
        if (!newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@#!%&*])[A-Za-z0-9@#!%&*]{8}$")) {
            throw new NotMatchPasswordException("password has to be 8 size and must contain at least 1 lower and upper case and 1 digit and 1 char");
        }
        Customer customer = customerService.findById(id);
        Customer updatedCustomer = customerService.UpdatePassword(oldPassword, newPassword, confirmPassword, customer);
        return "your password changed dear " + updatedCustomer.getFirstname();
    }

    @GetMapping("findOne_Order_Offers")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public List<OfferReturn> findOneOrderOffers(@RequestParam int id) {
        Order order = orderService.findById(id);
        List<Offer> offers = offerService.findOrderOffers(order);
        return OfferMapper.INSTANCE.listOfferToSaveResponse(offers);
    }

    @PostMapping("make_Order")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
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
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public List<OrderReturn> allOrdersOfOneCustomer(@RequestParam int id) {
        Customer customer = customerService.findById(id);
        List<Order> orders = orderService.findCustomerOrders(customer);
        return OrderMapper.INSTANCE.listOrderToSaveResponse(orders);
    }

    @GetMapping("find_By_OrderId")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<OrderReturn> findByOrderId(@RequestParam int id) {
        Order order = orderService.findById(id);
        return new ResponseEntity<>(OrderMapper.INSTANCE.modelOrderToSaveResponse(order),
                HttpStatus.FOUND);
    }

    @GetMapping("findBy_OrderCondition")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public List<OrderReturn> findByOrderCondition(@RequestParam OrderCondition orderCondition) {
        List<Order> orders = orderService.findByOrderCondition(orderCondition);
        return OrderMapper.INSTANCE.listOrderToSaveResponse(orders);
    }

    @GetMapping("findBy_SubDutyOrders")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public List<OrderReturn> findBySubDutyOrders(@RequestParam int id) {
        SubDuty subDuty = subDutyService.findById(id);
        List<Order> orders = orderService.findSubDutyOrders(subDuty);
        return OrderMapper.INSTANCE.listOrderToSaveResponse(orders);
    }

    @GetMapping("show_offers_ByExpertRate")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public List<OfferReturn> showOffersByExpertRate(@RequestParam int id) {
        Order order = orderService.findById(id);
        List<Offer> offers = offerService.setOffersByExpertRate(order);
        Collections.reverse(offers);
        return OfferMapper.INSTANCE.listOfferToSaveResponse(offers);
    }

    @GetMapping("show_offers_ByPrice")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public List<OfferReturn> showOffersByPrice(@RequestParam int id) {
        Order order = orderService.findById(id);
        List<Offer> offers = offerService.setOffersByPrice(order);
        return OfferMapper.INSTANCE.listOfferToSaveResponse(offers);
    }

    @GetMapping("save_expert_Image")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public @ResponseBody byte[] saveExpertImage(@RequestParam int id) {
        Offer offer = offerService.findById(id);
        byte[] image = offer.getExpert().getExpertImage();
        String firstname = offer.getExpert().getFirstname();
        String lastname = offer.getExpert().getLastname();
        takeAndCheckImage.saveExpertImageToHDD(image, firstname, lastname);
        return image;
    }

    @PatchMapping("accept_Offer")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public OrderReturn acceptOffer(@RequestParam int orderId, int offerId) {
        Order order = orderService.findById(orderId);
        Offer offer = offerService.findById(offerId);
        offerService.updateOfferCondition(OfferCondition.ACCEPTED, offer);
        Order acceptedOrder = orderService.updateOrderCondition(OrderCondition.ACCEPTED, order);
        offerService.rejectOtherOffers(order);
        return OrderMapper.INSTANCE.modelOrderToSaveResponse(acceptedOrder);
    }

    @PatchMapping("make_Order_OnGoing")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public OrderReturn makeOrderOnGoing(@RequestParam int id) {
        Order order = orderService.findById(id);
        Order onGoingOrder = orderService.makeOrderOngoing(order);
        return OrderMapper.INSTANCE.modelOrderToSaveResponse(onGoingOrder);
    }

    @PatchMapping("make_Order_Done")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public OrderReturn makeOrderDone(@RequestParam int id) {
        Order order = orderService.findById(id);
        Order doneOrder = orderService.makeOrderDone(order);
        return OrderMapper.INSTANCE.modelOrderToSaveResponse(doneOrder);
    }

    @PostMapping("save_Comments")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<CommentsReturn> saveComments(@Valid @RequestBody CommentsSaveRequest commentsSaveRequest) {
        Comments mappedComments = CommentsMapper.INSTANCE.INSTANCE.commentsSaveRequestToModel(commentsSaveRequest);
        Comments savedComments = commentsService.saveComment(mappedComments);
        commentsService.addRateToExpert(savedComments);
        return new ResponseEntity<>(CommentsMapper.INSTANCE.modelCommentsToSaveResponse(savedComments),
                HttpStatus.CREATED);
    }

    @GetMapping("show_balance")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public String showCustomerBalance(@RequestParam int id) {
        Customer customer = customerService.findById(id);
        return Integer.toString(customer.getCustomerBalance());
    }

    @PatchMapping("add_Balance")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<CustomerReturn> addBalance(@RequestParam int id, int price) {
        Customer customer = customerService.findById(id);
        Customer updatedCustomer = customerService.addBalance(customer, price);
        return new ResponseEntity<>(CustomerMapper.INSTANCE.modelCustomerToSaveResponse(updatedCustomer),
                HttpStatus.OK);
    }

    @PatchMapping("payBy_Balance")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<CustomerReturn> payByBalance(@RequestParam int id) {
        Order order = orderService.findById(id);
        Customer customer = customerService.payOrderPriceFromBalance(order);
        return new ResponseEntity<>(CustomerMapper.INSTANCE.modelCustomerToSaveResponse(customer),
                HttpStatus.OK);
    }

    @DeleteMapping("delete_customer")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public String removeCustomer(@RequestParam int id) {
        customerService.removeCustomer(id);
        return "customer and All relations have been deleted";
    }

    @DeleteMapping("delete_order")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public String removeOrder(@RequestParam int id) {
        orderService.removeOrder(id);
        return "order and All relations have been deleted";
    }
}
