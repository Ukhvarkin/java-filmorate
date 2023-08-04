package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.FriendshipStatus;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Component
public class FriendshipDaoImpl implements FriendshipDao {
    private final JdbcTemplate jdbcTemplate;

    public FriendshipDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        String sql = "INSERT INTO user_friends (user_id, friend_id, friendship_status_id) " +
                "VALUES (?, ?, ?)";

        int statusId = friendshipChecker(userId, friendId);
        jdbcTemplate.update(sql, userId, friendId, statusId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        String sql = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> getFriends(int userId) {
        String sql = "SELECT u.* FROM users u" +
                " JOIN user_friends AS uf ON u.user_id = uf.friend_id AND uf.user_id = ?";

        return jdbcTemplate.query(sql, this::makeUser, userId);
    }

    private int makeId(ResultSet rs, int rowNum) throws SQLException {
        return rs.getInt("friend_id");
    }

    private int friendshipChecker(int userId, int friendId) {
        List<User> result = getFriends(friendId);
        for (User user : result) {
            if (user.getId() == userId) {
                return 2;
            }
        }
        return 1;
    }

    @Override
    public FriendshipStatus getFriendshipStatusById(int friendshipStatusId) {
        if (friendshipStatusId == 1) {
            return FriendshipStatus.PENDING;
        } else if (friendshipStatusId == 2) {
            return FriendshipStatus.CONFIRMED;
        } else {
            throw new ValidationException("Не найден статус дружбы с id = " + friendshipStatusId);
        }
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
}
