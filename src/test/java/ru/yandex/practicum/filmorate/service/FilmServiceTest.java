package ru.yandex.practicum.filmorate.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmServiceTest {
  private FilmService filmService;
  private UserService userService;

  @BeforeEach
  void start() {
    FilmStorage filmStorage = new InMemoryFilmStorage();
    UserStorage userStorage = new InMemoryUserStorage();
    FilmIdGenerator filmIdGenerator = new FilmIdGenerator();
    UserIdGenerator userIdGenerator = new UserIdGenerator();

    filmService = new FilmService(filmStorage, userStorage, filmIdGenerator);
    userService = new UserService(userStorage, userIdGenerator);
  }

  private Film generateFilm() {
    return Film.builder()
            .name("Фильм")
            .description("Описание")
            .releaseDate(LocalDate.of(2000, 12, 12))
            .duration(178)
            .build();
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
  @DisplayName("Проверка лайка к фильму")
  public void shouldAddLike() throws FilmNotFoundException, UserNotFoundException {
    Film film1 = generateFilm();
    Film createdFilm1 = filmService.create(film1);

    User user1 = generateUser();
    User user2 = generateUser();

    User createdUser1 = userService.create(user1);
    User createdUser2 = userService.create(user2);

    int filmId = createdFilm1.getId();
    int userId1 = createdUser1.getId();
    int userId2 = createdUser2.getId();

    filmService.addLike(userId1, filmId);
    filmService.addLike(userId2, filmId);

    assertEquals(2, createdFilm1.getFilmLikesCount(), "Неверное количество лайков.");
  }


  @Test
  @DisplayName("Проверка удаление лайка у фильма.")
  public void shouldDeleteLike() {
    Film film1 = generateFilm();
    Film createdFilm1 = filmService.create(film1);

    User user1 = generateUser();
    User user2 = generateUser();

    User createdUser1 = userService.create(user1);
    User createdUser2 = userService.create(user2);

    int filmId = createdFilm1.getId();
    int userId1 = createdUser1.getId();
    int userId2 = createdUser2.getId();

    filmService.addLike(userId1, filmId);
    filmService.addLike(userId2, filmId);

    filmService.deleteLike(userId1, filmId);
    filmService.deleteLike(userId2, filmId);
    assertEquals(0, createdFilm1.getFilmLikesCount(), "Неверное количество лайков.");
  }

  @Test
  @DisplayName("Проверка работы логики по получению списка топа.")
  public void shouldFindTopFilms() {
    List<Film> filmsLikeList = new ArrayList<>();

    for (int i = 0; i < 15; i++) {
      int id = i + 1;
      String name = "Film " + id;

      Film film = Film.builder()
              .id(id)
              .name(name)
              .description("Описание.")
              .releaseDate(LocalDate.of(2000, 12, 12))
              .build();

      int numLikes = (int) (Math.random() * 10) + 1;
      for (int j = 0; j < numLikes; j++) {
        int userId = j + 1;
        film.addLike(userId);
      }
      filmService.create(film);
      filmsLikeList.add(film);
    }
    Film topFilmFromController = filmService.findTopFilms(1).stream().findFirst().orElse(null);
    Film topFilmFromList = filmsLikeList.stream()
            .max(Comparator.comparing(film1 -> film1.getLikes().size())).orElse(null);

    assertEquals(filmsLikeList.size(), filmService.findTopFilms(15).size(), "Неверное количество");
    assertEquals(5, filmService.findTopFilms(5).size(), "Неверное количество");
    assertEquals(topFilmFromController, topFilmFromList, "Фильмы должны быть одними и теми");
  }

  @Test
  @DisplayName("Проверка на лайка при несуществующем пользователе.")
  public void shouldThrowExceptionUserNotFoundWhenAddLikes() {
    Film createdFilm1 = generateFilm();
    Film createdFilm = filmService.create(createdFilm1);

    int incorrectUserId = 9999;
    int filmId = createdFilm1.getId();

    assertThrows(UserNotFoundException.class, () -> filmService.addLike(incorrectUserId, filmId));
  }

  @Test
  @DisplayName("Проверка на лайка при несуществующем фильме.")
  public void shouldThrowExceptionFilmNotFoundWhenAddLikes() {
    User user1 = generateUser();
    User createdUser1 = userService.create(user1);

    int incorrectFilmId = 9999;
    int userId1 = createdUser1.getId();

    assertThrows(FilmNotFoundException.class, () -> filmService.addLike(incorrectFilmId, userId1));
  }

  @Test
  @DisplayName("Проверка на получения фильма с несуществующим id.")
  public void shouldThrowExceptionFilmNotFoundWhenFindById() {
    int incorrectFilmId = 9999;

    assertThrows(FilmNotFoundException.class, () -> filmService.getFilmById(incorrectFilmId));
  }

  @Test
  @DisplayName("Проверка на лайка при несуществующем пользователе.")
  public void shouldThrowExceptionUserNotFoundWhenDeleteLikes() {
    Film createdFilm1 = generateFilm();
    Film createdFilm = filmService.create(createdFilm1);

    int incorrectUserId = 9999;
    int filmId = createdFilm1.getId();

    assertThrows(UserNotFoundException.class, () -> filmService.deleteLike(incorrectUserId, filmId));
    assertThrows(FilmNotFoundException.class, () -> filmService.deleteLike(filmId, incorrectUserId));
  }

  @Test
  @DisplayName("Проверка на лайка при несуществующем фильме.")
  public void shouldThrowExceptionFilmNotFoundWhenDeleteLikes() {
    User user1 = generateUser();
    User createdUser1 = userService.create(user1);

    int incorrectFilmId = 9999;
    int userId1 = createdUser1.getId();

    assertThrows(FilmNotFoundException.class, () -> filmService.deleteLike(userId1, incorrectFilmId));
    assertThrows(FilmNotFoundException.class, () -> filmService.deleteLike(incorrectFilmId, userId1));
  }
}