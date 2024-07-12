package com.example.finalprojectthirdphase.entity;

import com.example.finalprojectthirdphase.entity.enums.ExpertCondition;
import com.example.finalprojectthirdphase.entity.enums.Role;
import com.example.finalprojectthirdphase.validation.ValidationCode;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SoftDelete;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@SoftDelete
@Entity
@JsonIdentityInfo(scope = Expert.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Expert extends Person implements UserDetails {
    @Column(unique = true, name = "national_code")
    @NotBlank(message = "Expert national code can not be null")
    @ValidationCode
    String nationalCode;
    @Column(name = "expert_image")
    @Lob
    @Size(max = 300000, message = "it is too big file for image")
    @NotEmpty(message = "must upload an image")
    @JsonIgnore
    byte[] expertImage;
    @Column(name = "expert_condition")
    @Enumerated(EnumType.STRING)
    ExpertCondition expertCondition;
    @Column(length = 500, name = "reject_reason")
    String rejectReason;
    Integer rate;
    @Min(0)
    Integer balance;
    @Enumerated(EnumType.STRING)
    Role role;
    boolean isEnabled;
    String verificationToken;
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    List<SubDuty> subDuties;
    @OneToMany(mappedBy = "expert", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<Offer> offers;

    @PrePersist
    public void defaultValues() {
        if (expertCondition == null) {
            expertCondition = ExpertCondition.NEW;
        }
        if (balance == null) {
            balance = 0;
        }
        if (rate == null) {
            rate = 0;
        }
    }

    public void addSubDuty(SubDuty subDuty) {
        this.subDuties.add(subDuty);
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
