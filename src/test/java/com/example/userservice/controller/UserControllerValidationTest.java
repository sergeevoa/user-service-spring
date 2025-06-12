package com.example.userservice.controller;

import com.example.userservice.config.TestJacksonConfig;
import com.example.userservice.dto.UserRequestDTO;
import com.example.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UserController.class)
@Import(TestJacksonConfig.class)
public class UserControllerValidationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/users/add - validation failed: name is blank")
    void userCreationFailed1() throws Exception {
        UserRequestDTO request = new UserRequestDTO("", "alice@example.com", 25);

        mockMvc.perform(post("/api/users/add")
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Name is required"));
    }

    @Test
    @DisplayName("POST /api/users/add - validation failed: name is too long")
    void userCreationFailed2() throws Exception {
        UserRequestDTO request = new UserRequestDTO("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                "alice@example.com", 25);

        mockMvc.perform(post("/api/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Name must be at most 50 characters"));
    }

    @Test
    @DisplayName("POST /api/users/add - validation failed: email is blank")
    void userCreationFailed3() throws Exception {
        UserRequestDTO request = new UserRequestDTO("Alice", "", 25);

        mockMvc.perform(post("/api/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("Email is required"));
    }

    @Test
    @DisplayName("POST /api/users/add - validation failed: email is not valid")
    void userCreationFailed4() throws Exception {
        UserRequestDTO request = new UserRequestDTO("Alice", "aliceexample.com", 25);

        mockMvc.perform(post("/api/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("Invalid email format"));
    }

    @Test
    @DisplayName("POST /api/users/add - validation failed: email is too long")
    void userCreationFailed5() throws Exception {
        UserRequestDTO request = new UserRequestDTO("Alice", "alice@eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" +
                "eeeeeeeeeeeeeeeexample.com", 25);

        mockMvc.perform(post("/api/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("Email must be at most 50 characters"));
    }

    @Test
    @DisplayName("POST /api/users/add - validation failed: age is null")
    void userCreationFailed6() throws Exception {
        UserRequestDTO request = new UserRequestDTO("Alice", "alice@example.com", null);

        mockMvc.perform(post("/api/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.age").value("Age is required"));
    }

    @Test
    @DisplayName("POST /api/users/add - validation failed: age value is less than minimum")
    void userCreationFailed7() throws Exception {
        UserRequestDTO request = new UserRequestDTO("Alice", "alice@example.com", -1);

        mockMvc.perform(post("/api/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.age").value("Age must be at least 0"));
    }

    @Test
    @DisplayName("POST /api/users/add - validation failed: age value is greater than maximum")
    void userCreationFailed8() throws Exception {
        UserRequestDTO request = new UserRequestDTO("Alice", "alice@example.com", 121);

        mockMvc.perform(post("/api/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.age").value("Age must be at most 120"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - validation failed: name is blank")
    void userUpdateFailed1() throws Exception {
        UserRequestDTO request = new UserRequestDTO("", "alice@example.com", 25);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Name is required"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - validation failed: name is too long")
    void userUpdateFailed2() throws Exception {
        UserRequestDTO request = new UserRequestDTO("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                "alice@example.com", 25);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Name must be at most 50 characters"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - validation failed: email is blank")
    void userUpdateFailed3() throws Exception {
        UserRequestDTO request = new UserRequestDTO("Alice", "", 25);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("Email is required"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - validation failed: email is not valid")
    void userUpdateFailed4() throws Exception {
        UserRequestDTO request = new UserRequestDTO("Alice", "aliceexample.com", 25);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("Invalid email format"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - validation failed: email is too long")
    void userUpdateFailed5() throws Exception {
        UserRequestDTO request = new UserRequestDTO("Alice", "alice@eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" +
                "eeeeeeeeeeeeeeeexample.com", 25);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("Email must be at most 50 characters"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - validation failed: age is null")
    void userUpdateFailed6() throws Exception {
        UserRequestDTO request = new UserRequestDTO("Alice", "alice@example.com", null);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.age").value("Age is required"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - validation failed: age value is less than minimum")
    void userUpdateFailed7() throws Exception {
        UserRequestDTO request = new UserRequestDTO("Alice", "alice@example.com", -1);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.age").value("Age must be at least 0"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - validation failed: age value is greater than maximum")
    void userUpdateFailed8() throws Exception {
        UserRequestDTO request = new UserRequestDTO("Alice", "alice@example.com", 121);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.age").value("Age must be at most 120"));
    }
}
