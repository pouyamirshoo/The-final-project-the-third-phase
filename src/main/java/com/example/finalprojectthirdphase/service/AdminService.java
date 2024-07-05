package com.example.finalprojectthirdphase.service;

import com.example.finalprojectthirdphase.entity.Admin;
import com.example.finalprojectthirdphase.entity.Expert;
import com.example.finalprojectthirdphase.entity.Request;
import com.example.finalprojectthirdphase.entity.SubDuty;
import com.example.finalprojectthirdphase.exception.NotFoundException;
import com.example.finalprojectthirdphase.repository.AdminRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class AdminService {

    private final AdminRepository adminRepository;
    private final ExpertService expertService;
    private final RequestService requestService;
    private final SubDutyService subDutyService;

    public void saveAdmin(Admin admin) {
        adminRepository.save(admin);
    }

    public void adminSignIn(String username, String password) {
        adminRepository.findByUsernameAndPassword(username, password).orElseThrow(() ->
                new NotFoundException("wrong username or password"));
    }

    public Expert addExpertToSubDutyAuto(Expert expert) {
        Request request = requestService.findByExpert(expert);
        List<SubDuty> requestSubDuties = request.getSubDuties();
        for (SubDuty subDuty : requestSubDuties) {
            expert.addSubDuty(subDuty);
        }
        return expertService.forceSave(expert);
    }

    public Expert addExpertToSubDutyManual(Expert expert, int id) {
        SubDuty subDuty = subDutyService.findById(id);
        expert.addSubDuty(subDuty);
        return expertService.forceSave(expert);
    }

    public Expert removeExpertFromSubDuty(Expert expert, int subDutyId) {
        List<SubDuty> subDuties = expert.getSubDuties();
        subDuties.removeIf(subDuty -> subDuty.getId() == subDutyId);
        expert.setSubDuties(subDuties);
        return expertService.forceSave(expert);
    }
}
