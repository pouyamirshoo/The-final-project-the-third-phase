package com.example.finalprojectthirdphase.controller;

import com.example.finalprojectthirdphase.entity.Expert;
import com.example.finalprojectthirdphase.entity.Offer;
import com.example.finalprojectthirdphase.entity.Order;
import com.example.finalprojectthirdphase.entity.enums.OfferCondition;
import com.example.finalprojectthirdphase.entity.enums.OrderCondition;
import com.example.finalprojectthirdphase.service.ExpertService;
import com.example.finalprojectthirdphase.service.OfferService;
import com.example.finalprojectthirdphase.service.OrderService;
import com.example.finalprojectthirdphase.entity.PaymentModel;
import com.example.finalprojectthirdphase.util.recaptch.RecaptchaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


@Controller
@ResponseBody
@RequiredArgsConstructor
@Validated
public class PaymentController {

    private final OrderService orderService;
    private final ExpertService expertService;
    private final OfferService offerService;
    private final RecaptchaService validator;

    @ModelAttribute
    public PaymentModel setForm() {
        return new PaymentModel();
    }

    @GetMapping("/pay")
    public ModelAndView getPaymentModel() {
        PaymentModel paymentModel = new PaymentModel();
        ModelAndView mav = new ModelAndView("payment");
        mav.addObject("finalPayment", paymentModel);
        Order order = orderService.findById(5);
        Offer offer = offerService.findByOrderAndOfferCondition(order, OfferCondition.DONE).get(0);
        String price = Integer.toString(offer.getOfferPrice());
        String message = "you have to pay " + price + " for your order";
        mav.addObject("price", message);
        return mav;
    }

    @PostMapping("/pay")
    public ModelAndView doPayment(@ModelAttribute("finalPayment") @Valid PaymentModel paymentModel, BindingResult result,
                                  @RequestParam(name = "g-recaptcha-response")
                                  String captcha) {
        ModelAndView mav = new ModelAndView("payment");

        if (!validator.validateCaptcha(captcha)) {
            mav.addObject("message", "Please Verify Captcha");
        }

        if (result.hasErrors()) {
            new ModelAndView("error");
        }
        Order order = orderService.findById(5);
        Offer offer = offerService.findByOrderAndOfferCondition(order, OfferCondition.DONE).get(0);
        Expert expert = offer.getExpert();
        orderService.updateOrderCondition(OrderCondition.PAID, order);
        int balance = expert.getBalance();
        expert.setBalance(((offer.getOfferPrice() * 70) / 100) + balance);
        expertService.forceSave(expert);
        mav.addObject("finalPayment", result.getTarget());
        return mav;
    }
}
