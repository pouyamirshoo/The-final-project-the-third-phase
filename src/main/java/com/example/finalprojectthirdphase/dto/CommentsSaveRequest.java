package com.example.finalprojectthirdphase.dto;

public record CommentsSaveRequest(OrderReturn order,
                                  Integer rate,
                                  String additionalComments) {
}
