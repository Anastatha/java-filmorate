package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;

@Component
public class InMemoryGenreStorage implements GenreStorage {

    private final Map<Long, Genre> genres = Map.of(
            1L, new Genre(1L, "COMEDY"),
            2L, new Genre(2L, "DRAMA"),
            3L, new Genre(3L, "CARTOON"),
            4L, new Genre(4L, "THRILLER"),
            5L, new Genre(5L, "DOCUMENTARY"),
            6L, new Genre(6L, "ACTION")
    );

    @Override
    public Collection<Genre> getAll() {
        return genres.values();
    }

    @Override
    public Genre findById(Long id) {
        Genre genre = genres.get(id);
        if (genre == null) {
            throw new NoSuchElementException("Жанр с id " + id + " не найден");
        }
        return genre;
    }
}