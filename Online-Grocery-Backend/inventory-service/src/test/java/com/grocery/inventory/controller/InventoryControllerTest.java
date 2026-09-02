package com.grocery.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grocery.inventory.dto.InventoryRequest;
import com.grocery.inventory.dto.InventoryResponse;
import com.grocery.inventory.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private InventoryService inventoryService;

    @Test
    void addInventoryShouldReturnCreated() throws Exception {
        UUID productId = UUID.randomUUID();
        when(inventoryService.addInventory(any())).thenReturn(
                new InventoryResponse(productId, 100, 0, LocalDateTime.now(), LocalDateTime.now()));

        mockMvc.perform(post("/api/inventory/initialize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new InventoryRequest(productId, 100))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(productId.toString()));
    }
}
