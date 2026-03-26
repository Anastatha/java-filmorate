package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;

@Component
public class InMemoryMpaStorage implements MpaStorage {

    private final Map<Long, MpaRating> mpaRatings = Map.of(
            1L, new MpaRating(1L, "G"),
            2L, new MpaRating(2L, "PG"),
            3L, new MpaRating(3L, "PG-13"),
            4L, new MpaRating(4L, "R"),
            5L, new MpaRating(5L, "NC-17")
    );

    @Override
    public Collection<MpaRating> getAll() {
        return mpaRatings.values();
    }

    @Override
    public MpaRating findById(Long id) {
        MpaRating mpa = mpaRatings.get(id);
        if (mpa == null) {
            throw new NoSuchElementException("MPA с id " + id + " не найден");
        }
        return mpa;
    }
}