package com.example.finalprojectthirdphase.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SoftDelete;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SoftDelete
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@JsonIdentityInfo(scope = Customer.class,generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Customer extends Person {
    @Column(name = "customer_balance")
    Integer customerBalance;
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<Order> orders;
    @PrePersist
    public void defaultValues() {
        if (customerBalance == null)
            customerBalance = 0;
    }
}
