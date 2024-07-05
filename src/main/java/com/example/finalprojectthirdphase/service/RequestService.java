package com.example.finalprojectthirdphase.service;


import com.example.finalprojectthirdphase.entity.Expert;
import com.example.finalprojectthirdphase.entity.Request;
import com.example.finalprojectthirdphase.exception.NotFoundException;
import com.example.finalprojectthirdphase.repository.RequestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestService {

    private final RequestRepository requestRepository;

    @Transactional
    public Request saveRequests(Request request) {
        return requestRepository.save(request);
    }

    @Transactional
    public Request findByExpert(Expert expert) {
        return requestRepository.findByExpert(expert).orElseThrow(() ->
                new NotFoundException("request for expert with id " + expert.getId() + " not founded"));
    }

    public Request findById(int id) {
        return requestRepository.findById(id).orElseThrow(() ->
                new NotFoundException("request with id " + id + " not founded"));
    }
}
