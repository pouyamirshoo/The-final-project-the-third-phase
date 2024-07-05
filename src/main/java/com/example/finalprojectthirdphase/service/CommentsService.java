package com.example.finalprojectthirdphase.service;


import com.example.finalprojectthirdphase.entity.Comments;
import com.example.finalprojectthirdphase.entity.Expert;
import com.example.finalprojectthirdphase.entity.Offer;
import com.example.finalprojectthirdphase.entity.Order;
import com.example.finalprojectthirdphase.entity.enums.OfferCondition;
import com.example.finalprojectthirdphase.exception.NotFoundException;
import com.example.finalprojectthirdphase.exception.WrongConditionException;
import com.example.finalprojectthirdphase.repository.CommentsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class CommentsService {

    private final CommentsRepository commentsRepository;
    private final OfferService offerService;
    private final ExpertService expertService;

    public Comments saveComment(Comments comments) {
        return commentsRepository.save(comments);
    }

    public Comments findById(int id) {
        return commentsRepository.findById(id).orElseThrow(() ->
                new NotFoundException("comment with id " + id + " not founded"));
    }

    public Comments findByOrder(Order order) {
        return commentsRepository.findByOrder(order).orElseThrow(() ->
                new NotFoundException("this order do not have comments"));
    }

    public String showDoneOfferRate(Offer offer) {
        if (offer.getOfferCondition() != OfferCondition.DONE)
            throw new WrongConditionException("offers can not take comments before they done");
        Comments comments = findByOrder(offer.getOrder());
        return "customer rate you " + comments.getRate() + " for your job";
    }

    public void addRateToExpert(Comments comments) {
        int rate = comments.getRate();
        List<Offer> offers = offerService.findByOrderAndOfferCondition(comments.getOrder(), OfferCondition.DONE);
        Expert expert = offers.get(0).getExpert();
        expertService.setRate(rate, expert);
    }
}
