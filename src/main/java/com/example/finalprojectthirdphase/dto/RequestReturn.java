package com.example.finalprojectthirdphase.dto;

import com.example.finalprojectthirdphase.entity.Expert;

import java.util.List;

public record RequestReturn(Integer id,
                            Expert expert,
                            List<SubDutyReturn> subDutyList) {
}
