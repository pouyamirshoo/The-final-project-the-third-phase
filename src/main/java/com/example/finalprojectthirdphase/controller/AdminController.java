package com.example.finalprojectthirdphase.controller;

import com.example.finalprojectthirdphase.dto.*;
import com.example.finalprojectthirdphase.entity.Customer;
import com.example.finalprojectthirdphase.entity.Duty;
import com.example.finalprojectthirdphase.entity.Expert;
import com.example.finalprojectthirdphase.entity.SubDuty;
import com.example.finalprojectthirdphase.entity.criteria.SearchOperation;
import com.example.finalprojectthirdphase.entity.enums.ExpertCondition;
import com.example.finalprojectthirdphase.mapper.CustomerMapper;
import com.example.finalprojectthirdphase.mapper.DutyMapper;
import com.example.finalprojectthirdphase.mapper.ExpertMapper;
import com.example.finalprojectthirdphase.mapper.SubDutyMapper;
import com.example.finalprojectthirdphase.service.*;
import com.example.finalprojectthirdphase.util.specification.CustomerSpecificationsBuilder;
import com.example.finalprojectthirdphase.util.specification.ExpertSpecificationsBuilder;
import com.google.common.base.Joiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminController {

    private final AdminService adminService;
    private final DutyService dutyService;
    private final SubDutyService subDutyService;
    private final ExpertService expertService;
    private final CustomerService customerService;

    @PostMapping("enter_duty")
    public ResponseEntity<DutyReturn> enterDuty(@Validated @RequestBody DutySaveRequest newDuty) {
        Duty mappedDuty = DutyMapper.INSTANCE.dutySaveRequestToModel(newDuty);
        Duty savedDuty = dutyService.saveDuty(mappedDuty);
        return new ResponseEntity<>(DutyMapper.INSTANCE.modelDutyToSaveResponse(savedDuty), HttpStatus.CREATED);
    }

    @GetMapping("show_All_Duties")
    public List<DutyReturn> showAllDuties() {
        List<Duty> duties = dutyService.showAllDuties();
        return DutyMapper.INSTANCE.listDutyToSaveResponse(duties);
    }

    @GetMapping("findBy_DutyId")
    public ResponseEntity<DutyReturn> findByDutyId(@RequestParam int id) {
        Duty duty = dutyService.findById(id);
        return new ResponseEntity<>(DutyMapper.INSTANCE.modelDutyToSaveResponse(duty),
                HttpStatus.FOUND);
    }

    @PostMapping("enter_subDuty")
    public ResponseEntity<SubDutyReturn> enterSubDuty(@Validated @RequestBody SubDutySaveRequest newSubDuty) {
        SubDuty mappedSubDuty = SubDutyMapper.INSTANCE.subDutySaveRequestToModel(newSubDuty);
        SubDuty savedSubDuty = subDutyService.saveSubDuty(mappedSubDuty);
        return new ResponseEntity<>(SubDutyMapper.INSTANCE.modelSubDutyToSaveResponse(savedSubDuty),
                HttpStatus.CREATED);
    }

    @GetMapping("findBy_SubDutyId")
    public ResponseEntity<SubDutyReturn> findBySubDutyId(@RequestParam int id) {
        SubDuty subDuty = subDutyService.findById(id);
        return new ResponseEntity<>(SubDutyMapper.INSTANCE.modelSubDutyToSaveResponse(subDuty),
                HttpStatus.FOUND);
    }

    @GetMapping("update_SubDuty_Price")
    public ResponseEntity<SubDutyReturn> updateSubDutyPrice(@RequestParam int id, int newPrice) {
        SubDuty subDuty = subDutyService.updateSubDutyPrice(newPrice, id);
        return new ResponseEntity<>(SubDutyMapper.INSTANCE.modelSubDutyToSaveResponse(subDuty),
                HttpStatus.OK);
    }

    @GetMapping("update_SubDuty_Description")
    public ResponseEntity<SubDutyReturn> updateSubDutyDescription(@RequestParam int id, String newDescription) {
        SubDuty subDuty = subDutyService.updateSubDutyDescription(newDescription, id);
        return new ResponseEntity<>(SubDutyMapper.INSTANCE.modelSubDutyToSaveResponse(subDuty),
                HttpStatus.OK);
    }

    @GetMapping("show_SubDuties_For_One_Duty")
    public List<SubDutyReturn> showSubDutiesForOneDuty(@RequestParam int id) {
        Duty duty = dutyService.findById(id);
        List<SubDuty> subDuties = subDutyService.findByDuty(duty);
        return SubDutyMapper.INSTANCE.listSubDutyToSaveResponse(subDuties);
    }

    @GetMapping("make_Expert_Accepted")
    public ResponseEntity<ExpertReturn> makeExpertAccepted(@RequestParam int id, ExpertCondition expertCondition) {
        Expert expert = expertService.findById(id);
        Expert updated = expertService.updateExpertCondition(expertCondition, expert);
        return new ResponseEntity<>(ExpertMapper.INSTANCE.modelExpertToSaveResponse(updated),
                HttpStatus.OK);
    }

    @GetMapping("add_Expert_ToSubDuties _Auto")
    public ResponseEntity<ExpertReturn> addExpertToSubDutiesAuto(@RequestParam int id) {
        Expert expert = expertService.findById(id);
        Expert addedExpert = adminService.addExpertToSubDutyAuto(expert);
        return new ResponseEntity<>(ExpertMapper.INSTANCE.modelExpertToSaveResponse(addedExpert),
                HttpStatus.OK);
    }

    @GetMapping("add_Expert_ToSubDuties _Manual")
    public ResponseEntity<ExpertReturn> addExpertToSubDutiesManual(@RequestParam int id, int subDutyId) {
        Expert expert = expertService.findById(id);
        Expert addedExpert = adminService.addExpertToSubDutyManual(expert, subDutyId);
        return new ResponseEntity<>(ExpertMapper.INSTANCE.modelExpertToSaveResponse(addedExpert),
                HttpStatus.OK);
    }

    @GetMapping("remove_Expert_SubDuty")
    public ResponseEntity<ExpertReturn> removeExpertSubDuty(@RequestParam int id, int subDutyId) {
        Expert expert = expertService.findById(id);
        Expert addedExpert = adminService.removeExpertFromSubDuty(expert, subDutyId);
        return new ResponseEntity<>(ExpertMapper.INSTANCE.modelExpertToSaveResponse(addedExpert),
                HttpStatus.OK);
    }

    @DeleteMapping("delete_duty")
    public String removeDuty(@RequestParam int id){
        dutyService.removeDuty(id);
        return "duty and All relations have been deleted";
    }

    @DeleteMapping("delete_SubDuty")
    public String removeSubDuty(@RequestParam int id){
        subDutyService.removeSubDuty(id);
        return "subDuty and All relations have been deleted";
    }


}
