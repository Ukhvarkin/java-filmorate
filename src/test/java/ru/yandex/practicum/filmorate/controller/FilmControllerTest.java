package ru.yandex.practicum.filmorate.controller;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
class FilmControllerTest {
  FilmController filmController = new FilmController();

  private Film generateFilm() {
    return Film.builder()
            .name("Фильм")
            .description("Описание")
            .releaseDate(LocalDate.of(2000, 12, 12))
            .duration(178)
            .build();
  }

  @Test
  @DisplayName("Добавление фильма")
  public void createFilm() {
    Film film = Film.builder()
            .name("Властелин Колец: Братство Кольца")
            .description("Сказания о Средиземье — это хроника Великой войны за Кольцо, " +
                    "длившейся не одну тысячу лет. ")
            .releaseDate(LocalDate.of(2001, 12, 10))
            .duration(178)
            .build();
    filmController.create(film);
    assertEquals(1, filmController.findAll().size(), "Фильм не добавлен.");
  }

  @Test
  @DisplayName("Проверка корректности ввода названия.")
  public void shouldThrowExceptionInName() {
    Film incorrectName = generateFilm();
    incorrectName.setName("");
    assertThrows(ValidationException.class, () -> {
              filmController.create(incorrectName);
            }
    );
  }

  @Test
  @DisplayName("Проверка корректности ввода описания.")
  public void shouldThrowExceptionInDescription() {
    Film incorrectDescription = generateFilm();
    incorrectDescription.setDescription("Фильм рассказывает о мрачном будущем, " +
            "в котором человечество бессознательно оказывается в ловушке внутри Матрицы, " +
            "симулированной реальности, созданной интеллектуальными машинами, " +
            "чтобы отвлекать людей, используя их тела в качестве источника энергии.");
    assertThrows(ValidationException.class, () -> {
      filmController.create(incorrectDescription);
    });
  }


}