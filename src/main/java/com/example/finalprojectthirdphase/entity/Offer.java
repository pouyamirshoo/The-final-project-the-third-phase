package com.example.finalprojectthirdphase.entity;

import com.example.finalprojectthirdphase.entity.enums.OfferCondition;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SoftDelete
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@JsonIdentityInfo(scope = Offer.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinColumn(referencedColumnName = "id")
    Expert expert;
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinColumn(referencedColumnName = "id")
    Order order;
    @Column(name = "offer_condition")
    @Enumerated(EnumType.STRING)
    OfferCondition offerCondition;
    @Column(name = "offer_price")
    @NotNull(message = "offer price can not be null")
    Integer offerPrice;
    @Column(name = "take_long")
    @NotNull(message = "takeLong can not be null")
    Integer takeLong;
    @Column(name = "creat_Offer_Date", updatable = false)
    @CreationTimestamp
    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    Date creatOfferDate;
    @Column(name = "update_Offer_Date")
    @JsonIgnore
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    Date updateOfferDate;

    @PrePersist
    public void defaultValues() {
        if (offerCondition == null) {
            offerCondition = OfferCondition.WAITING;
        }
    }
}
