package com.example.finalprojectthirdphase.service;

import com.example.finalprojectthirdphase.entity.Expert;
import com.example.finalprojectthirdphase.entity.enums.ExpertCondition;
import com.example.finalprojectthirdphase.exception.DuplicateInformationException;
import com.example.finalprojectthirdphase.exception.NotFoundException;
import com.example.finalprojectthirdphase.exception.NotMatchPasswordException;
import com.example.finalprojectthirdphase.repository.ExpertRepository;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExpertService {

    private final ExpertRepository expertRepository;

    public Expert saveExpert(Expert expert) {
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
        return expertRepository.save(expert);
    }


    public Expert signInExpert(String username, String password) {
        return expertRepository.findByUsernameAndPassword(username, password).orElseThrow(() ->
                new NotFoundException("wrong username or password"));
    }


    public Expert findById(int id) {
        return expertRepository.findById(id).orElseThrow(() ->
                new NotFoundException("expert with id " + id + " not founded"));
    }

    public Expert UpdatePassword(String oldPassword, String newPassword, String confirmPassword,
                                 Expert expert) {
        if (!expert.getPassword().equals(oldPassword))
            throw new NotMatchPasswordException("wrong password entered");
        if (!newPassword.equals(confirmPassword))
            throw new NotMatchPasswordException("different password entered");
        expert.setPassword(newPassword);
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
        expert.setRate(rate);
        expertRepository.save(expert);
    }

    public void blockExpert(Expert expert) {
        int rate = expert.getRate();
        if (rate < 0)
            updateExpertCondition(ExpertCondition.BLOCKED, expert);
    }

    public void removeExpert(int id) {
        Expert expert = findById(id);
        expertRepository.delete(expert);
    }

    public Expert forceSave(Expert expert) {
        return expertRepository.save(expert);
    }
}
