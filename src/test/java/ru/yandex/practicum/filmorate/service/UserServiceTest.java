package ru.yandex.practicum.filmorate.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

  UserService userService;
  User user;

  @BeforeEach
  void start() {
    UserStorage userStorage = new InMemoryUserStorage();
    UserIdGenerator userIdGenerator = new UserIdGenerator();

    userService = new UserService(userStorage, userIdGenerator);
  }

  private User generateUser() {
    return User.builder()
            .email("email@yandex.ru")
            .login("Login")
            .name("Name")
            .birthday(LocalDate.of(2000, 12, 12))
            .build();
  }

  @Test
  @DisplayName("Проверка добавления в список друзей.")
  public void shouldAddFriend() {
    User user1 = generateUser();
    User user2 = generateUser();

    userService.create(user1);
    userService.create(user2);
    userService.addFriend(1, 2);

    assertTrue(user1.getFriends().contains(2), "Пользователи не добавлены в друзья друг другу");
    assertTrue(user2.getFriends().contains(1), "Пользователи не добавлены в друзья друг другу");
  }

  @Test
  @DisplayName("Проверка на удаление из списка друзей.")
  public void shouldDeleteFriend() {
    User user1 = generateUser();
    User user2 = generateUser();

    userService.create(user1);
    userService.create(user2);
    userService.addFriend(1, 2);
    userService.deleteFriend(1, 2);

    assertFalse(user1.getFriends().contains(2), "Пользователи не добавлены в друзья друг другу");
    assertFalse(user2.getFriends().contains(1), "Пользователи не добавлены в друзья друг другу");
  }

  @Test
  @DisplayName("Проверка на получения списка друзей.")
  public void shouldFindAllUserFriends() {
    User user1 = generateUser();
    User user2 = generateUser();
    User user3 = generateUser();

    userService.create(user1);
    userService.create(user2);
    userService.create(user3);

    int user1Id = user1.getId();
    int user2Id = user2.getId();
    int user3Id = user3.getId();

    userService.addFriend(user1Id, user2Id);
    userService.addFriend(user1Id, user3Id);
    assertEquals(2, userService.getFriendsList(user1Id).size(), "Неверное количество.");

    assertTrue(userService.getFriendsList(user1Id).contains(user2),
            "Пользователи не добавлены в друзья друг другу");
    assertTrue(userService.getFriendsList(user1Id).contains(user3),
            "Пользователи не добавлены в друзья друг другу");
  }

  @Test
  @DisplayName("Проверка на получения списка общих друзей.")
  public void shouldFindCommonFriends() {
    User user1 = generateUser();
    User user2 = generateUser();
    User user3 = generateUser();

    userService.create(user1);
    userService.create(user2);
    userService.create(user3);

    int user1Id = user1.getId();
    int user2Id = user2.getId();
    int user3Id = user3.getId();

    userService.addFriend(user1Id, user3Id);
    userService.addFriend(user2Id, user3Id);

    assertTrue(userService.getCommonFriends(user2Id, user1Id).contains(user3), "Общего User не найдено.");
  }

  @Test
  @DisplayName("Проверка эксепшена при поиске пользователя.")
  public void shouldThrowExceptionUserNotFoundWhenFindById() {
    int incorrectUserId = 9999;

    assertThrows(UserNotFoundException.class, () -> userService.getUserById(incorrectUserId));
  }

  @Test
  @DisplayName("Проверка эксепшена при добавлении в друзья.")
  public void shouldThrowExceptionUserNotFoundWhenAddFriend() {
    User user1 = generateUser();
    userService.create(user1);

    int userId1 = user1.getId();
    int incorrectUserId = 9999;

    assertThrows(UserNotFoundException.class, () -> userService.addFriend(userId1, incorrectUserId));
    assertThrows(UserNotFoundException.class, () -> userService.addFriend(incorrectUserId, userId1));
  }

  @Test
  @DisplayName("Проверка эксепшена при удалении из друзей.")
  public void shouldThrowExceptionUserNotFoundWhenDeleteFriend() {
    User user1 = generateUser();
    userService.create(user1);

    int userId1 = user1.getId();
    int incorrectUserId = 9999;

    assertThrows(UserNotFoundException.class, () -> userService.deleteFriend(userId1, incorrectUserId));
    assertThrows(UserNotFoundException.class, () -> userService.deleteFriend(incorrectUserId, userId1));
  }

  @Test
  @DisplayName("Проверка эксепшена при вызове списка друзей.")
  public void shouldThrowExceptionUserNotFoundWhenGetFriendsList() {
    int incorrectUserId = 9999;

    assertThrows(UserNotFoundException.class, () -> userService.getFriendsList(incorrectUserId));
  }

  @Test
  @DisplayName("Проверка эксепшена при вызове общего списка друзей.")
  public void shouldThrowExceptionUserNotFoundWhenGetCommonFriends() {
    User user1 = generateUser();
    userService.create(user1);

    int userId1 = user1.getId();
    int incorrectUserId = 9999;

    assertThrows(UserNotFoundException.class, () -> userService.getCommonFriends(incorrectUserId, userId1));
    assertThrows(UserNotFoundException.class, () -> userService.getCommonFriends(userId1, incorrectUserId));
  }
}