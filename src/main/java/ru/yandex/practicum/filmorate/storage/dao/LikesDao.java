package ru.yandex.practicum.filmorate.storage.dao;

public interface LikesDao {
    void addLike(int userId, int filmId);

    void deleteLike(int userId, int filmId);

    int likesCount(int filmId);
}
