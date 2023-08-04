package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipDao;

import java.sql.ResultSet;
import java.sql.SQLException;
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
    public List<Integer> getFriends(int userId) {
        String sql = "SELECT friend_id FROM user_friends WHERE user_id = ?";
        return jdbcTemplate.query(sql, this::makeId, userId);
    }

    private int makeId(ResultSet rs, int rowNum) throws SQLException {
        return rs.getInt("friend_id");
    }

    private int friendshipChecker(int userId, int friendId) {
        List<Integer> result = getFriends(friendId);
        if (result.contains(userId)) {
            return 2;
        } else {
            return 1;
        }
    }
}
