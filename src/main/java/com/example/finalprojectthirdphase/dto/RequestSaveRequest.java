package com.example.finalprojectthirdphase.dto;

import com.example.finalprojectthirdphase.entity.Expert;
import com.example.finalprojectthirdphase.entity.SubDuty;

import java.util.List;

public record RequestSaveRequest(Expert expert,
                                 List<SubDuty> subDuties
) {
}
