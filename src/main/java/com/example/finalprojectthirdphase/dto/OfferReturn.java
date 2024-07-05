package com.example.finalprojectthirdphase.dto;

import com.example.finalprojectthirdphase.entity.enums.OfferCondition;

public record OfferReturn(Integer id,
                          ExpertReturn expert,
                          OrderReturn order,
                          OfferCondition offerCondition,
                          Integer offerPrice,
                          Integer takeLong) {
}
