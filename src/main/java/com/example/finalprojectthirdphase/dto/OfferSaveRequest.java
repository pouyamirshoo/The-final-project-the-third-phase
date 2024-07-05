package com.example.finalprojectthirdphase.dto;

import com.example.finalprojectthirdphase.entity.Expert;
import com.example.finalprojectthirdphase.entity.Order;

public record OfferSaveRequest(Expert expert,
                               Order order,
                               Integer offerPrice,
                               Integer takeLong) {
}
