package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
  Collection<User> findAll();

  User create(User user) throws ValidationException;

  User update(User user) throws ValidationException, UserNotFoundException;

  User getUser(int userId) throws UserNotFoundException;
}