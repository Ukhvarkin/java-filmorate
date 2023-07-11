package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {
  FilmController filmController;
  Film film;

  @BeforeEach
  void start() {
    filmController = new FilmController();
    film = generateFilm();
  }

  private Film generateFilm() {
    return Film.builder()
            .name("Фильм")
            .description("Описание")
            .releaseDate(LocalDate.of(2000, 12, 12))
            .duration(178)
            .build();
  }

  @Test
  @DisplayName("Добавление фильма и получения всего списка.")
  public void createFilmAndFindAllTest() {
    Film createFilm1 = film;
    Film createFilm2 = film;
    Film createFilm3 = film;

    filmController.create(createFilm1);
    filmController.create(createFilm2);
    filmController.create(createFilm3);

    assertEquals(3, filmController.findAll().size(), "Неверное количество фильмов.");
  }

  @Test
  @DisplayName("Проверка обновление фильма.")
  public void updateFilmTest() {
    filmController.create(film);

    Film updateFilm = film;
    updateFilm.setName("Обновленный фильм.");
    updateFilm.setDescription("Обновленное описание.");
    updateFilm.setReleaseDate(LocalDate.of(2020, 12, 12));
    updateFilm.setDuration(200);

    int id = film.getId();
    updateFilm.setId(id);

    filmController.update(updateFilm);
    assertEquals(film.getName(), updateFilm.getName(), "Название не обновлено.");
    assertEquals(film.getDescription(), updateFilm.getDescription(), "Описание не обновлено.");
    assertEquals(film.getReleaseDate(), updateFilm.getReleaseDate(), "Дата релиза не обновилась.");
    assertEquals(film.getDuration(), updateFilm.getDuration(), "Продолжительность не обновилась.");
  }

  @Test
  @DisplayName("Проверка корректности ввода названия.")
  public void shouldThrowExceptionInName() {
    Film incorrectName = film;
    incorrectName.setName("");
    assertThrows(ValidationException.class, () -> {
              filmController.create(incorrectName);
            }
    );
  }

  @Test
  @DisplayName("Проверка корректности ввода описания.")
  public void shouldThrowExceptionInDescription() {
    Film incorrectDescription = film;
    incorrectDescription.setDescription("Фильм рассказывает о мрачном будущем, " +
            "в котором человечество бессознательно оказывается в ловушке внутри Матрицы, " +
            "симулированной реальности, созданной интеллектуальными машинами, " +
            "чтобы отвлекать людей, используя их тела в качестве источника энергии.");
    assertThrows(ValidationException.class, () -> {
      filmController.create(incorrectDescription);
    });
  }

  @Test
  @DisplayName("Проверка корректности ввода даты релиза.")
  public void shouldThrowExceptionInReleaseData() {
    Film incorrectReleaseData = film;
    incorrectReleaseData.setReleaseDate(LocalDate.of(1000, 1, 1));
    assertThrows(ValidationException.class, () -> {
              filmController.create(incorrectReleaseData);
            }
    );
  }

}