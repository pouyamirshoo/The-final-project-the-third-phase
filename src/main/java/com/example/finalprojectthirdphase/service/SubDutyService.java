package com.example.finalprojectthirdphase.service;


import com.example.finalprojectthirdphase.entity.Duty;
import com.example.finalprojectthirdphase.entity.SubDuty;
import com.example.finalprojectthirdphase.exception.DuplicateInformationException;
import com.example.finalprojectthirdphase.exception.NotFoundException;
import com.example.finalprojectthirdphase.repository.SubDutyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class SubDutyService {

    private final SubDutyRepository subDutyRepository;

    @Transactional
    public SubDuty saveSubDuty(SubDuty subDuty) {
        if (subDutyRepository.findBySubDutyName(subDuty.getSubDutyName()).isPresent()) {
            log.error("duplicate subDuty name can not insert");
            throw new DuplicateInformationException("duplicate subDuty name can not insert");
        }
        subDutyRepository.save(subDuty);
        return subDuty;
    }

    public SubDuty findById(int id) {
        return subDutyRepository.findById(id).orElseThrow(() ->
                new NotFoundException("subDuty with id " + id + " not founded"));
    }

    public SubDuty updateSubDutyPrice(int price, int id) {
        SubDuty subDuty = findById(id);
        subDuty.setPrice(price);
        return subDutyRepository.save(subDuty);
    }

    public SubDuty updateSubDutyDescription(String description, int id) {
        SubDuty subDuty = findById(id);
        subDuty.setDescription(description);
        return subDutyRepository.save(subDuty);
    }

    public List<SubDuty> findByDuty(Duty duty) {
        List<SubDuty> subDuties = subDutyRepository.findByDuty(duty);
        if (subDuties.isEmpty())
            throw new NullPointerException("no subDuty founded");
        return subDuties;
    }

    public void removeSubDuty(int id) {
        SubDuty subDuty = findById(id);
        subDutyRepository.delete(subDuty);
    }
}
