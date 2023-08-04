package ru.yandex.practicum.filmorate.storage.dao;

import java.util.List;

public interface FriendshipDao {
    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<Integer> getFriends(int userId);
}
