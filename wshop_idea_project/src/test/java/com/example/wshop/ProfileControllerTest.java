package com.example.wshop;

import com.example.wshop.dto.LoginRequest;
import com.example.wshop.dto.ProfileDTO;
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
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    static private String jwtToken;

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
    void testGetProfileById() throws Exception {
        mockMvc.perform(get("/profile/1")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").isEmpty());

        mockMvc.perform(get("/profile/999")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetCurrentProfile() throws Exception {
        mockMvc.perform(get("/profile/me")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").isNotEmpty());

        mockMvc.perform(get("/profile/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateProfileById() throws Exception {
        ProfileDTO updatedProfile = new ProfileDTO();
        updatedProfile.setFirstname("UpdatedFirstname");
        updatedProfile.setLastname("UpdatedLastname");
        updatedProfile.setBirthday("2000-01-01");
        updatedProfile.setGender("Male");

        mockMvc.perform(put("/profile/1")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProfile)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstname").value("UpdatedFirstname"));

        mockMvc.perform(put("/profile/999")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProfile)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateCurrentProfile() throws Exception {
        ProfileDTO updatedProfile = new ProfileDTO();
        updatedProfile.setFirstname("UpdatedMe");
        updatedProfile.setLastname("UpdatedLastname");
        updatedProfile.setBirthday("1995-05-05");
        updatedProfile.setGender("Female");

        mockMvc.perform(put("/profile/me")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProfile)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstname").value("UpdatedMe"));
    }

    @Test
    void testGetAllProfiles() throws Exception {
        mockMvc.perform(get("/profile/all")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/profile/all"))
                .andExpect(status().isUnauthorized());
    }
}
