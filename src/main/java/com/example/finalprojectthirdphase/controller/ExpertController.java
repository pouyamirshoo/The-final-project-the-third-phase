package com.example.finalprojectthirdphase.controller;

import com.example.finalprojectthirdphase.dto.*;
import com.example.finalprojectthirdphase.entity.*;
import com.example.finalprojectthirdphase.entity.enums.ExpertCondition;
import com.example.finalprojectthirdphase.entity.enums.OfferCondition;
import com.example.finalprojectthirdphase.exception.NotMatchPasswordException;
import com.example.finalprojectthirdphase.mapper.ExpertMapper;
import com.example.finalprojectthirdphase.mapper.OfferMapper;
import com.example.finalprojectthirdphase.mapper.OrderMapper;
import com.example.finalprojectthirdphase.mapper.RequestMapper;
import com.example.finalprojectthirdphase.service.*;
import com.example.finalprojectthirdphase.validation.TakeAndCheckImage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class ExpertController {

    private final ExpertService expertService;
    private final TakeAndCheckImage takeAndCheckImage;
    private final RequestService requestService;
    private final SubDutyService subDutyService;
    private final OfferService offerService;
    private final OrderService orderService;
    private final CommentsService commentsService;

    @PostMapping("register_Expert")
    public ResponseEntity<?> registerExpert(@Valid @RequestBody ExpertSaveRequest expertSaveRequest) {
        String password = expertSaveRequest.password();
        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@#!%&*])[A-Za-z0-9@#!%&*]{8}$")) {
            throw new NotMatchPasswordException("password has to be 8 size and must contain at least 1 lower and upper case and 1 digit and 1 char");
        }
        byte[] image = takeAndCheckImage.expertImage(expertSaveRequest.imagePath());
        Expert mappedExpert = ExpertMapper.INSTANCE.expertSaveRequestToModel(expertSaveRequest);
        mappedExpert.setExpertImage(image);
        expertService.saveExpert(mappedExpert);
        return ResponseEntity.ok("Verify email by the link sent on your email address");
    }

    @RequestMapping(value = "/verify-ExpertEmail", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> confirmUserAccount(@RequestParam("token") String confirmationToken) {

        Expert expert = expertService.findByToken(confirmationToken);

        if (expert == null) {
            return ResponseEntity.badRequest().body("Error: Couldn't verify email");
        }
        if (expert.isEnabled()) {
            return ResponseEntity.badRequest().body("Error: already verified email");
        }
        expert.setEnabled(true);
        expert.setExpertCondition(ExpertCondition.AWAITING);
        expertService.forceSave(expert);
        return ResponseEntity.ok("Email verified successfully!////please wait until your account be accepted");
    }

    @GetMapping("expert_SignIn")
    @PreAuthorize("hasRole('ROLE_EXPERT')")
    public ResponseEntity<ExpertReturn> expertSignIn(@RequestParam String username, String password) {
        Expert expert = expertService.signInExpert(username, password);
        return new ResponseEntity<>(ExpertMapper.INSTANCE.modelExpertToSaveResponse(expert),
                HttpStatus.FOUND);
    }

    @GetMapping("findBy_ExpertCondition")
    @PreAuthorize("hasRole('ROLE_EXPERT')")
    public List<ExpertReturn> findByExpertCondition(@RequestParam ExpertCondition expertCondition) {
        List<Expert> experts = expertService.findByExpertCondition(expertCondition);
        return ExpertMapper.INSTANCE.listExpertToSaveResponse(experts);
    }

    @GetMapping("save_Expert_Image")
    public void saveExpertImage(@RequestParam int id) {
        Expert expert = expertService.findById(id);
        takeAndCheckImage.saveExpertImageToHDD(expert.getExpertImage(), expert.getFirstname(), expert.getLastname());
    }

    @GetMapping("update_Expert_Password")
    @PreAuthorize("hasRole('ROLE_EXPERT')")
    public String updateExpertPassword(@RequestParam int id, String oldPassword, String newPassword, String confirmPassword) {
        if (!newPassword.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@#!%&*])[A-Za-z0-9@#!%&*]{8}$")) {
            throw new NotMatchPasswordException("password has to be 8 size and must contain at least 1 lower and upper case and 1 digit and 1 char");
        }
        Expert expert = expertService.findById(id);
        Expert updatedExpert = expertService.UpdatePassword(oldPassword, newPassword, confirmPassword, expert);
        return "your password changed dear " + updatedExpert.getFirstname();
    }

    @PostMapping(path = "save_Request")
    @PreAuthorize("hasRole('ROLE_EXPERT')")
    public ResponseEntity<RequestReturn> saveRequest(@Valid @RequestBody RequestSaveRequest request) {
        List<SubDuty> subDuties = request.subDuties();
        Request mappedRequest = RequestMapper.INSTANCE.requestSaveRequestToModel(request);
        mappedRequest.setSubDuties(subDuties);
        Request savedRequest = requestService.saveRequests(mappedRequest);
        return new ResponseEntity<>(RequestMapper.INSTANCE.modelRequestToSaveResponse(savedRequest),
                HttpStatus.CREATED);
    }

    @GetMapping("access_Denied")
    @PreAuthorize("hasRole('ROLE_EXPERT')")
    public boolean accessDenied(@RequestParam int id) {
        Expert expert = expertService.findById(id);
        return expertService.accessDenied(expert);
    }

    @GetMapping("show_related_orders")
    @PreAuthorize("hasRole('ROLE_EXPERT')")
    public List<OrderReturn> showExpertRelatedOffers(@RequestParam int id) {
        Expert expert = expertService.findById(id);
        List<SubDuty> subDuties = expert.getSubDuties();
        List<Order> orders = new ArrayList<>();
        for (SubDuty subDuty : subDuties) {
            orders = Stream.concat(orders.stream(), subDuty.getOrders().stream()).collect(Collectors.toList());
        }
        return OrderMapper.INSTANCE.listOrderToSaveResponse(orders);
    }

    @PostMapping("save_Offer")
    @PreAuthorize("hasRole('ROLE_EXPERT')")
    public ResponseEntity<OfferReturn> saveOffer(@Valid @RequestBody OfferSaveRequest offerSaveRequest) {
        Offer mappedOffer = OfferMapper.INSTANCE.offerSaveRequestToModel(offerSaveRequest);
        Order order = orderService.findById(offerSaveRequest.order().getId());
        SubDuty subDuty = subDutyService.findById(order.getSubDuty().getId());
        Offer savedOffer = offerService.saveOffer(mappedOffer, subDuty, order);
        orderService.makeOrderConditionWaitForAccept(order);
        return new ResponseEntity<>(OfferMapper.INSTANCE.modelOfferToSaveResponse(savedOffer),
                HttpStatus.CREATED);
    }

    @GetMapping("findOne_Expert_Offers")
    @PreAuthorize("hasRole('ROLE_EXPERT')")
    public List<OfferReturn> findOneExpertOffers(@RequestParam int id) {
        Expert expert = expertService.findById(id);
        List<Offer> offers = offerService.findExpertOffers(expert);
        return OfferMapper.INSTANCE.listOfferToSaveResponse(offers);
    }

    @GetMapping("findBy_OfferCondition")
    @PreAuthorize("hasRole('ROLE_EXPERT')")
    public List<OfferReturn> findByOrderCondition(@RequestParam OfferCondition offerCondition) {
        List<Offer> offers = offerService.findByOfferCondition(offerCondition);
        return OfferMapper.INSTANCE.listOfferToSaveResponse(offers);
    }

    @GetMapping("show_Offer_Rate")
    @PreAuthorize("hasRole('ROLE_EXPERT')")
    public String showOfferRate(@RequestParam int id) {
        Offer offer = offerService.findById(id);
        return commentsService.showDoneOfferRate(offer);
    }

    @GetMapping("show_Balance")
    @PreAuthorize("hasRole('ROLE_EXPERT')")
    public String showBalance(@RequestParam int id) {
        Expert expert = expertService.findById(id);
        return Integer.toString(expert.getBalance());
    }

    @DeleteMapping("delete_expert")
    @PreAuthorize("hasRole('ROLE_EXPERT')")
    public String removeExpert(@RequestParam int id) {
        expertService.removeExpert(id);
        return "expert and All relations have been deleted";
    }
}
