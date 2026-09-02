package com.grocery.order.controller;//package com.grocery.order.controller;

import com.grocery.order.dto.CustomerDTO;
import com.grocery.order.dto.LoginRequest;
import com.grocery.order.exception.ErrorResponse;
import com.grocery.order.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@Tag(name = "Customer Management API", description = """
        APIs for managing customers in the Online Grocery Store.
        
        Features:
        • Register a new customer
        • Retrieve customer details
        • Retrieve all customers
        • Update customer information
        • Delete a customer
        """)
public class

CustomerController {

    private final CustomerService customerService;

    @PostMapping("/login")
    public ResponseEntity<CustomerDTO> login(@RequestBody LoginRequest

                                                     loginRequest) {

        CustomerDTO login = customerService.login(loginRequest);
        CustomerDTO customerDTO = login;

        return ResponseEntity.ok(customerDTO);
    }

    @Operation(summary = "Register Customer", description = """
            Registers a new customer.
            
            Business Rules:
            • Customer email must be unique.
            • Name and email are mandatory.
            • Email must be in a valid format.
            """)
    @ApiResponses(value = {

            @ApiResponse(responseCode = "201", description = "Customer registered successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDTO.class), examples = @ExampleObject(value = """
                    {
                      "id":"550e8400-e29b-41d4-a716-446655440000",
                      "name":"Ashok Kumar",
                      "email":"ashok@gmail.com",
                      "phone":"9876543210",
                      "address":"Visakhapatnam, Andhra Pradesh"
                    }
                    """))),

            @ApiResponse(responseCode = "400", description = "Invalid customer details", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
                    {
                      "timestamp":"2026-07-03T20:30:00",
                      "status":400,
                      "error":"Bad Request",
                      "message":"Email must be valid",
                      "path":"/api/customers"
                    }
                    """))),

            @ApiResponse(responseCode = "409", description = "Customer already exists", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
                    {
                      "timestamp":"2026-07-03T20:30:00",
                      "status":409,
                      "error":"Conflict",
                      "message":"Customer already exists",
                      "path":"/api/customers"
                    }
                    """)))

    })
    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Customer registration details", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDTO.class), examples = @ExampleObject(value = """
                    {
                      "name":"Ashok Kumar",
                      "email":"ashok@gmail.com",
                      "phone":"9876543210",
                      "address":"Visakhapatnam, Andhra Pradesh"
                    }
                    """)))

            @Valid @RequestBody CustomerDTO customerDTO) {

        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(customerDTO));

    }

    @Operation(summary = "Get Customer By ID", description = """
            Retrieves complete customer information using the customer UUID.
            """)
    @ApiResponses(value = {

            @ApiResponse(responseCode = "200", description = "Customer retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDTO.class), examples = @ExampleObject(value = """
                    {
                      "id":"550e8400-e29b-41d4-a716-446655440000",
                      "name":"Ashok Kumar",
                      "email":"ashok@gmail.com",
                      "phone":"9876543210",
                      "address":"Visakhapatnam, Andhra Pradesh"
                    }
                    """))),

            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
                    {
                      "timestamp":"2026-07-03T20:30:00",
                      "status":404,
                      "error":"Not Found",
                      "message":"Customer not found",
                      "path":"/api/customers/550e8400-e29b-41d4-a716-446655440000"
                    }
                    """)))

    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomer(

            @Parameter(description = "Unique Customer UUID", required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id) {

        return ResponseEntity.ok(customerService.getCustomerById(id));

    }

    @Operation(summary = "Get All Customers", description = "Retrieves all registered customers.")
    @ApiResponses(value = {

            @ApiResponse(responseCode = "200", description = "Customers retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDTO.class), examples = @ExampleObject(value = """
                    [
                      {
                        "id":"550e8400-e29b-41d4-a716-446655440000",
                        "name":"Ashok Kumar",
                        "email":"ashok@gmail.com",
                        "phone":"9876543210",
                        "address":"Visakhapatnam"
                      },
                      {
                        "id":"9e0d2ab5-4f7e-4f8f-a4c2-ef1e9b47f6f1",
                        "name":"Ravi Kumar",
                        "email":"ravi@gmail.com",
                        "phone":"9876501234",
                        "address":"Hyderabad"
                      }
                    ]
                    """)))

    })
    @GetMapping
    public ResponseEntity<List<CustomerDTO>> getAllCustomers() {

        return ResponseEntity.ok(customerService.getAllCustomers());

    }

    @Operation(summary = "Update Customer", description = """
            Updates an existing customer.
            
            Business Rules:
            • Customer must exist.
            • Email address must remain unique.
            • Name and email cannot be empty.
            """)
    @ApiResponses(value = {

            @ApiResponse(responseCode = "200", description = "Customer updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDTO.class), examples = @ExampleObject(value = """
                    {
                      "id":"550e8400-e29b-41d4-a716-446655440000",
                      "name":"Ashok Kumar",
                      "email":"ashok@gmail.com",
                      "phone":"9876543210",
                      "address":"Vizag"
                    }
                    """))),

            @ApiResponse(responseCode = "400", description = "Invalid customer details", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
                    {
                      "timestamp":"2026-07-03T20:30:00",
                      "status":400,
                      "error":"Bad Request",
                      "message":"Email must be valid",
                      "path":"/api/customers/550e8400-e29b-41d4-a716-446655440000"
                    }
                    """))),

            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
                    {
                      "timestamp":"2026-07-03T20:30:00",
                      "status":404,
                      "error":"Not Found",
                      "message":"Customer not found",
                      "path":"/api/customers/550e8400-e29b-41d4-a716-446655440000"
                    }
                    """))),

            @ApiResponse(responseCode = "409", description = "Customer already exists", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
                    {
                      "timestamp":"2026-07-03T20:30:00",
                      "status":409,
                      "error":"Conflict",
                      "message":"Customer already exists",
                      "path":"/api/customers/550e8400-e29b-41d4-a716-446655440000"
                    }
                    """)))

    })
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(

            @Parameter(description = "Unique Customer UUID", required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated customer details", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CustomerDTO.class), examples = @ExampleObject(value = """
                    {
                      "name":"Ashok Kumar",
                      "email":"ashok@gmail.com",
                      "phone":"9876543210",
                      "address":"Vizag"
                    }
                    """))) @Valid @RequestBody CustomerDTO customerDTO) {

        return ResponseEntity.ok(customerService.updateCustomer(id, customerDTO));

    }

    @Operation(summary = "Delete Customer", description = """
            Deletes a customer.
            
            Business Rules:
            • Customer must exist.
            • Customer cannot be deleted if associated with existing orders.
            """)
    @ApiResponses(value = {

            @ApiResponse(responseCode = "200", description = "Customer deleted successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "\"Customer deleted successfully.\""))),

            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
                    {
                      "timestamp":"2026-07-03T20:30:00",
                      "status":404,
                      "error":"Not Found",
                      "message":"Customer not found",
                      "path":"/api/customers/550e8400-e29b-41d4-a716-446655440000"
                    }
                    """))),

            @ApiResponse(responseCode = "409", description = "Customer cannot be deleted", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = """
                    {
                      "timestamp":"2026-07-03T20:30:00",
                      "status":409,
                      "error":"Conflict",
                      "message":"Customer has existing orders and cannot be deleted",
                      "path":"/api/customers/550e8400-e29b-41d4-a716-446655440000"
                    }
                    """)))

    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(

            @Parameter(description = "Unique Customer UUID", required = true, example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable UUID id) {

        customerService.deleteCustomer(id);

        return ResponseEntity.ok("Customer deleted successfully.");

    }

}