package com.example.finalprojectthirdphase.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SoftDelete;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SoftDelete
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@JsonIdentityInfo(scope = SubDuty.class,generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
public class SubDuty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column(unique = true, name = "sub_duty_name")
    @Pattern(regexp = "[a-zA-Z]+")
    @NotBlank(message = "sub duty name can not be null")
    String subDutyName;
    @NotNull(message = "sub duty price can not be null")
    @Min(0)
    Integer price;
    @Column(length = 500)
    @NotBlank(message = "description most not null")
    String description;
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    Duty duty;
    @OneToMany(mappedBy = "subDuty", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    List<Order> orders;
}
