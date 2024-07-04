package com.example.finalprojectthirdphase.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SoftDelete;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SoftDelete
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@JsonIdentityInfo(scope = Request.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @OneToOne(orphanRemoval = true)
    Expert expert;
    @Column(insertable = false, updatable = false)
    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable(
            name = "request_sub_duties",
            joinColumns = @JoinColumn(name = "request_id"),
            inverseJoinColumns = @JoinColumn(name = "sub_duties_id")
    )
    List<SubDuty> subDuties = new ArrayList<>();

}
