package com.example.finalprojectthirdphase.service;

import com.example.finalprojectthirdphase.entity.Expert;
import com.example.finalprojectthirdphase.entity.Offer;
import com.example.finalprojectthirdphase.entity.enums.ExpertCondition;
import com.example.finalprojectthirdphase.entity.enums.OfferCondition;
import com.example.finalprojectthirdphase.entity.enums.Role;
import com.example.finalprojectthirdphase.exception.DuplicateInformationException;
import com.example.finalprojectthirdphase.exception.NotFoundException;
import com.example.finalprojectthirdphase.exception.NotMatchPasswordException;
import com.example.finalprojectthirdphase.repository.ExpertRepository;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExpertService {

    private final ExpertRepository expertRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final OfferService offerService;

    public void saveExpert(Expert expert) {
        if (expertRepository.findByUsername(expert.getUsername()).isPresent()) {
            log.error("duplicate username can not insert");
            throw new DuplicateInformationException("duplicate username can not insert");
        }
        if (expertRepository.findByEmail(expert.getEmail()).isPresent()) {
            log.error("duplicate email can not insert");
            throw new DuplicateInformationException("duplicate email can not insert");
        }
        if (expertRepository.findByPhoneNumber(expert.getPhoneNumber()).isPresent()) {
            log.error("duplicate phoneNumber can not insert");
            throw new DuplicateInformationException("duplicate phoneNumber can not insert");
        }
        if (expertRepository.findByPostalCode(expert.getPostalCode()).isPresent()) {
            log.error("duplicate postalCode can not insert");
            throw new DuplicateInformationException("duplicate postalCode can not insert");
        }
        if (expertRepository.findByNationalCode(expert.getNationalCode()).isPresent()) {
            log.error("duplicate nationalCode can not insert");
            throw new DuplicateInformationException("duplicate nationalCode can not insert");
        }
        expert.setRole(Role.ROLE_EXPERT);
        expert.setPassword(passwordEncoder.encode(expert.getPassword()));

        String token = UUID.randomUUID().toString();
        expert.setVerificationToken(token);
        expertRepository.save(expert);

        String confirmationUrl = "http://localhost:8080/verify-ExpertEmail?token=" + token;
        emailService.sendEmail(expert.getEmail(), "Email Verification", "Click the link to verify your email: " + confirmationUrl);
    }

    public Expert findByToken(String token) {
        return expertRepository.findByVerificationToken(token).orElse(null);
    }

    public Expert findByUsername(String username) {
        return expertRepository.findByUsername(username).orElseThrow(() ->
                new NotFoundException("expert with id " + username + " not founded"));
    }

    public Optional<Expert> findByUsernameOP(String username) {
        return expertRepository.findByUsername(username);
    }

    public List<Expert> findSubDutyExperts(String subDutyName) {
        return expertRepository.findBySubDuties_SubDutyName(subDutyName);
    }

    public Expert signInExpert(String username, String password) {
        Expert expert = findByUsername(username);
        String enCodePassword = expert.getPassword();
        if (!passwordEncoder.matches(password, enCodePassword)) {
            throw new NotFoundException("wrong username or password");
        }
        return expert;
    }

    public Expert findById(int id) {
        return expertRepository.findById(id).orElseThrow(() ->
                new NotFoundException("expert with id " + id + " not founded"));
    }

    public Expert UpdatePassword(String oldPassword, String newPassword, String confirmPassword,
                                 Expert expert) {
        String enCodePassword = expert.getPassword();
        if (!passwordEncoder.matches(oldPassword, enCodePassword))
            throw new NotMatchPasswordException("wrong password entered");
        if (!newPassword.equals(confirmPassword))
            throw new NotMatchPasswordException("different password entered");
        expert.setPassword(passwordEncoder.encode(newPassword));
        return expertRepository.save(expert);
    }

    public Expert updateExpertCondition(ExpertCondition expertCondition, Expert expert) {
        expert.setExpertCondition(expertCondition);
        return expertRepository.save(expert);
    }

    public List<Expert> findAll(Specification<Expert> expertSpecification) {
        return expertRepository.findAll(expertSpecification);
    }

    public List<Expert> findByExpertCondition(ExpertCondition expertCondition) {
        List<Expert> experts = expertRepository.findByExpertCondition(expertCondition);
        if (experts.isEmpty())
            throw new NullPointerException();
        return experts;
    }

    public boolean accessDenied(Expert expert) {
        ExpertCondition expertCondition = expert.getExpertCondition();
        return expertCondition == ExpertCondition.ACCEPTED;
    }

    public void setRate(int rate, Expert expert) {
        expert.setRate(rate + expert.getRate());
        expertRepository.save(expert);
    }

    public void blockExpert(Expert expert) {
        int rate = expert.getRate();
        if (rate < 0)
            updateExpertCondition(ExpertCondition.BLOCKED, expert);
    }

    public Map<Integer, Integer> findAllByOfferCount() {
        List<Expert> experts = expertRepository.findAll();
        Map<Integer, Integer> offerCountById = new HashMap<>();
        for (Expert expert : experts) {
            offerCountById.put(expert.getId(), expert.getOffers().size());
        }
        return offerCountById;
    }

    public Map<Integer, Integer> findByDoneOfferCount() {
        List<Offer> offers = offerService.findByOfferCondition(OfferCondition.DONE);
        Map<Integer, Integer> offerDoneCountById = new HashMap<>();
        for (Offer offer : offers) {
            if (offerDoneCountById.containsKey(offer.getExpert().getId())) {
                offerDoneCountById.replace(offer.getExpert().getId(), offerDoneCountById.get(offer.getExpert().getId()) + 1);
            }
            offerDoneCountById.put(offer.getExpert().getId(), 1);
        }
        return offerDoneCountById;
    }

    public void removeExpert(int id) {
        Expert expert = findById(id);
        expertRepository.delete(expert);
    }

    public Expert forceSave(Expert expert) {
        return expertRepository.save(expert);
    }
}
