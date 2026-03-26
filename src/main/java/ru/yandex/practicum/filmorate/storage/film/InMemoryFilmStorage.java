package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);

    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private long nextFilmId = 1;

    private final Map<Long, MpaRating> mpaRatings = Map.of(

            1L, new MpaRating(1L, "G"),
            2L, new MpaRating(2L, "PG"),
            3L, new MpaRating(3L, "PG-13"),
            4L, new MpaRating(4L, "R"),
            5L, new MpaRating(5L, "NC-17")
    );

    private final Map<Long, Genre> genres = Map.of(
            1L, new Genre(1L, "COMEDY"),
            2L, new Genre(2L, "DRAMA"),
            3L, new Genre(3L, "CARTOON"),
            4L, new Genre(4L, "THRILLER"),
            5L, new Genre(5L, "DOCUMENTARY"),
            6L, new Genre(6L, "ACTION")
    );

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film create(final Film film) {
        validateReleaseDate(film);
        film.setId(nextFilmId++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(final Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Фильм с id {} не найден", film.getId());
            throw new NoSuchElementException("Фильм не найден");
        }

        validateReleaseDate(film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film findById(final Long id) {
        Film film = films.get(id);

        if (film == null) {
            log.error("Фильм с id {} не найден", id);
            throw new NoSuchElementException("Фильм не найден");
        }
        enrichFilm(film);
        return film;
    }

    private void validateReleaseDate(final Film film) {
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            log.error("Дата релиза раньше 28.12.1895");
            throw new IllegalArgumentException(
                    "Дата релиза не может быть раньше 28.12.1895"
            );
        }
    }

    private void enrichFilm(Film film) {
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            film.setMpa(mpaRatings.get(film.getMpa().getId()));
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Genre> filmGenres = film.getGenres().stream()
                    .map(g -> genres.get(g.getId()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            film.setGenres(filmGenres);
        }
    }
}
