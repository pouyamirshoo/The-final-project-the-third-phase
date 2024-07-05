package com.example.finalprojectthirdphase.dto;

import com.example.finalprojectthirdphase.entity.Customer;
import com.example.finalprojectthirdphase.entity.SubDuty;
import com.example.finalprojectthirdphase.entity.enums.BestTime;

public record OrderSaveRequest(Customer customer,
                               SubDuty subDuty,
                               Integer orderPrice,
                               String description,
                               String needExpertDate,
                               BestTime bestTime) {
}
