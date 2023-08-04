package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> findAll();

    User create(User user) throws ValidationException;

    User update(User user) throws ValidationException, UserNotFoundException;

    Optional<User> findUserById(int id) throws UserNotFoundException;

    boolean containsUser(int userId);

    Collection<User> getCommonFriends(int userId, int friendId) throws UserNotFoundException;

}
