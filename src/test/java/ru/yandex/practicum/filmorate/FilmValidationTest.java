package ru.yandex.practicum.filmorate;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class FilmValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private Film createValidFilm() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        return film;
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        Film film = createValidFilm();
        film.setName("");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void shouldFailWhenDescriptionTooLong() {
        Film film = createValidFilm();
        film.setDescription("a".repeat(201));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void shouldPassWhenDescription200Symbols() {
        Film film = createValidFilm();
        film.setDescription("a".repeat(200));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailWhenDurationZero() {
        Film film = createValidFilm();
        film.setDuration(0);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertThat(violations).isNotEmpty();
    }

    @Test
    void shouldFailWhenReleaseDateBeforeCinemaBirthday() {
        Film film = createValidFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        assertThat(film.getReleaseDate())
                .isBefore(LocalDate.of(1895, 12, 28));
    }
}