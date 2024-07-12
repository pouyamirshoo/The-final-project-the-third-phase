package com.example.finalprojectthirdphase.repository;


import com.example.finalprojectthirdphase.entity.Expert;
import com.example.finalprojectthirdphase.entity.enums.ExpertCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ExpertRepository extends JpaRepository<Expert, Integer>, JpaSpecificationExecutor<Expert> {
    Optional<Expert> findByUsername(String username);

    Optional<Expert> findByEmail(String email);

    Optional<Expert> findByPhoneNumber(String phoneNumber);

    Optional<Expert> findByPostalCode(String postalCode);

    Optional<Expert> findByNationalCode(String nationalCode);

    List<Expert> findByExpertCondition(ExpertCondition expertCondition);

    Optional<Expert> findByVerificationToken(String token);

    List<Expert> findBySubDuties_SubDutyName(String subDutyName);

}
