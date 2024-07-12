package com.example.finalprojectthirdphase.dto;

public record AdminReturn(Integer id,
                          String firstname,
                          String lastname,
                          String username,
                          String email,
                          String phoneNumber,
                          String city,
                          String address,
                          String postalCode) {
}
