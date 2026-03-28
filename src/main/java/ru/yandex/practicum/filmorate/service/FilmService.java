package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       GenreStorage genreStorage,
                       MpaStorage mpaStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.findById(filmId);
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

    public Film create(Film film) {
        if (film.getMpa() != null) {
            Long mpaId = film.getMpa().getId();
            film.setMpa(mpaStorage.findById(mpaId));
        } else {
            throw new IllegalArgumentException("MPA должен быть указан");
        }

        Set<Genre> genres = new HashSet<>();
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                genres.add(genreStorage.findById(genre.getId()));
            }
        }
        film.setGenres(genres);

        return filmStorage.create(film);
    }

    public Film update(Film film) {
        if (film.getMpa() != null) {
            film.setMpa(mpaStorage.findById(film.getMpa().getId()));
        } else {
            throw new IllegalArgumentException("MPA должен быть указан");
        }

        Set<Genre> genres = new HashSet<>();
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                genres.add(genreStorage.findById(genre.getId()));
            }
        }
        film.setGenres(genres);

        return filmStorage.update(film);
    }

    public Film findById(final Long id) {
        return filmStorage.findById(id);
    }
}
