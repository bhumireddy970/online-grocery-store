package com.grocery.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 36)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name="order_id")

    private GroceryOrder groceryOrder;

    @Column(name="product_id",nullable=false)

    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID productId;

    @Column(name="product_name",nullable=false)

    private String productName;

    @Column(nullable=false)

    private BigDecimal price;

    @Column(nullable=false)

    private Integer quantity;

    @Column(name="sub_total",nullable=false)

    private BigDecimal subTotal;
}
