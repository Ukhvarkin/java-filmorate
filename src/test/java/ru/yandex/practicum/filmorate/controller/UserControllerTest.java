package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {
  UserController userController;
  User user;

  @BeforeEach
  void start() {
    userController = new UserController();
    user = generateUser();
  }

  private User generateUser() {
    return User.builder()
            .id(1)
            .email("email@yandex.ru")
            .login("Login")
            .name("Name")
            .birthday(LocalDate.of(2000, 12, 12))
            .build();
  }

  @Test
  @DisplayName("Добавление пользователей и получение всего списка.")
  public void createUser() {
    User user1 = user;
    User user2 = user;
    User user3 = user;

    userController.create(user1);
    userController.create(user2);
    userController.create(user3);

    assertEquals(3, userController.findAll().size(), "Неверное количество пользователей.");
  }

  @Test
  @DisplayName("Проверка на обновление пользователя.")
  public void updateUserTest() {
    userController.create(user);

    User updateUser = user;
    updateUser.setEmail("yandex@yandex.ru");
    updateUser.setLogin("Yandex");
    updateUser.setName("Яндекс");
    updateUser.setBirthday(LocalDate.of(1997, 9, 23));

    int id = user.getId();
    updateUser.setId(id);

    userController.update(updateUser);
    assertEquals(updateUser.getEmail(), user.getEmail(), "Почта не обновлена.");
    assertEquals(updateUser.getLogin(), user.getLogin(), "Логин не обновлен.");
    assertEquals(updateUser.getName(), user.getName(), "Имя не обновлено.");
    assertEquals(updateUser.getBirthday(), user.getBirthday(), "Дата рождения не обновлена.");
  }

  @Test
  @DisplayName("Проверка корректности ввода почты.")
  public void shouldThrowExceptionInEmail() {

    User incorrectEmail = user;
    incorrectEmail.setEmail("yandex.ru");
    assertThrows(ValidationException.class, () -> {
              userController.create(incorrectEmail);
            }
    );
  }

  @Test
  @DisplayName("Проверка корректности ввода логина")
  public void shouldThrowExceptionInLogin() {

    User incorrectLogin = user;
    incorrectLogin.setLogin("");
    assertThrows(ValidationException.class, () -> {
              userController.create(incorrectLogin);
            }
    );
  }

  @Test
  @DisplayName("Проверка корректности ввода даты рождения")
  public void shouldThrowExceptionInData() {

    User incorrectData = user;
    incorrectData.setBirthday(LocalDate.of(5858, 12, 12));
    assertThrows(ValidationException.class, () -> {
              userController.create(incorrectData);
            }
    );
  }

}