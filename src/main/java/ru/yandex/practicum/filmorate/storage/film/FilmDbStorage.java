package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> getFilms() {
        String sql = """
                    SELECT f.*, m.name AS mpa_name
                    FROM films f
                    JOIN mpa_ratings m ON f.mpa_id = m.id
                """;

        List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper());
        films.forEach(this::loadGenres);
        return films;
    }

    @Override
    public Film findById(Long id) {
        String sql = """
                    SELECT f.*, m.name AS mpa_name
                    FROM films f
                    JOIN mpa_ratings m ON f.mpa_id = m.id
                    WHERE f.id = ?
                """;

        Film film = jdbcTemplate.query(sql, new FilmRowMapper(), id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Фильм не найден"));

        loadGenres(film);
        return film;
    }

    @Override
    public Film create(Film film) {
        String sql = """
                    INSERT INTO films(name, description, release_date, duration, mpa_id)
                    VALUES (?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );

        Long id = jdbcTemplate.queryForObject("SELECT MAX(id) FROM films", Long.class);
        film.setId(id);

        saveGenres(film);

        return findById(id);
    }

    @Override
    public Film update(Film film) {
        String sql = """
                    UPDATE films
                    SET name=?, description=?, release_date=?, duration=?, mpa_id=?
                    WHERE id=?
                """;

        int updated = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        if (updated == 0) {
            throw new NoSuchElementException("Фильм не найден");
        }

        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id=?", film.getId());
        saveGenres(film);

        return findById(film.getId());
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO likes(film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM likes WHERE film_id=? AND user_id=?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = """
                    SELECT f.*, m.name AS mpa_name, COUNT(l.user_id) AS likes_count
                    FROM films f
                    JOIN mpa_ratings m ON f.mpa_id = m.id
                    LEFT JOIN likes l ON f.id = l.film_id
                    GROUP BY f.id, m.name
                    ORDER BY likes_count DESC
                    LIMIT ?
                """;

        List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper(), count);
        films.forEach(this::loadGenres);
        return films;
    }

    private void loadGenres(Film film) {
        String sql = """
                    SELECT g.id, g.name
                    FROM genres g
                    JOIN film_genres fg ON g.id = fg.genre_id
                    WHERE fg.film_id = ?
                """;

        Set<Genre> genres = new HashSet<>(jdbcTemplate.query(sql,
                new GenreRowMapper(),
                film.getId()
        ));

        film.setGenres(genres);
    }

    private void saveGenres(Film film) {
        if (film.getGenres() == null) return;

        String sql = "INSERT INTO film_genres(film_id, genre_id) VALUES (?, ?)";

        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sql, film.getId(), genre.getId());
        }
    }
}