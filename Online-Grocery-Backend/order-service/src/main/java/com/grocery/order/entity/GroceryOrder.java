package com.grocery.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "grocery_orders")
public class GroceryOrder extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 36)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name="customer_id")

    private Customer customer;

    @Column(name="order_date",nullable=false)

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)

    @Column(nullable=false)

    private OrderStatus status;

    @Column(name="total_amount",nullable=false)

    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "groceryOrder",
            cascade = CascadeType.ALL,
            orphanRemoval = true)

    private List<OrderItem> orderItems;
}
