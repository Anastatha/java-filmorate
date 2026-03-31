package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(UserDbStorage.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final UserDbStorage userStorage;

    @Test
    void testCreateAndFindUser() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("test_login");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        userStorage.create(user);

        User foundUser = userStorage.findById(user.getId());

        assertThat(foundUser.getId()).isEqualTo(user.getId());
        assertThat(foundUser.getEmail()).isEqualTo("test@mail.com");
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setLogin("login");
        user.setName("Name");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        userStorage.create(user);

        user.setName("Updated Name");
        userStorage.update(user);

        User updated = userStorage.findById(user.getId());

        assertThat(updated.getName()).isEqualTo("Updated Name");
    }
}
