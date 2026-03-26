package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(final Long userId, final Long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);

        user.getFriends().put(friendId, FriendshipStatus.UNCONFIRMED);
    }

    public void confirmFriend(Long userId, Long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);

        if (friend.getFriends().get(userId) == FriendshipStatus.UNCONFIRMED) {
            friend.getFriends().put(userId, FriendshipStatus.CONFIRMED);
            user.getFriends().put(friendId, FriendshipStatus.CONFIRMED);
        } else {
            throw new NoSuchElementException("Заявка на дружбу не найдена");
        }
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        User user = userStorage.findById(userId);
        User other = userStorage.findById(otherId);

        Set<Long> commonIds = user.getFriends().entrySet().stream()
                .filter(e -> e.getValue() == FriendshipStatus.CONFIRMED)
                .map(Map.Entry::getKey)
                .filter(id -> other.getFriends().get(id) == FriendshipStatus.CONFIRMED)
                .collect(Collectors.toSet());

        return commonIds.stream()
                .map(userStorage::findById)
                .toList();
    }

    public List<User> getFriends(Long userId) {
        User user = userStorage.findById(userId);

        return user.getFriends().entrySet().stream()
//                .filter(entry -> entry.getValue() == FriendshipStatus.CONFIRMED)
                .map(entry -> userStorage.findById(entry.getKey()))
                .toList();
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

        return userStorage.update(user);
    }

    public User findById(final Long id) {
        return userStorage.findById(id);
    }

}
