package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validateReleaseDate(film);

        film.setId(getNextId());

        films.put(film.getId(), film);

        log.info("Добавлен фильм: {}", film);

        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (film.getId() == null) {
            throw new IllegalArgumentException("Id должен быть указан");
        }
        if (!films.containsKey(film.getId())) {
            log.error("Фильм с id {} не найден", film.getId());
            throw new NoSuchElementException("Фильм не найден");
        }

        validateReleaseDate(film);

        films.put(film.getId(), film);

        log.info("Обновлён фильм: {}", film);

        return film;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            log.error("Дата релиза раньше 28.12.1895");
            throw new IllegalArgumentException("Дата релиза не может быть раньше 28.12.1895");
        }
    }
}
