package com.example.wshop;

import com.example.wshop.dto.LoginRequest;
import com.example.wshop.dto.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String jwtTokenAdmin;
    private static String jwtTokenUser;


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
    }

    @Test
    void testCreateOrder() throws Exception{
        mockMvc.perform(post("/order")
                        .header("Authorization", "Bearer " + jwtTokenUser))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/order")
                        .header("Authorization", "Bearer " + jwtTokenAdmin))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetOrder() throws Exception{
        String orderResponse = mockMvc.perform(post("/order")
                        .header("Authorization", "Bearer " + jwtTokenUser))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long firstOrderID = new ObjectMapper().readTree(orderResponse).get("orderid").asLong();


        mockMvc.perform(get("/order/"+firstOrderID.toString())
                        .header("Authorization", "Bearer " + jwtTokenAdmin))
                .andExpect(status().isOk());

        mockMvc.perform(get("/order/all")
                        .header("Authorization", "Bearer " + jwtTokenAdmin))
                .andExpect(status().isOk());

        mockMvc.perform(get("/order/all/me")
                        .header("Authorization", "Bearer " + jwtTokenUser))
                .andExpect(status().isOk());

        mockMvc.perform(get("/order/all")
                        .header("Authorization", "Bearer " + jwtTokenUser))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCancelledAndCompletedAndDeleteOrderItem() throws Exception{
        String orderResponse = mockMvc.perform(post("/order")
                        .header("Authorization", "Bearer " + jwtTokenUser))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long firstOrderID = new ObjectMapper().readTree(orderResponse).get("orderid").asLong();

        orderResponse = mockMvc.perform(post("/order")
                        .header("Authorization", "Bearer " + jwtTokenUser))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long secondOrderID = new ObjectMapper().readTree(orderResponse).get("orderid").asLong();

        mockMvc.perform(delete("/order/"+firstOrderID)
                        .header("Authorization", "Bearer " + jwtTokenAdmin))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/order/cancelled/"+firstOrderID)
                        .header("Authorization", "Bearer " + jwtTokenAdmin))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/order/cancelled/"+firstOrderID)
                        .header("Authorization", "Bearer " + jwtTokenUser))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/order/completed/"+secondOrderID)
                        .header("Authorization", "Bearer " + jwtTokenUser))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/order/completed/"+secondOrderID)
                        .header("Authorization", "Bearer " + jwtTokenAdmin))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/order/"+firstOrderID)
                        .header("Authorization", "Bearer " + jwtTokenAdmin))
                .andExpect(status().isNoContent());
    }


}
