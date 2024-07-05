package com.example.finalprojectthirdphase.repository;


import com.example.finalprojectthirdphase.entity.Duty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DutyRepository extends JpaRepository<Duty, Integer> {
    Optional<Duty> findByDutyName(String name);
}
