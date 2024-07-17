package com.example.finalprojectthirdphase.controller;

import com.example.finalprojectthirdphase.dto.*;
import com.example.finalprojectthirdphase.entity.*;
import com.example.finalprojectthirdphase.entity.criteria.SearchOperation;
import com.example.finalprojectthirdphase.entity.enums.ExpertCondition;
import com.example.finalprojectthirdphase.exception.NotMatchPasswordException;
import com.example.finalprojectthirdphase.mapper.*;
import com.example.finalprojectthirdphase.service.*;
import com.example.finalprojectthirdphase.util.specification.CustomerSpecificationsBuilder;
import com.example.finalprojectthirdphase.util.specification.ExpertSpecificationsBuilder;
import com.example.finalprojectthirdphase.util.specification.OfferSpecificationsBuilder;
import com.example.finalprojectthirdphase.util.specification.OrderSpecificationsBuilder;
import com.google.common.base.Joiner;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final OrderService orderService;
    private final OfferService offerService;

    @PostMapping("registerAdmin")
    public ResponseEntity<AdminReturn> registerCustomer(@Valid @RequestBody AdminSaveRequest request) {
        String password = request.password();
        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@#!%&*])[A-Za-z0-9@#!%&*]{8}$")) {
            throw new NotMatchPasswordException("password has to be 8 size and must contain at least 1 lower and upper case and 1 digit and 1 char");
        }
        Admin mappedAdmin = AdminMapper.INSTANCE.adminSaveRequestToModel(request);
        Admin savedAdmin = adminService.saveAdmin(mappedAdmin);
        return new ResponseEntity<>(AdminMapper.INSTANCE.modelAdminToSaveResponse(savedAdmin), HttpStatus.CREATED);
    }

    @PostMapping("enter_duty")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<DutyReturn> enterDuty(@Valid @RequestBody DutySaveRequest newDuty) {
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<DutyReturn> findByDutyId(@RequestParam int id) {
        Duty duty = dutyService.findById(id);
        return new ResponseEntity<>(DutyMapper.INSTANCE.modelDutyToSaveResponse(duty),
                HttpStatus.FOUND);
    }

    @PostMapping("enter_subDuty")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SubDutyReturn> enterSubDuty(@Valid @RequestBody SubDutySaveRequest newSubDuty) {
        SubDuty mappedSubDuty = SubDutyMapper.INSTANCE.subDutySaveRequestToModel(newSubDuty);
        SubDuty savedSubDuty = subDutyService.saveSubDuty(mappedSubDuty);
        return new ResponseEntity<>(SubDutyMapper.INSTANCE.modelSubDutyToSaveResponse(savedSubDuty),
                HttpStatus.CREATED);
    }

    @GetMapping("findBy_SubDutyId")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SubDutyReturn> findBySubDutyId(@RequestParam int id) {
        SubDuty subDuty = subDutyService.findById(id);
        return new ResponseEntity<>(SubDutyMapper.INSTANCE.modelSubDutyToSaveResponse(subDuty),
                HttpStatus.FOUND);
    }

    @PatchMapping("update_SubDuty_Price")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<SubDutyReturn> updateSubDutyPrice(@RequestParam int id, int newPrice) {
        SubDuty subDuty = subDutyService.updateSubDutyPrice(newPrice, id);
        return new ResponseEntity<>(SubDutyMapper.INSTANCE.modelSubDutyToSaveResponse(subDuty),
                HttpStatus.OK);
    }

    @PatchMapping("update_SubDuty_Description")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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

    @GetMapping("find_By_CustomerId")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CustomerReturn> findByCustomerId(@RequestParam int id) {
        Customer customer = customerService.findById(id);
        return new ResponseEntity<>(CustomerMapper.INSTANCE.modelCustomerToSaveResponse(customer),
                HttpStatus.FOUND);
    }

    @GetMapping("findBy_ExpertId")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ExpertReturn> findByExpertId(@RequestParam int id) {
        Expert expert = expertService.findById(id);
        return new ResponseEntity<>(ExpertMapper.INSTANCE.modelExpertToSaveResponse(expert),
                HttpStatus.FOUND);
    }

    @PatchMapping("make_Expert_Accepted")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ExpertReturn> makeExpertAccepted(@RequestParam int id, ExpertCondition expertCondition) {
        Expert expert = expertService.findById(id);
        Expert updated = expertService.updateExpertCondition(expertCondition, expert);
        return new ResponseEntity<>(ExpertMapper.INSTANCE.modelExpertToSaveResponse(updated),
                HttpStatus.OK);
    }

    @GetMapping("add_Expert_ToSubDuties _Auto")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ExpertReturn> addExpertToSubDutiesAuto(@RequestParam int id) {
        Expert expert = expertService.findById(id);
        Expert addedExpert = adminService.addExpertToSubDutyAuto(expert);
        return new ResponseEntity<>(ExpertMapper.INSTANCE.modelExpertToSaveResponse(addedExpert),
                HttpStatus.OK);
    }

    @GetMapping("add_Expert_ToSubDuties _Manual")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ExpertReturn> addExpertToSubDutiesManual(@RequestParam int id, int subDutyId) {
        Expert expert = expertService.findById(id);
        Expert addedExpert = adminService.addExpertToSubDutyManual(expert, subDutyId);
        return new ResponseEntity<>(ExpertMapper.INSTANCE.modelExpertToSaveResponse(addedExpert),
                HttpStatus.OK);
    }

    @GetMapping("remove_Expert_SubDuty")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ExpertReturn> removeExpertSubDuty(@RequestParam int id, int subDutyId) {
        Expert expert = expertService.findById(id);
        Expert addedExpert = adminService.removeExpertFromSubDuty(expert, subDutyId);
        return new ResponseEntity<>(ExpertMapper.INSTANCE.modelExpertToSaveResponse(addedExpert),
                HttpStatus.OK);
    }

    @PatchMapping("block_Expert")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ExpertReturn> blockExpert(@RequestParam int id) {
        Expert expert = expertService.findById(id);
        expert.setRate(-1);
        expertService.forceSave(expert);
        expertService.blockExpert(expert);
        return new ResponseEntity<>(ExpertMapper.INSTANCE.modelExpertToSaveResponse(expert),
                HttpStatus.FOUND);
    }

    @DeleteMapping("delete_duty")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String removeDuty(@RequestParam int id) {
        dutyService.removeDuty(id);
        return "duty and All relations have been deleted";
    }

    @DeleteMapping("delete_SubDuty")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String removeSubDuty(@RequestParam int id) {
        subDutyService.removeSubDuty(id);
        return "subDuty and All relations have been deleted";
    }

    @GetMapping("customer_Search")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    public List<CustomerReturn> customerSearch(@RequestParam String search) {
        CustomerSpecificationsBuilder builder = new CustomerSpecificationsBuilder();
        String operationSet = Joiner.on("|").join(SearchOperation.SIMPLE_OPERATION_SET);
        Pattern pattern = Pattern.compile("(\\w+?)(" + operationSet + ")(\\p{Punct}?)(\\w+?)(\\p{Punct}?),");
        Matcher matcher = pattern.matcher(search + ",");
        while (matcher.find()) {
            builder.with(
                    matcher.group(1),
                    matcher.group(2),
                    matcher.group(4),
                    matcher.group(3),
                    matcher.group(5));
        }
        Specification<Customer> spec = builder.build();
        return CustomerMapper.INSTANCE.listCustomerToSaveResponse(customerService.findAll(spec));
    }

    @GetMapping("expert_Search")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    public List<ExpertReturn> expertSearch(@RequestParam String search) {
        ExpertSpecificationsBuilder builder = new ExpertSpecificationsBuilder();
        String operationSet = Joiner.on("|").join(SearchOperation.SIMPLE_OPERATION_SET);
        Pattern pattern = Pattern.compile("(\\w+?)(" + operationSet + ")(\\p{Punct}?)(\\w+?)(\\p{Punct}?),");
        Matcher matcher = pattern.matcher(search + ",");
        while (matcher.find()) {
            builder.with(
                    matcher.group(1),
                    matcher.group(2),
                    matcher.group(4),
                    matcher.group(3),
                    matcher.group(5));
        }
        Specification<Expert> spec = builder.build();
        return ExpertMapper.INSTANCE.listExpertToSaveResponse(expertService.findAll(spec));
    }

    @GetMapping("find_subDuty_experts")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<ExpertReturn> findOneSubDutyExpert(@RequestParam String subDutyName) {
        return ExpertMapper.INSTANCE.listExpertToSaveResponse(expertService.findSubDutyExperts(subDutyName));
    }

    @GetMapping("find_Duty_orders")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<OrderReturn> findDutyOrders(@RequestParam int id) {
        Duty duty = dutyService.findById(id);
        List<SubDuty> subDuties = duty.getSubDuties();
        List<Order> orders = new ArrayList<>();
        for (SubDuty subDuty : subDuties) {
            orders = Stream.concat(orders.stream(), subDuty.getOrders().stream()).collect(Collectors.toList());
        }
        return OrderMapper.INSTANCE.listOrderToSaveResponse(orders);
    }

    @GetMapping("findBy_countOf_order")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<CustomerReturn> findByCountOfOrders(@RequestParam char operator, int count) {
        Map<Integer, Integer> mappedByCount = customerService.findAllByOrderCount();
        return switch (operator) {
            case '=' -> orderCountEqual(count, mappedByCount);
            case '>' -> orderCountMore(count, mappedByCount);
            case '<' -> orderCountLess(count, mappedByCount);
            default -> null;
        };
    }

    @GetMapping("findBy_countOf_Done_order")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<CustomerReturn> findByCountOfDoneOrders(@RequestParam char operator, int count) {
        Map<Integer, Integer> mappedByCount = customerService.findByDoneOrderCount();
        return switch (operator) {
            case '=' -> orderCountEqual(count, mappedByCount);
            case '>' -> orderCountMore(count, mappedByCount);
            case '<' -> orderCountLess(count, mappedByCount);
            default -> null;
        };
    }

    @GetMapping("findBy_countOf_offer")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<ExpertReturn> findByCountOfOffers(@RequestParam char operator, int count) {
        Map<Integer, Integer> mappedByCount = expertService.findAllByOfferCount();
        return switch (operator) {
            case '=' -> offerCountEqual(count, mappedByCount);
            case '>' -> offerCountMore(count, mappedByCount);
            case '<' -> offerCountLess(count, mappedByCount);
            default -> null;
        };
    }

    @GetMapping("findBy_countOf_Done_offer")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<ExpertReturn> findByCountOfDoneOffers(@RequestParam char operator, int count) {
        Map<Integer, Integer> mappedByCount = expertService.findByDoneOfferCount();
        return switch (operator) {
            case '=' -> offerCountEqual(count, mappedByCount);
            case '>' -> offerCountMore(count, mappedByCount);
            case '<' -> offerCountLess(count, mappedByCount);
            default -> null;
        };
    }

    @GetMapping("order_Search")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    public List<OrderReturn> orderSearch(@RequestParam String search) {
        OrderSpecificationsBuilder builder = new OrderSpecificationsBuilder();
        String operationSet = Joiner.on("|").join(SearchOperation.SIMPLE_OPERATION_SET);
        Pattern pattern = Pattern.compile("(\\w+?)(" + operationSet + ")(\\p{Punct}?)(\\w+?)(\\p{Punct}?),");
        Matcher matcher = pattern.matcher(search + ",");
        while (matcher.find()) {
            builder.with(
                    matcher.group(1),
                    matcher.group(2),
                    matcher.group(4),
                    matcher.group(3),
                    matcher.group(5));
        }
        Specification<Order> spec = builder.build();
        return OrderMapper.INSTANCE.listOrderToSaveResponse(orderService.findAll(spec));
    }

    @GetMapping("offer_Search")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    public List<OfferReturn> offerSearch(@RequestParam String search) {
        OfferSpecificationsBuilder builder = new OfferSpecificationsBuilder();
        String operationSet = Joiner.on("|").join(SearchOperation.SIMPLE_OPERATION_SET);
        Pattern pattern = Pattern.compile("(\\w+?)(" + operationSet + ")(\\p{Punct}?)(\\w+?)(\\p{Punct}?),");
        Matcher matcher = pattern.matcher(search + ",");
        while (matcher.find()) {
            builder.with(
                    matcher.group(1),
                    matcher.group(2),
                    matcher.group(4),
                    matcher.group(3),
                    matcher.group(5));
        }
        Specification<Offer> spec = builder.build();
        return OfferMapper.INSTANCE.listOfferToSaveResponse(offerService.findAll(spec));
    }

    private List<CustomerReturn> orderCountEqual(int num, Map<Integer, Integer> map) {
        List<Integer> customerId = map.entrySet().stream()
                .filter(e -> num == e.getValue())
                .map(Map.Entry::getKey).toList();
        List<Customer> customers = new ArrayList<>();
        for (Integer integer : customerId) {
            customers.add(customerService.findById(integer));
        }
        return CustomerMapper.INSTANCE.listCustomerToSaveResponse(customers);
    }

    private List<CustomerReturn> orderCountMore(int num, Map<Integer, Integer> map) {
        List<Integer> customerId = map.entrySet().stream()
                .filter(e -> num < e.getValue())
                .map(Map.Entry::getKey).toList();
        List<Customer> customers = new ArrayList<>();
        for (Integer integer : customerId) {
            customers.add(customerService.findById(integer));
        }
        return CustomerMapper.INSTANCE.listCustomerToSaveResponse(customers);
    }

    private List<CustomerReturn> orderCountLess(int num, Map<Integer, Integer> map) {
        List<Integer> customerId = map.entrySet().stream()
                .filter(e -> num > e.getValue())
                .map(Map.Entry::getKey).toList();
        List<Customer> customers = new ArrayList<>();
        for (Integer integer : customerId) {
            customers.add(customerService.findById(integer));
        }
        return CustomerMapper.INSTANCE.listCustomerToSaveResponse(customers);
    }

    private List<ExpertReturn> offerCountEqual(int num, Map<Integer, Integer> map) {
        List<Integer> expertId = map.entrySet().stream()
                .filter(e -> num == e.getValue())
                .map(Map.Entry::getKey).toList();
        List<Expert> experts = new ArrayList<>();
        for (Integer integer : expertId) {
            experts.add(expertService.findById(integer));
        }
        return ExpertMapper.INSTANCE.listExpertToSaveResponse(experts);
    }

    private List<ExpertReturn> offerCountMore(int num, Map<Integer, Integer> map) {
        List<Integer> expertId = map.entrySet().stream()
                .filter(e -> num < e.getValue())
                .map(Map.Entry::getKey).toList();
        List<Expert> experts = new ArrayList<>();
        for (Integer integer : expertId) {
            experts.add(expertService.findById(integer));
        }
        return ExpertMapper.INSTANCE.listExpertToSaveResponse(experts);
    }

    private List<ExpertReturn> offerCountLess(int num, Map<Integer, Integer> map) {
        List<Integer> expertId = map.entrySet().stream()
                .filter(e -> num > e.getValue())
                .map(Map.Entry::getKey).toList();
        List<Expert> experts = new ArrayList<>();
        for (Integer integer : expertId) {
            experts.add(expertService.findById(integer));
        }
        return ExpertMapper.INSTANCE.listExpertToSaveResponse(experts);
    }
}
