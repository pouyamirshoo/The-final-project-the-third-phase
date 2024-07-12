package com.example.finalprojectthirdphase.dto;

public record AdminSaveRequest(String firstname,
                               String lastname,
                               String username,
                               String password,
                               String email,
                               String phoneNumber,
                               String city,
                               String address,
                               String postalCode) {
}
