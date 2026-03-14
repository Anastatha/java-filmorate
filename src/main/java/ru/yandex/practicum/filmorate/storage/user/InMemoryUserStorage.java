package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);

    private final Map<Long, User> users = new HashMap<>();
    private long nextUserId = 1;

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User create(final User user) {
        user.setId(nextUserId++);
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User update(final User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Пользователь с id {} не найден", user.getId());
            throw new NoSuchElementException("Пользователь не найден");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User findById(final Long id) {
        User user = users.get(id);

        if (user == null) {
            log.error("Пользователь с id {} не найден", id);
            throw new NoSuchElementException("Пользователь не найден");
        }

        return user;
    }
}
