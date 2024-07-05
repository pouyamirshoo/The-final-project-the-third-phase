package com.example.finalprojectthirdphase.repository;


import com.example.finalprojectthirdphase.entity.Comments;
import com.example.finalprojectthirdphase.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentsRepository extends JpaRepository<Comments, Integer> {

    Optional<Comments> findByOrder(Order order);
}
