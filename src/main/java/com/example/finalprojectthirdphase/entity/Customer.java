package com.example.finalprojectthirdphase.entity;

import com.example.finalprojectthirdphase.entity.enums.Role;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SoftDelete;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SoftDelete
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@JsonIdentityInfo(scope = Customer.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Customer extends Person implements UserDetails {

    @Column(name = "customer_balance")
    Integer customerBalance;

    @Enumerated(EnumType.STRING)
    Role role;

    boolean isEnabled;

    String verificationToken;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<Order> orders;

    @PrePersist
    public void defaultValues() {
        if (customerBalance == null)
            customerBalance = 0;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

}
