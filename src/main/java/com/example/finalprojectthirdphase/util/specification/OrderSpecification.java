package com.example.finalprojectthirdphase.util.specification;

import com.example.finalprojectthirdphase.entity.Order;
import com.example.finalprojectthirdphase.entity.criteria.SearchCriteria;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

@AllArgsConstructor
public class OrderSpecification implements Specification<Order> {

    private SearchCriteria criteria;

    @Override
    public Predicate toPredicate(@NonNull
                                 Root<Order> root, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder builder) {

        return switch (criteria.getOperation()) {
            case EQUALITY -> builder.equal(root.get(criteria.getKey()), criteria.getValue());
            case NEGATION -> builder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN -> builder.greaterThan(root.get(
                    criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN -> builder.lessThan(root.get(
                    criteria.getKey()), criteria.getValue().toString());
            case LIKE -> builder.like(root.get(
                    criteria.getKey()), criteria.getValue().toString());
            case STARTS_WITH -> builder.like(root.get(criteria.getKey()), criteria.getValue() + "%");
            case ENDS_WITH -> builder.like(root.get(criteria.getKey()), "%" + criteria.getValue());
            case CONTAINS -> builder.like(root.get(
                    criteria.getKey()), "%" + criteria.getValue() + "%");
            case JOIN -> builder.equal(root.get("subDuty").get("subDutyName"), criteria.getValue().toString());
            case JOIIN -> builder.equal(root.get("customer").get("firstname"), criteria.getValue().toString());
        };
    }
}
