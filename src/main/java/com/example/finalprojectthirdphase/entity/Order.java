package com.example.finalprojectthirdphase.entity;

import com.example.finalprojectthirdphase.entity.enums.BestTime;
import com.example.finalprojectthirdphase.entity.enums.OrderCondition;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SoftDelete
@Table(name = "Orders")
@Entity
@JsonIdentityInfo(scope = Order.class, generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinColumn(referencedColumnName = "id")
    Customer customer;
    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinColumn(referencedColumnName = "id")
    SubDuty subDuty;
    @Column(name = "order_condition")
    @Enumerated(EnumType.STRING)
    OrderCondition orderCondition;
    @Column(name = "date_create_order")
    @NotNull
    Date dateCreatOrder;
    @NotNull(message = "you must enter price")
    int orderPrice;
    @Column(length = 500)
    @NotNull(message = "enter description")
    String description;
    @Column(name = "need_expert")
    @NotNull
    Date needExpert;
    @Column(name = "best_time")
    @Enumerated(EnumType.STRING)
    @NotNull
    BestTime bestTime;
    @Column(name = "update_Order_Date")
    @UpdateTimestamp
    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    Date updateOrderDate;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    List<Offer> offers;

    @PrePersist
    public void defaultValues() {
        if (orderCondition == null) {
            orderCondition = OrderCondition.RECEIVING_OFFERS;
        }
    }
}
