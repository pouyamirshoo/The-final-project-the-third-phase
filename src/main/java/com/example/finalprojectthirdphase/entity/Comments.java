package com.example.finalprojectthirdphase.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SoftDelete;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@SoftDelete
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Comments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @OneToOne
    Order order;
    @NotNull
    @Min(0)
    @Max(5)
    int rate;
    @Column(name = "additional_comments", length = 500)
    String additionalComments;
}
