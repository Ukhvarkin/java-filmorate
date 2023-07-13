package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
  private int id = 1;
  private final Map<Integer, User> users = new HashMap<>();

  @Override
  public Collection<User> findAll() {
    log.debug("Текущее количество пользователей: {}.", users.size());
    return users.values();
  }

  @Override
  public User create(User user) throws ValidationException {
    user.setId(id);
    users.put(id, user);
    id++;
    log.debug("Добавлен пользователь: id:{}. {}.", user.getId(), user.getLogin());
    return user;
  }

  @Override
  public User update(User user) {
    if (users.containsKey(user.getId())) {
      users.put(user.getId(), user);
      log.info("Пользователь обновлен.");
    } else {
      String message = "Пользователя с id = " + user.getId() + ", не найдено.";
      log.warn(message);
      throw new UserNotFoundException(message);
    }
    return user;
  }

  @Override
  public User getUser(int userId) {
    if (!users.containsKey(userId)) {
      throw new UserNotFoundException("Не найден пользователя с id = " + userId);
    }
    return users.get(userId);
  }
}
