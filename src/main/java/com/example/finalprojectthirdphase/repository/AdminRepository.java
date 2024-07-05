package com.example.finalprojectthirdphase.repository;

import com.example.finalprojectthirdphase.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Integer> {

    Optional<Admin> findByUsernameAndPassword(String username, String password);
}