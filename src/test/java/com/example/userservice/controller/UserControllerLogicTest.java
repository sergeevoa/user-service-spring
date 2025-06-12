package com.example.userservice.controller;

import com.example.userservice.config.TestJacksonConfig;
import com.example.userservice.dto.UserRequestDTO;
import com.example.userservice.dto.UserResponseDTO;
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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.userservice.util.TestUtils.jsonFieldMatches;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.example.userservice.util.TestUtils.jsonField;

@WebMvcTest(UserController.class)
@Import(TestJacksonConfig.class)
public class UserControllerLogicTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;  //для сериализации в JSON

    @Test
    @DisplayName("POST /api/users/add - successful creation")
    void successfulUserCreation() throws Exception {
        LocalDateTime creationTime = LocalDateTime.now();
        UserRequestDTO request = new UserRequestDTO("Alice", "alice@example.com", 25);
        UserResponseDTO response = new UserResponseDTO(1L, "Alice", "alice@example.com", 25,
                creationTime);

        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonField("$.id", 1L))
                .andExpect(jsonField("$.name", "Alice"))
                .andExpect(jsonField("$.email", "alice@example.com"))
                .andExpect(jsonField("$.age", 25))
                .andExpect(jsonFieldMatches("$.createdAt",
                        startsWith(creationTime.truncatedTo(ChronoUnit.SECONDS).toString())));
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
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonField("$.id", 1L))
                .andExpect(jsonField("$.name", "Alice"))
                .andExpect(jsonField("$.email", "alice@example.com"))
                .andExpect(jsonField("$.age", 25))
                .andExpect(jsonFieldMatches("$.createdAt",
                        startsWith(creationTime.truncatedTo(ChronoUnit.SECONDS).toString())));
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
        LocalDateTime creationTime1 = LocalDateTime.now();
        LocalDateTime creationTime2 = LocalDateTime.now();
        List<UserResponseDTO> users = List.of(
            new UserResponseDTO(1L, "Alice", "alice@example.com", 25, creationTime1),
            new UserResponseDTO(2L, "Bob", "bob@example.com", 30, creationTime2)
        );

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonField("$[0].id", 1L))
                .andExpect(jsonField("$[0].name", "Alice"))
                .andExpect(jsonField("$[0].email", "alice@example.com"))
                .andExpect(jsonField("$[0].age", 25))
                .andExpect(jsonFieldMatches("$[0].createdAt",
                        startsWith(creationTime1.truncatedTo(ChronoUnit.SECONDS).toString())))
                .andExpect(jsonField("$[1].id", 2L))
                .andExpect(jsonField("$[1].name", "Bob"))
                .andExpect(jsonField("$[1].email", "bob@example.com"))
                .andExpect(jsonField("$[1].age", 30))
                .andExpect(jsonFieldMatches("$[1].createdAt",
                        startsWith(creationTime1.truncatedTo(ChronoUnit.SECONDS).toString())));
    }

    @Test
    @DisplayName("GET /api/users/all - database is empty")
    void getAllUsersDBIsEmpty() throws Exception {
        List<UserResponseDTO> users = new ArrayList<>();

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
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
                .andExpect(jsonField("$.id", 1L))
                .andExpect(jsonField("$.name", "Alice"))
                .andExpect(jsonField("$.email", "alice@example.com"))
                .andExpect(jsonField("$.age", 25))
                .andExpect(jsonFieldMatches("$.createdAt",
                        startsWith(creationTime.truncatedTo(ChronoUnit.SECONDS).toString())));
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
