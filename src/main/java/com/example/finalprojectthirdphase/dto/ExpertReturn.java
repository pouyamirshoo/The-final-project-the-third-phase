package com.example.finalprojectthirdphase.dto;

public record ExpertReturn(Integer id,
                           String firstname,
                           String lastname,
                           String username,
                           String email,
                           String phoneNumber,
                           String city,
                           String address,
                           String postalCode,
                           String nationalCode,
                           Integer rate,
                           byte[] image) {
}
