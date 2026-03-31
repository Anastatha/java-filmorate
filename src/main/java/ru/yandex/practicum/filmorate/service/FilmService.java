package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);

    public FilmService(FilmStorage filmStorage,
                       UserStorage userStorage,
                       GenreStorage genreStorage,
                       MpaStorage mpaStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    public void addLike(Long filmId, Long userId) {
        filmStorage.findById(filmId);
        filmStorage.findById(filmId);
        userStorage.findById(userId);

        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        filmStorage.findById(filmId);
        userStorage.findById(userId);

        filmStorage.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }


    private void prepareFilm(Film film) {
        if (film.getMpa() != null) {
            film.setMpa(mpaStorage.findById(film.getMpa().getId()));
        } else {
            throw new IllegalArgumentException("MPA должен быть указан");
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {

            List<Long> ids = film.getGenres().stream()
                    .map(Genre::getId)
                    .toList();

            Collection<Genre> genresFromDb = genreStorage.findAllByIds(ids);

            film.setGenres(new LinkedHashSet<>(genresFromDb));
        } else {
            film.setGenres(new LinkedHashSet<>());
        }

        validateReleaseDate(film.getReleaseDate());
    }

    public Film create(Film film) {
        prepareFilm(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        prepareFilm(film);
        return filmStorage.update(film);
    }

    public Film findById(final Long id) {
        return filmStorage.findById(id);
    }

    private void validateReleaseDate(LocalDate releaseDate) {
        if (releaseDate.isBefore(FIRST_FILM_DATE)) {
            throw new IllegalArgumentException("Дата релиза не может быть раньше 28.12.1895");
        }
    }
}
