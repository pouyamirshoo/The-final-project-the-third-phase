package com.example.finalprojectthirdphase.repository;


import com.example.finalprojectthirdphase.entity.Expert;
import com.example.finalprojectthirdphase.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Integer> {
    Optional<Request> findByExpert(Expert expert);
}
