package ru.yandex.practicum.filmorate.storage.dao;

import ru.yandex.practicum.filmorate.enums.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendshipDao {
    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<User> getFriends(int userId);
}
