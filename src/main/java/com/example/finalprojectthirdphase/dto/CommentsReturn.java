package com.example.finalprojectthirdphase.dto;

public record CommentsReturn(Integer id,
                             OrderReturn order,
                             Integer rate,
                             String additionalComments) {
}
