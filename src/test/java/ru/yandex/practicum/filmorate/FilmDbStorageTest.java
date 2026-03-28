package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, MpaDbStorage.class, GenreDbStorage.class, UserDbStorage.class})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;
    private final MpaDbStorage mpaStorage;
    private final GenreDbStorage genreStorage;
    private final UserDbStorage userStorage; // ✅ ДОБАВИЛИ

    @Test
    void testCreateAndFindFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        film.setMpa(mpaStorage.findById(1L));
        film.setGenres(Set.of(genreStorage.findById(1L)));

        filmStorage.create(film);

        Film found = filmStorage.findById(film.getId());

        assertThat(found.getId()).isEqualTo(film.getId());
        assertThat(found.getName()).isEqualTo("Test Film");

        assertThat(found.getMpa()).isNotNull();
        assertThat(found.getMpa().getId()).isEqualTo(1L);

        assertThat(found.getGenres()).hasSize(1);
    }

    @Test
    void testAddLike() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        userStorage.create(user);

        Film film = new Film();
        film.setName("Film");
        film.setDescription("Desc");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(100);
        film.setMpa(mpaStorage.findById(1L));

        filmStorage.create(film);

        filmStorage.addLike(film.getId(), user.getId());

        List<Film> popular = filmStorage.getPopularFilms(10);

        assertThat(popular).isNotEmpty();
    }
}