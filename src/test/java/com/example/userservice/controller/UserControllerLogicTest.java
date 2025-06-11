package com.example.userservice.controller;

import com.example.userservice.config.TestJacksonConfig;
import com.example.userservice.dto.UserRequestDTO;
import com.example.userservice.dto.UserResponseDTO;
import com.example.userservice.service.UserService;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(TestJacksonConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;  //для сериализации в JSON

    @Test
    @DisplayName("POST /api/users/add - successful creation")
    void successfulUserCreation() throws Exception {
        LocalDateTime creationTime = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        UserRequestDTO request = new UserRequestDTO("Alice", "alice@example.com", 25);
        UserResponseDTO response = new UserResponseDTO(1L, "Alice", "alice@example.com", 25,
                creationTime);

        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.age").value(25))
                .andExpect(jsonPath("$.createdAt").value(creationTime.toString()));
    }

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
    @DisplayName("GET /api/users/{id} - user is found")
    void userIsFound() throws Exception {
        LocalDateTime creationTime = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        UserResponseDTO response = new UserResponseDTO(1L, "Alice", "alice@example.com", 25,
                creationTime);

        when(userService.getUserById(1L)).thenReturn(Optional.of(response));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.age").value(25))
                .andExpect(jsonPath("$.createdAt").value(creationTime.toString()));
    }

    @Test
    @DisplayName("GET /api/users/{id} - user is not found")
    void userIsNotFound() throws Exception {
        when(userService.getUserById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/users/all - there are some users in the database")
    void getAllUsersDBIsNotEmpty() throws Exception {
        LocalDateTime creationTime1 = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        LocalDateTime creationTime2 = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        List<UserResponseDTO> users = List.of(
            new UserResponseDTO(1L, "Alice", "alice@example.com", 25, creationTime1),
            new UserResponseDTO(2L, "Bob", "bob@example.com", 30, creationTime2)
        );

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[0].email").value("alice@example.com"))
                .andExpect(jsonPath("$[0].age").value(25))
                .andExpect(jsonPath("$[0].createdAt").value(creationTime1.toString()))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Bob"))
                .andExpect(jsonPath("$[1].email").value("bob@example.com"))
                .andExpect(jsonPath("$[1].age").value(30))
                .andExpect(jsonPath("$[1].createdAt").value(creationTime2.toString()));
    }

    @Test
    @DisplayName("GET /api/users/all - database is empty")
    void getAllUsersDBIsEmpty() throws Exception {
        List<UserResponseDTO> users = new ArrayList<>();

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - successful update")
    void successfulUserUpdate() throws Exception {
        UserRequestDTO request = new UserRequestDTO("Alice", "alice123@example.com", 25);
        LocalDateTime creationTime = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        UserResponseDTO response = new UserResponseDTO(1L, "Alice", "alice@example.com", 25,
                creationTime);

        when(userService.updateUser(any(Long.class), any(UserRequestDTO.class))).thenReturn(Optional.of(response));

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.age").value(25))
                .andExpect(jsonPath("$.createdAt").value(creationTime.toString()));
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

    @Test
    @DisplayName("PUT /api/users/{id} - user is not found")
    void userUpdateFailed9() throws Exception {
        UserRequestDTO request = new UserRequestDTO("Alice", "alice@example.com", 21);

        when(userService.updateUser(any(Long.class), any(UserRequestDTO.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - user is found")
    void successfulUserDelete() throws Exception {
        when(userService.deleteUser(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - user is not found")
    void unsuccessfulUserDelete() throws Exception {
        when(userService.deleteUser(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNotFound());
    }
}
