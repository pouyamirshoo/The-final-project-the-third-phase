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


}
