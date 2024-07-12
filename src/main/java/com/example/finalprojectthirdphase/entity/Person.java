package com.example.finalprojectthirdphase.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
@SoftDelete
@MappedSuperclass
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Pattern(regexp = "[a-zA-Z]+")
    @NotBlank(message = "firstname can not be null")
    String firstname;
    @Pattern(regexp = "[a-zA-Z]+")
    @NotBlank(message = "lastname can not be null")
    String lastname;
    @Column(unique = true)
    @NotBlank(message = "username can not be null")
    String username;
    @NotBlank(message = "password can not be null")
    String password;
    @Column(unique = true)
    @Email
    @NotBlank(message = "email can not be null")
    String email;
    @NotBlank(message = "phoneNumber can not be null")
    @Pattern(regexp = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$")
    @Column(unique = true)
    String phoneNumber;
    @Pattern(regexp = "[a-zA-Z]+")
    @NotBlank(message = "city can not be null")
    String city;
    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "address can not be null")
    String address;
    @Column(unique = true)
    @Pattern(regexp = "^(?!(\\d)\1{3})[13-9]{4}[1346-9][013-9]{5}$")
    @NotBlank(message = "postal code can not be null")
    String postalCode;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false, name = "register_Time")
    Date registerTime;
}
