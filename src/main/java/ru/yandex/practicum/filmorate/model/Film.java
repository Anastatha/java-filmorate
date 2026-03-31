package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
public class Film {
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = MAX_DESCRIPTION_LENGTH,
            message = "Максимальная длина описания — 200 символов")
    private String description;

    @NotNull(message = "Дата релиза обязательна")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность должна быть положительным числом")
    private Integer duration;

    private MpaRating mpa;

    private Set<Genre> genres = new HashSet<>();

}
