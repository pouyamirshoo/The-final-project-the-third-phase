package com.example.finalprojectthirdphase.dto;

public record CustomerReturn(Integer id,
                             String firstname,
                             String lastname,
                             String username,
                             String email,
                             String phoneNumber,
                             String city,
                             String address,
                             String postalCode) {
}
