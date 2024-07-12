package com.example.finalprojectthirdphase.service;

import com.example.finalprojectthirdphase.entity.*;
import com.example.finalprojectthirdphase.entity.enums.Role;
import com.example.finalprojectthirdphase.exception.NotFoundException;
import com.example.finalprojectthirdphase.repository.AdminRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class AdminService {

    private final AdminRepository adminRepository;
    private final ExpertService expertService;
    private final RequestService requestService;
    private final SubDutyService subDutyService;
    private final BCryptPasswordEncoder passwordEncoder;

    public Admin saveAdmin(Admin admin) {
        admin.setRole(Role.ROLE_ADMIN);
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminRepository.save(admin);
    }

    public Admin adminSignIn(String username, String password) {
        Admin admin = findByUsername(username);
        String enCodePassword = admin.getPassword();
        if (!passwordEncoder.matches(password, enCodePassword)) {
            throw new NotFoundException("wrong username or password");
        }
        return admin;
    }

    public Admin findByUsername(String username) {
        return adminRepository.findByUsername(username).orElseThrow(() ->
                new NotFoundException("customer with id " + username + " not founded"));
    }

    public Optional<Admin> findByUsernameOP(String username) {
        return adminRepository.findByUsername(username);
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
