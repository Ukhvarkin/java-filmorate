package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
  private int id = 1;
  private final Map<Integer, User> users = new HashMap<>();

  @GetMapping
  public Collection<User> findAll() {
    log.info("GET запрос.");
    log.debug("Добавлен пользователь: {}.", users.size());
    return new ArrayList<>(users.values());
  }

  @PostMapping
  public User create(@Valid @RequestBody User user) throws ValidationException {
    log.info("Post запрос");
    userValidator(user);
    user.setId(id);
    users.put(id, user);
    id++;
    log.debug("Добавлен пользователь: {}.", user.getLogin());
    return user;
  }

  @PutMapping
  public User update(@Valid @RequestBody User user) throws ValidationException {
    log.info("PUT запрос");
    userValidator(user);
    if (users.containsKey(user.getId())) {
      users.put(user.getId(), user);
      log.info("Пользователь обновлен.");
    }else {
      String message = "Пользователя с таким id не найдено.";
      log.warn(message);
      throw new ValidationException(message);
    }
    return user;
  }

  private void userValidator(User user) throws ValidationException {
    if (user == null){
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
    } else if (user.getBirthday().isAfter(LocalDate.now())) {
      String message = "Дата рождения не может быть в будущем.";
      log.warn(message);
      throw new ValidationException(message);
    }
  }

}
