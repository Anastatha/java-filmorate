package ru.yandex.practicum.filmorate;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private User createValidUser() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }

    @Test
    void shouldFailWhenEmailBlank() {
        User user = createValidUser();
        user.setEmail("");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void shouldFailWhenEmailInvalid() {
        User user = createValidUser();
        user.setEmail("wrong-email");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void shouldFailWhenLoginContainsSpaces() {
        User user = createValidUser();
        user.setLogin("log in");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void shouldFailWhenBirthdayInFuture() {
        User user = createValidUser();
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertThat(violations).isNotEmpty();
    }
}