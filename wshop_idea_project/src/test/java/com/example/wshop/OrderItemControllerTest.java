package com.example.wshop;

import com.example.wshop.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class OrderItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String jwtTokenAdmin;
    private static String jwtTokenUser;
    private static Long productId;
    private static Long orderID;

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

        jwtTokenAdmin = new ObjectMapper().readTree(response).get("token").asText();

        userDTO.setUsername("user");
        userDTO.setPassword("userpassword");
        userDTO.setEmail("admin@example.com");
        userDTO.setRole("USER");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated());

        loginRequest = new LoginRequest();
        loginRequest.setUsername("user");
        loginRequest.setPassword("userpassword");

        response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        jwtTokenUser = new ObjectMapper().readTree(response).get("token").asText();

        SupplierDTO supplier = new SupplierDTO();
        supplier.setSuppliername("Supplier");
        supplier.setContactinfo("123-456-789");

        mockMvc.perform(post("/supplier")
                        .header("Authorization", "Bearer " + jwtTokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(supplier)))
                .andExpect(status().isCreated());

        CategoryDTO category = new CategoryDTO();
        category.setCategoryname("Electronics");
        category.setInfo("Category for electronic products");

        mockMvc.perform(post("/category")
                        .header("Authorization", "Bearer " + jwtTokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isCreated());

        ProductDTO product = new ProductDTO();
        product.setProductname("Sample Product");
        product.setPrice(new BigDecimal("99.99"));
        product.setTotalquantity(100);
        product.setActivity("ACTIVE");
        product.setCategoryname("Electronics");
        product.setSuppliername("Supplier");

        String productResponse = mockMvc.perform(post("/product")
                        .header("Authorization", "Bearer " + jwtTokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        productId = new ObjectMapper().readTree(productResponse).get("productid").asLong();

        String orderResponse = mockMvc.perform(post("/order")
                        .header("Authorization", "Bearer " + jwtTokenUser))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        orderID = new ObjectMapper().readTree(orderResponse).get("orderid").asLong();
    }

    @Test
    void testCreateAndUpdateOrderItem() throws Exception{
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setProductid(productId);
        orderItemDTO.setOrderid(orderID);
        orderItemDTO.setItemcount(50);

        mockMvc.perform(post("/orderitem")
                        .header("Authorization", "Bearer " + jwtTokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderItemDTO)))
                .andExpect(status().isCreated());

        orderItemDTO.setItemcount(100);

        mockMvc.perform(put("/orderitem")
                        .header("Authorization", "Bearer " + jwtTokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderItemDTO)))
                .andExpect(status().isCreated());

        orderItemDTO.setItemcount(200);

        mockMvc.perform(put("/orderitem")
                        .header("Authorization", "Bearer " + jwtTokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderItemDTO)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testGetOrderItem() throws Exception{
        mockMvc.perform(get("/orderitem/all")
                        .header("Authorization", "Bearer " + jwtTokenAdmin))
                .andExpect(status().isOk());

        mockMvc.perform(get("/orderitem/order/"+orderID)
                        .header("Authorization", "Bearer " + jwtTokenUser))
                .andExpect(status().isOk());


    }

    @Test
    void testDeleteOrderItem() throws Exception{
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setProductid(productId);
        orderItemDTO.setOrderid(orderID);

        mockMvc.perform(delete("/orderitem")
                        .header("Authorization", "Bearer " + jwtTokenUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderItemDTO)))
                .andExpect(status().isNoContent());
    }
}
