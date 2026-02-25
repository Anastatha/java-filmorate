package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.UserController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturn400WhenEmptyBody() throws Exception {
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenInvalidEmail() throws Exception {
        String invalidJson = """
                {
                    "email": "wrong",
                    "login": "login",
                    "birthday": "2000-01-01"
                }
                """;

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(invalidJson)).andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateUserWhenValid() throws Exception {
        String validJson = """
                {
                    "email": "test@mail.com",
                    "login": "login",
                    "birthday": "2000-01-01"
                }
                """;

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(validJson)).andExpect(status().isOk());
    }
}