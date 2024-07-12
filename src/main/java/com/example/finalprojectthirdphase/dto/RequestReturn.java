package com.example.finalprojectthirdphase.dto;

import java.util.List;

public record RequestReturn(Integer id,
                            ExpertReturn expert,
                            List<SubDutyReturn> subDutyList) {
}
