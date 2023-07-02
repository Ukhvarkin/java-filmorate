package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {
  UserController userController = new UserController();

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
  @DisplayName("Добавление пользователя.")
  public void createUser() {
    User user = User.builder()
            .name("Имя")
            .login("Login")
            .email("mail@yandex.ru")
            .birthday(LocalDate.of(2000, 12, 12))
            .build();
    userController.create(user);
    assertEquals(1, userController.findAll().size(), "Пользователь не добавлен.");
  }

  @Test
  @DisplayName("Проверка корректности ввода почты.")
  public void shouldThrowExceptionInEmail() {

    User incorrectEmail = generateUser();
    incorrectEmail.setEmail("yandex.ru");
    assertThrows(ValidationException.class, () -> {
              userController.create(incorrectEmail);
            }
    );
  }

  @Test
  @DisplayName("Проверка корректности ввода логина")
  public void shouldThrowExceptionInLogin() {

    User incorrectLogin = generateUser();
    incorrectLogin.setLogin("");
    assertThrows(ValidationException.class, () -> {
              userController.create(incorrectLogin);
            }
    );
  }

  @Test
  @DisplayName("Проверка корректности ввода даты рождения")
  public void shouldThrowExceptionInData() {

    User incorrectData = generateUser();
    incorrectData.setBirthday(LocalDate.of(5858, 12, 12));
    assertThrows(ValidationException.class, () -> {
              userController.create(incorrectData);
            }
    );
  }
}