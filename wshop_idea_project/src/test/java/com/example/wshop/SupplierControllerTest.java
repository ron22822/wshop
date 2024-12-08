package com.example.wshop;

import com.example.wshop.dto.LoginRequest;
import com.example.wshop.dto.SupplierDTO;
import com.example.wshop.dto.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class SupplierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String jwtToken;

    @BeforeAll
    static void setup(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper) throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("admin");
        userDTO.setPassword("adminpassword");
        userDTO.setEmail("admin@example.com");
        userDTO.setRole("ADMIN");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("adminpassword");

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        jwtToken = new ObjectMapper().readTree(response).get("token").asText();
    }

    @Test
    void testGetSupplierById() throws Exception {
        SupplierDTO supplier = new SupplierDTO();
        supplier.setSuppliername("Supplier");
        supplier.setContactinfo("123-456-789");

        String supplierResponse = mockMvc.perform(post("/supplier")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplier)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long supplierId = new ObjectMapper().readTree(supplierResponse).get("supplierid").asLong();

        mockMvc.perform(get("/supplier/" + supplierId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.suppliername").value("Supplier"));

        mockMvc.perform(get("/supplier/999")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllSuppliers() throws Exception {
        mockMvc.perform(get("/supplier/all")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/supplier/all"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateSupplier() throws Exception {
        SupplierDTO supplier = new SupplierDTO();
        supplier.setSuppliername("Supplier ABC");
        supplier.setContactinfo("Contact Info for Supplier ABC");

        mockMvc.perform(post("/supplier")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplier)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.suppliername").value("Supplier ABC"));

        supplier.setSuppliername("");
        mockMvc.perform(post("/supplier")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplier)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateSupplierById() throws Exception {
        SupplierDTO supplier = new SupplierDTO();
        supplier.setSuppliername("Supplier XYZ");
        supplier.setContactinfo("Contact Info for Supplier XYZ");

        String supplierResponse = mockMvc.perform(post("/supplier")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplier)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long supplierId = new ObjectMapper().readTree(supplierResponse).get("supplierid").asLong();

        SupplierDTO updatedSupplier = new SupplierDTO();
        updatedSupplier.setSuppliername("Updated Supplier XYZ");
        updatedSupplier.setContactinfo("Updated Contact Info");

        mockMvc.perform(put("/supplier/" + supplierId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSupplier)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.suppliername").value("Updated Supplier XYZ"));

        mockMvc.perform(put("/supplier/999")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedSupplier)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteSupplier() throws Exception {
        SupplierDTO supplier = new SupplierDTO();
        supplier.setSuppliername("Supplier to Delete");
        supplier.setContactinfo("Contact Info for Deletion");

        String supplierResponse = mockMvc.perform(post("/supplier")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplier)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long supplierId = new ObjectMapper().readTree(supplierResponse).get("supplierid").asLong();

        mockMvc.perform(delete("/supplier/" + supplierId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/supplier/999")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }
}

