package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(final Long userId, final Long friendId) {
        userStorage.findById(userId);
        userStorage.findById(friendId);
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        userStorage.findById(userId);
        userStorage.findById(friendId);

        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        userStorage.findById(userId);
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        userStorage.findById(userId);
        userStorage.findById(otherId);

        return userStorage.getCommonFriends(userId, otherId);
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User create(final User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        return userStorage.create(user);
    }

    public User update(final User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        boolean updated = userStorage.update(user);
        if (!updated) {
            throw new NoSuchElementException("Пользователь не найден");
        }

        return user;
    }

    public User findById(final Long id) {
        return userStorage.findById(id);
    }

}
