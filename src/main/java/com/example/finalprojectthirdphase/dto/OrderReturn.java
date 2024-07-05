package com.example.finalprojectthirdphase.dto;

import com.example.finalprojectthirdphase.entity.enums.BestTime;
import com.example.finalprojectthirdphase.entity.enums.OrderCondition;

import java.util.Date;

public record OrderReturn(Integer id,
                          CustomerReturn customer,
                          SubDutyReturn subDuty,
                          OrderCondition orderCondition,
                          Integer orderPrice,
                          String description,
                          Date needExpert,
                          BestTime bestTime) {
}
