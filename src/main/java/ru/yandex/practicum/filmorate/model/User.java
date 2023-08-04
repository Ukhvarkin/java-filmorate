package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
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
