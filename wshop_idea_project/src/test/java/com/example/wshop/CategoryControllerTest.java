package com.example.wshop;

import com.example.wshop.dto.CategoryDTO;
import com.example.wshop.dto.LoginRequest;
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
public class CategoryControllerTest {

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
    void testGetCategoryById() throws Exception {
        CategoryDTO category = new CategoryDTO();
        category.setCategoryname("Electronics");
        category.setInfo("Category for electronic products");

        String categoryResponse = mockMvc.perform(post("/category")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long categoryId = new ObjectMapper().readTree(categoryResponse).get("categoryid").asLong();

        mockMvc.perform(get("/category/" + categoryId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryname").value("Electronics"));

        mockMvc.perform(get("/category/999")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllCategories() throws Exception {
        mockMvc.perform(get("/category/all")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/category/all"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateCategory() throws Exception {
        CategoryDTO category = new CategoryDTO();
        category.setCategoryname("Books");
        category.setInfo("Category for all types of books");

        mockMvc.perform(post("/category")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryname").value("Books"));

        category.setCategoryname("");
        mockMvc.perform(post("/category")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateCategoryById() throws Exception {
        CategoryDTO category = new CategoryDTO();
        category.setCategoryname("Furniture");
        category.setInfo("Category for furniture");

        String categoryResponse = mockMvc.perform(post("/category")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long categoryId = new ObjectMapper().readTree(categoryResponse).get("categoryid").asLong();

        CategoryDTO updatedCategory = new CategoryDTO();
        updatedCategory.setCategoryname("Home Furniture");
        updatedCategory.setInfo("Updated category for furniture");

        mockMvc.perform(put("/category/" + categoryId)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCategory)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryname").value("Home Furniture"));

        mockMvc.perform(put("/category/999")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedCategory)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteCategory() throws Exception {
        CategoryDTO category = new CategoryDTO();
        category.setCategoryname("Clothing");
        category.setInfo("Category for clothes");

        String categoryResponse = mockMvc.perform(post("/category")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(category)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long categoryId = new ObjectMapper().readTree(categoryResponse).get("categoryid").asLong();

        mockMvc.perform(delete("/category/" + categoryId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/category/999")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }
}
