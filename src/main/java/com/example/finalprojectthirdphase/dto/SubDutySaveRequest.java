package com.example.finalprojectthirdphase.dto;

import com.example.finalprojectthirdphase.entity.Duty;

public record SubDutySaveRequest(String subDutyName,
                                 Integer price,
                                 String description,
                                 Duty duty) {
}
