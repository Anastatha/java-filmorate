package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

@Component
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> getUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );

        Long id = jdbcTemplate.queryForObject("SELECT MAX(id) FROM users", Long.class);
        user.setId(id);

        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE id=?";

        int updated = jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );

        if (updated == 0) {
            throw new NoSuchElementException("Пользователь не найден");
        }

        return user;
    }

    @Override
    public User findById(Long id) {
        String sql = "SELECT * FROM users WHERE id=?";

        return jdbcTemplate.query(sql, new UserRowMapper(), id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));
    }

    //    @Override
//    public void addFriend(Long userId, Long friendId) {
//        String sql = "INSERT INTO friends(user_id, friend_id) VALUES (?, ?)";
//        jdbcTemplate.update(sql, userId, friendId);
//    }
    @Override
    public void addFriend(Long userId, Long friendId) {
        String sql = "MERGE INTO friendships (user_id, friend_id, status) KEY(user_id, friend_id) VALUES (?, ?, 'CONFIRMED')";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM friendships WHERE user_id=? AND friend_id=?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        String sql = "SELECT u.* FROM users u JOIN friendships f ON u.id = f.friend_id WHERE f.user_id = ?";
        return jdbcTemplate.query(sql, new UserRowMapper(), userId);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        String sql = "SELECT u.* FROM users u WHERE u.id IN (SELECT f1.friend_id FROM friendships f1 JOIN friendships f2 ON f1.friend_id = f2.friend_id WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbcTemplate.query(sql, new UserRowMapper(), userId, otherId);
    }
}