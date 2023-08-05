package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, this::makeUser);
    }

    @Override
    public User create(User user) throws ValidationException {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES ( ?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        return user;
    }

    @Override
    public User update(User user) throws ValidationException, UserNotFoundException {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public Optional<User> findUserById(int userId) throws UserNotFoundException {
        String sql = "select * from USERS where user_id = " + userId;
        User user = jdbcTemplate.query(sql, rs -> rs.next() ? makeUser(rs, 0) : null);
        if (user == null) {
            throw new UserNotFoundException(String.format("Пользователь c id = %d, не найден", userId));
        }
        return Optional.of(user);
    }

    @Override
    public boolean containsUser(int userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count > 0;
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();

        return User.builder()
                .id(id)
                .email(email)
                .login(login)
                .name(name)
                .birthday(birthday)
                .build();
    }

    @Override
    public Collection<User> getCommonFriends(int userId, int friendId) throws UserNotFoundException {
        String sql = "SELECT u.* FROM users u" +
                " JOIN user_friends uf1 ON u.user_id = uf1.friend_id AND uf1.user_id = ?" +
                " JOIN user_friends uf2 ON u.user_id = uf2.friend_id AND uf2.user_id = ?";
        return jdbcTemplate.query(sql, this::makeUser, userId, friendId);
    }
}
