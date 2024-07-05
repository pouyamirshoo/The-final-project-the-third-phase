package com.example.finalprojectthirdphase.util.specification;

import com.example.finalprojectthirdphase.entity.Customer;
import com.example.finalprojectthirdphase.entity.criteria.SearchCriteria;
import com.example.finalprojectthirdphase.entity.criteria.SearchOperation;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CustomerSpecificationsBuilder {

    private final List<SearchCriteria> params = new ArrayList<>();

    public CustomerSpecificationsBuilder with(
            String key, String operation, Object value, String prefix, String suffix) {

        SearchOperation op = SearchOperation.getSimpleOperation(operation.charAt(0));
        if (op != null) {
            if (op == SearchOperation.EQUALITY) {
                boolean startWithAsterisk = prefix.contains("*");
                boolean endWithAsterisk = suffix.contains("*");

                if (startWithAsterisk && endWithAsterisk) {
                    op = SearchOperation.CONTAINS;
                } else if (startWithAsterisk) {
                    op = SearchOperation.ENDS_WITH;
                } else if (endWithAsterisk) {
                    op = SearchOperation.STARTS_WITH;
                }
            }
            params.add(new SearchCriteria(key, op, value));
        }
        return this;
    }

    public Specification<Customer> build() {

        if (params.size() == 0) {
            return null;
        }

        Specification<Customer> result = new CustomerSpecification(params.get(0));

        for (int i = 1; i < params.size(); i++) {
            result = params.get(i).isOrPredicate()
                    ? Specification.where(result).or(new CustomerSpecification(params.get(i)))
                    : Specification.where(result).and(new CustomerSpecification(params.get(i)));
        }
        return result;
    }
}
