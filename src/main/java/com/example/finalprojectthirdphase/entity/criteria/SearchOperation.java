package com.example.finalprojectthirdphase.entity.criteria;

public enum SearchOperation {
    EQUALITY, NEGATION, GREATER_THAN, LESS_THAN, LIKE, STARTS_WITH, ENDS_WITH, CONTAINS, JOIN, JOIIN;

    public static final String[] SIMPLE_OPERATION_SET = {":", "!", ">", "<", "~", "#", "@"};

    public static SearchOperation getSimpleOperation(char input) {
        return switch (input) {
            case ':' -> EQUALITY;
            case '!' -> NEGATION;
            case '>' -> GREATER_THAN;
            case '<' -> LESS_THAN;
            case '~' -> LIKE;
            case '#' -> JOIIN;
            case '@' -> JOIN;
            default -> null;
        };
    }
}

