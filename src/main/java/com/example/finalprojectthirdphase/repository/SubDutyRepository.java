package com.example.finalprojectthirdphase.repository;


import com.example.finalprojectthirdphase.entity.Duty;
import com.example.finalprojectthirdphase.entity.SubDuty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubDutyRepository extends JpaRepository<SubDuty, Integer> {
    Optional<SubDuty> findBySubDutyName(String name);

    List<SubDuty> findByDuty(Duty duty);
}
