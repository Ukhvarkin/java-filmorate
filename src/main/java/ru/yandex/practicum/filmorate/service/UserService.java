package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {
  private final UserStorage userStorage;
  private final UserIdGenerator userIdGenerator;

  @Autowired
  public UserService(UserStorage userStorage, UserIdGenerator userIdGenerator) {
    this.userStorage = userStorage;
    this.userIdGenerator = userIdGenerator;
  }

  public Collection<User> findAll() {
    return userStorage.findAll();
  }

  public User create(User user) throws ValidationException {
    userValidator(user);
    user.setId(userIdGenerator.generateId());
    return userStorage.create(user);
  }

  public User update(User user) throws ValidationException, UserNotFoundException {
    userValidator(user);
    if (userStorage.containsUser(user.getId())) {
      return userStorage.update(user);
    } else {
      String message = "Пользователя с id = " + user.getId() + ", не найдено.";
      log.warn(message);
      throw new UserNotFoundException(message);
    }
  }

  public void addFriend(int userId, int friendId) throws UserNotFoundException {
    validateUserId(userId);
    validateUserId(friendId);
    userStorage.getUser(userId).addFriend(friendId);
    userStorage.getUser(friendId).addFriend(userId);
    log.debug("Пользователи с id:{} и id:{} - друзья.", userId, friendId);
  }

  public void deleteFriend(int userId, int friendId) throws UserNotFoundException {
    validateUserId(userId);
    validateUserId(friendId);
    userStorage.getUser(userId).deleteFriend(friendId);
    userStorage.getUser(friendId).deleteFriend(userId);
    log.debug("Пользователи с id:{} и id:{} - больше не друзья.", userId, friendId);
  }

  public Collection<User> getFriendsList(int userId) throws UserNotFoundException {
    List<User> allFriends = new ArrayList<>();

    for (int id : userStorage.getUser(userId).getFriends()) {
      allFriends.add(userStorage.getUser(id));
    }
    return allFriends;
  }

  public Collection<User> getCommonFriends(int userId, int friendId) throws UserNotFoundException {
    Set<Integer> userFriends = userStorage.getUser(userId).getFriends();
    Set<Integer> friendFriends = userStorage.getUser(friendId).getFriends();

    List<User> commonFriends = new ArrayList<>();
    for (int id : userFriends) {
      if (friendFriends.contains(id)) {
        commonFriends.add(userStorage.getUser(id));
      }
    }
    return commonFriends;
  }

  public User getUserById(int userId) throws UserNotFoundException {
    validateUserId(userId);
    return userStorage.getUser(userId);
  }

  private void userValidator(User user) throws ValidationException {
    if (user == null) {
      String message = "Некорректный ввод. Передан пустой пользователь.";
      log.warn(message);
      throw new ValidationException(message);
    }
    if (user.getEmail() == null || user.getLogin() == null || user.getBirthday() == null) {
      String message = "Некорректный ввод, есть пустые поля.";
      log.warn(message);
      throw new ValidationException(message);
    }
    if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
      String message = "Некорректный ввод электронной почты.";
      log.warn(message);
      throw new ValidationException(message);
    }
    if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
      String message = "Некорректный ввод. Логин не должен содержать пробелов.";
      log.warn(message);
      throw new ValidationException(message);
    }
    if (user.getName() == null || user.getName().isBlank()) {
      user.setName(user.getLogin());
      log.debug("Пользователю присвоено имя: {}.", user.getName());
    }
    if (user.getBirthday().isAfter(LocalDate.now())) {
      String message = "Дата рождения не может быть в будущем.";
      log.warn(message);
      throw new ValidationException(message);
    }
  }

  private void validateUserId(int userId) {
    if (userId <= 0) {
      throw new UserNotFoundException("Не найден пользователь с id: " + userId);
    }
  }
}
