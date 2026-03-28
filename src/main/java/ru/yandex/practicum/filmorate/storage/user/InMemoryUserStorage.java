package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("inMemoryUserStorage")
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

        if (user.getFriends() == null) {
            user.setFriends(new HashMap<>());
        }

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

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = findById(userId);
        findById(friendId);

        user.getFriends().put(friendId, FriendshipStatus.UNCONFIRMED);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        User user = findById(userId);
        user.getFriends().remove(friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        User user = findById(userId);

        return user.getFriends().keySet().stream()
                .map(this::findById)
                .toList();
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        User user = findById(userId);
        User other = findById(otherId);

        Set<Long> commonIds = user.getFriends().keySet().stream()
                .filter(other.getFriends()::containsKey)
                .collect(Collectors.toSet());

        return commonIds.stream()
                .map(this::findById)
                .toList();
    }
}