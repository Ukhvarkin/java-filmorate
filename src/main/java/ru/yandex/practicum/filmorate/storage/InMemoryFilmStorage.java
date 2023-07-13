package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
  private int id = 1;
  private final Map<Integer, Film> films = new HashMap<>();


  @Override
  public Collection<Film> findAll() {
    log.info("Получен запрос на получение списка всех фильмов.");
    log.debug("Текущее количество фильмов: {}.", films.size());
    return films.values();
  }

  @Override
  public Film create(Film film) throws ValidationException {
    film.setId(id);
    films.put(id, film);
    id++;
    log.debug("Добавлен фильм: id:{}. {}.", film.getId(), film.getName());
    return film;
  }

  @Override
  public Film update(Film film) {
    if (films.containsKey(film.getId())) {
      films.put(film.getId(), film);
      log.info("Фильм {} обновлен.", film.getName());
    } else {
      String message = "Фильма с id =" + film.getId() + ", не найдено.";
      log.warn(message);
      throw new FilmNotFoundException(message);
    }
    return film;
  }

  @Override
  public Film getFilm(int filmId) {
    if (!films.containsKey(filmId)) {
      throw new FilmNotFoundException("Не найден фильм с id: " + filmId);
    }
    return films.get(filmId);
  }
}