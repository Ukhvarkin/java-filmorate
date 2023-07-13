package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public Collection<User> findAll() {
    log.info("Получен запрос на получение списка всех пользователей.");
    return userService.findAll();
  }

  @GetMapping("/{id}")
  public User getUser(@PathVariable int id) {
    log.info("Получен запрос на получение пользователя.");
    userCheckerId(id);
    return userService.getUserById(id);
  }

  @GetMapping("/{id}/friends")
  public Collection<User> getFriendsList(@PathVariable int id) {
    log.info("Получен запрос на получение списка друзей пользователя.");
    return userService.getFriendsList(id);
  }

  @GetMapping("{id}/friends/common/{otherId}")
  public Collection<User> getCommonFriends(@PathVariable int id,
                                           @PathVariable int otherId) {
    log.info("Получен запрос на получение списка всех пользователей.");
    return userService.getCommonFriends(id, otherId);
  }

  @PostMapping
  public User create(@Valid @RequestBody User user) throws ValidationException {
    log.info("Получен запрос на добавление пользователя.");
    userValidator(user);
    return userService.create(user);
  }

  @PutMapping
  public User update(@Valid @RequestBody User user) throws ValidationException {
    log.info("Получен запрос на обновление пользователя.");
    userValidator(user);
    return userService.update(user);
  }

  @PutMapping("/{id}/friends/{friendId}")
  public void addFriend(@PathVariable int id,
                        @PathVariable int friendId) {
    log.info("Получен запрос на добавление в друзья.");
    userCheckerId(id);
    userCheckerId(friendId);
    userService.addFriend(id, friendId);
  }

  @DeleteMapping("{id}/friends/{friendId}")
  public void deleteFriend(@PathVariable int id,
                           @PathVariable int friendId) {
    log.info("Получен запрос на удаление из друзей.");
    userCheckerId(id);
    userCheckerId(friendId);
    userService.deleteFriend(id, friendId);
  }

  private void userCheckerId(int userId) {
    if (userId <= 0) {
      throw new UserNotFoundException("Не найден пользователь с id: " + userId);
    }
  }


  private void userValidator(User user) throws ValidationException {
    if (user == null) {
      String message = "Некорректный ввод. Передан пустой пользователь.";
      log.warn(message);
      throw new NullPointerException(message);
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

}
