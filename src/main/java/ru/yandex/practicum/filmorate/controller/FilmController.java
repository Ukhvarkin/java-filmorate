package ru.yandex.practicum.filmorate.controller;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
  private int id = 1;
  private Map<Integer, Film> films = new HashMap<>();
  private final LocalDate startFilmDate = LocalDate.of(1895, 12, 28);

  @GetMapping
  public Collection<Film> findAll() {
    log.info("GET запрос.");
    log.debug("Добавлен фильм: {}.", films.size());
    return new ArrayList<>(films.values());
  }

  @PostMapping
  public Film create(@Valid @RequestBody Film film) throws ValidationException {
    log.info("POST запрос.");
    filmValidator(film);
    film.setId(id);
    films.put(id, film);
    id++;
    log.debug("Добавлен фильм: {}.", film.getName());
    return film;
  }

  @PutMapping
  public Film update(@Valid @RequestBody Film film) throws ValidationException {
    log.info("PUT запрос.");
    filmValidator(film);
    if (films.containsKey(film.getId())) {
      films.put(film.getId(), film);
      log.info("Фильм обновлен.");
    } else {
      String message = "Фильма с таким id не найдено.";
      log.warn(message);
      throw new ValidationException(message);
    }
    return film;
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
    if (film.getName().isBlank()){
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
