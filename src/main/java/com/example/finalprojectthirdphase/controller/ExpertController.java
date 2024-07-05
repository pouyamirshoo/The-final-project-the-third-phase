package com.example.finalprojectthirdphase.controller;

import com.example.finalprojectthirdphase.dto.*;
import com.example.finalprojectthirdphase.entity.*;
import com.example.finalprojectthirdphase.entity.enums.ExpertCondition;
import com.example.finalprojectthirdphase.entity.enums.OfferCondition;
import com.example.finalprojectthirdphase.mapper.ExpertMapper;
import com.example.finalprojectthirdphase.mapper.OfferMapper;
import com.example.finalprojectthirdphase.mapper.RequestMapper;
import com.example.finalprojectthirdphase.service.*;
import com.example.finalprojectthirdphase.validation.TakeAndCheckImage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
    public ResponseEntity<ExpertReturn> registerExpert(@Valid @RequestBody ExpertSaveRequest expertSaveRequest) {
        byte[] image = takeAndCheckImage.expertImage(expertSaveRequest.imagePath());
        Expert mappedExpert = ExpertMapper.INSTANCE.expertSaveRequestToModel(expertSaveRequest);
        mappedExpert.setExpertImage(image);
        Expert savedExpert = expertService.saveExpert(mappedExpert);
        return new ResponseEntity<>(ExpertMapper.INSTANCE.modelExpertToSaveResponse(savedExpert),
                HttpStatus.CREATED);
    }

    @GetMapping("expert_SignIn")
    public ResponseEntity<ExpertReturn> expertSignIn(@RequestParam String username, String password) {
        Expert expert = expertService.signInExpert(username, password);
        return new ResponseEntity<>(ExpertMapper.INSTANCE.modelExpertToSaveResponse(expert),
                HttpStatus.FOUND);
    }

    @GetMapping("findBy_ExpertId")
    public ResponseEntity<ExpertReturn> findByExpertId(@RequestParam int id) {
        Expert expert = expertService.findById(id);
        return new ResponseEntity<>(ExpertMapper.INSTANCE.modelExpertToSaveResponse(expert),
                HttpStatus.FOUND);
    }

    @GetMapping("findBy_ExpertCondition")
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
    public String updateExpertPassword(@RequestParam int id, String oldPassword, String newPassword, String confirmPassword) {
        Expert expert = expertService.findById(id);
        Expert updatedExpert = expertService.UpdatePassword(oldPassword, newPassword, confirmPassword, expert);
        return updatedExpert.getPassword();
    }

    @PostMapping(path = "save_Request")
    public ResponseEntity<RequestReturn> saveRequest(@Valid @RequestBody RequestSaveRequest request) {
        List<SubDuty> subDuties = request.subDuties();
        Request mappedRequest = RequestMapper.INSTANCE.requestSaveRequestToModel(request);
        mappedRequest.setSubDuties(subDuties);
        Request savedRequest = requestService.saveRequests(mappedRequest);
        return new ResponseEntity<>(RequestMapper.INSTANCE.modelRequestToSaveResponse(savedRequest),
                HttpStatus.CREATED);
    }

    @GetMapping("access_Denied")
    public boolean accessDenied(@RequestParam int id) {
        Expert expert = expertService.findById(id);
        return expertService.accessDenied(expert);
    }

    @PostMapping("save_Offer")
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
    public List<OfferReturn> findOneExpertOffers(@RequestParam int id) {
        Expert expert = expertService.findById(id);
        List<Offer> offers = offerService.findExpertOffers(expert);
        return OfferMapper.INSTANCE.listOfferToSaveResponse(offers);
    }

    @GetMapping("findOne_Order_Offers")
    public List<OfferReturn> findOneOrderOffers(@RequestParam int id) {
        Order order = orderService.findById(id);
        List<Offer> offers = offerService.findOrderOffers(order);
        return OfferMapper.INSTANCE.listOfferToSaveResponse(offers);
    }

    @GetMapping("findBy_OfferCondition")
    public List<OfferReturn> findByOrderCondition(@RequestParam OfferCondition offerCondition) {
        List<Offer> offers = offerService.findByOfferCondition(offerCondition);
        return OfferMapper.INSTANCE.listOfferToSaveResponse(offers);
    }

    @GetMapping("block_Expert")
    public ResponseEntity<ExpertReturn> blockExpert(@RequestParam int id) {
        Expert expert = expertService.findById(id);
        expert.setRate(-1);
        expertService.forceSave(expert);
        expertService.blockExpert(expert);
        return new ResponseEntity<>(ExpertMapper.INSTANCE.modelExpertToSaveResponse(expert),
                HttpStatus.FOUND);
    }

    @GetMapping("show_Offer_Rate")
    public String showOfferRate(@RequestParam int id) {
        Offer offer = offerService.findById(id);
        return commentsService.showDoneOfferRate(offer);
    }

    @DeleteMapping("delete_expert")
    public String removeExpert(@RequestParam int id) {
        expertService.removeExpert(id);
        return "expert and All relations have been deleted";
    }
}
