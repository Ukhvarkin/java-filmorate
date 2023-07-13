package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
  private final LocalDate startFilmDate = LocalDate.of(1895, 12, 28);

  private final FilmService filmService;

  @Autowired
  public FilmController(FilmService filmService) {
    this.filmService = filmService;
  }

  @GetMapping
  public Collection<Film> findAll() {
    log.info("Получен запрос на получение списка всех фильмов.");
    return filmService.findAll();
  }

  @GetMapping("/{id}")
  public Film getFilm(@PathVariable int id) {
    log.info("Получен запрос на получение фильма по id.");
    return filmService.getFilmById(id);
  }

  @GetMapping("/popular")
  public Collection<Film> findTopFilms(@RequestParam(defaultValue = "10", required = false) int count) {
    log.info("Получен запрос на получение списка топ фильмов.");
    if (count <= 0) {
      throw new ValidationException("Введите значение больше 0");
    }
    return filmService.findTopFilms(count);
  }

  @PostMapping
  public Film create(@Valid @RequestBody Film film) throws ValidationException {
    log.info("Получен запрос на добавление фильма.");
    filmValidator(film);
    filmService.create(film);
    return film;
  }

  @PutMapping("/{id}/like/{userId}")
  public void addLike(@PathVariable int id,
                      @PathVariable int userId) throws FilmNotFoundException, UserNotFoundException {
    log.info("Получен запрос на добавление лайка к фильму.");
    filmCheckerId(id);
    userCheckerId(userId);
    filmService.addLike(userId, id);
  }

  @PutMapping
  public Film update(@Valid @RequestBody Film film) throws ValidationException {
    log.info("Получен запрос на обновление фильма.");
    filmValidator(film);
    return filmService.update(film);
  }

  @DeleteMapping("/{id}/like/{userId}")
  public void deleteLike(@PathVariable int id,
                         @PathVariable int userId) throws FilmNotFoundException, UserNotFoundException {
    log.info("Получен запрос на удаление лайка у фильма.");
    filmCheckerId(id);
    userCheckerId(userId);
    filmService.deleteLike(userId, id);
  }

  private void userCheckerId(int userId) throws ValidationException {
    if (userId <= 0) {
      throw new UserNotFoundException("Не найден пользователь с id: " + userId);
    }
  }

  private void filmCheckerId(int filmId) throws ValidationException {
    if (filmId <= 0) {
      throw new UserNotFoundException("Не найден фильм с id: " + filmId);
    }
  }

  private void filmValidator(Film film) throws ValidationException {
    if (film == null) {
      String message = "Некорректный ввод. Передан пустой фильм.";
      log.warn(message);
      throw new ValidationException(message);
    }
    if (film.getName() == null || film.getDescription() == null) {
      String message = "Некорректный ввод, есть пустые поля.";
      log.warn(message);
      throw new ValidationException(message);
    }
    if (film.getName().isBlank()) {
      String message = "Некорректный ввод, пустое поле названия.";
      log.warn(message);
      throw new ValidationException(message);
    }
    if (film.getDescription().length() > 200) {
      String message = "В описании больше 200 символов.";
      log.warn(message);
      throw new ValidationException(message);
    }
    if (film.getReleaseDate().isBefore(startFilmDate)) {
      String message = "Дата фильма должна быть после 28.12.1895.";
      log.warn(message);
      throw new ValidationException(message);
    }
    if (film.getDuration() < 0) {
      String message = "Продолжительность фильма должна быть больше 0.";
      log.warn(message);
      throw new ValidationException(message);
    }
  }
}
