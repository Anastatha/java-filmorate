package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);

    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1895, 12, 28);
    private long nextFilmId = 1;

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
}
