package com.grocery.order.repository;

import com.grocery.order.entity.GroceryOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GroceryOrderRepository extends JpaRepository<GroceryOrder, UUID> {

}