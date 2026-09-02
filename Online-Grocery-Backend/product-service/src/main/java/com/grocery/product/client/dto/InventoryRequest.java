package com.grocery.product.client.dto;

import java.util.UUID;


public record InventoryRequest (

     UUID productId,

     Integer availableQuantity

){}