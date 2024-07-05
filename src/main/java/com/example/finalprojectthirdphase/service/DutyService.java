package com.example.finalprojectthirdphase.service;


import com.example.finalprojectthirdphase.entity.Duty;
import com.example.finalprojectthirdphase.exception.DuplicateInformationException;
import com.example.finalprojectthirdphase.exception.NotFoundException;
import com.example.finalprojectthirdphase.repository.DutyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class DutyService {

    private final DutyRepository dutyRepository;

    public Duty saveDuty(Duty duty) {
        if (dutyRepository.findByDutyName(duty.getDutyName()).isPresent()) {
            log.error("duplicate duty name can not insert");
            throw new DuplicateInformationException("duplicate duty name can not insert");
        }
        dutyRepository.save(duty);
        return duty;
    }

    public Duty findById(int id) {
        return dutyRepository.findById(id).orElseThrow(() ->
                new NotFoundException("duty with id " + id + " not founded"));
    }

    public List<Duty> showAllDuties() {
        return dutyRepository.findAll();
    }

    public void removeDuty(int id) {
        Duty duty = findById(id);
        dutyRepository.delete(duty);
    }
}
