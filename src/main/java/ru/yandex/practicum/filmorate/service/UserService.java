package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {
  private final UserStorage userStorage;

  @Autowired
  public UserService(UserStorage userStorage) {
    this.userStorage = userStorage;
  }

  public Collection<User> findAll() {
    return userStorage.findAll();
  }

  public User create(User user) throws ValidationException {
    return userStorage.create(user);
  }

  public User update(User user) throws ValidationException, UserNotFoundException {
    return userStorage.update(user);
  }

  public void addFriend(int userId, int friendId) throws UserNotFoundException {
    userStorage.getUser(userId).addFriend(friendId);
    userStorage.getUser(friendId).addFriend(userId);
    log.debug("Пользователи с id:{} и id:{} - друзья.", userId, friendId);
  }

  public void deleteFriend(int userId, int friendId) throws UserNotFoundException {
    userStorage.getUser(userId).deleteFriend(friendId);
    userStorage.getUser(friendId).deleteFriend(userId);
    log.debug("Пользователи с id:{} и id:{} - больше не друзья.", userId, friendId);
  }

  public Collection<User> getFriendsList(int userId) throws UserNotFoundException {
    List<User> allFriends = new ArrayList<>();

    for (int id : userStorage.getUser(userId).getFriends()) {
      allFriends.add(getUserById(id));
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
    return userStorage.getUser(userId);
  }

}
