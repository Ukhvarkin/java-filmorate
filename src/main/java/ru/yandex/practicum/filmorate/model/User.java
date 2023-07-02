package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;

import java.time.LocalDate;

@Data
@Builder
public class User {
  private int id;
  @NotNull(message = "Введите электронную почту.")
  @Email(message = "Электронная почта введена некорректна.")
  private String email;
  @NotNull(message = "Введите логин.")
  @NotBlank(message = "Введите логин без пробелов.")
  private String login;
  private String name;
  @Past
  private LocalDate birthday;
}
