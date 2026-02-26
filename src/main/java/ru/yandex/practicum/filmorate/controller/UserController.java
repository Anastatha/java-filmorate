package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final Map<Long, User> users = new HashMap<>();
    private long nextUserId = 1;

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody final User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(nextUserId++);
        users.put(user.getId(), user);

        log.info("Создан пользователь: {}", user);

        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody final User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("Id должен быть указан");
        }
        if (!users.containsKey(user.getId())) {
            log.error("Пользователь с id {} не найден", user.getId());
            throw new NoSuchElementException("Пользователь не найден");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
        log.info("Обновлён пользователь: {}", user);

        return user;
    }
}
