package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class FilmService {
  private final FilmStorage filmStorage;

  @Autowired
  public FilmService(FilmStorage filmStorage) {
    this.filmStorage = filmStorage;
  }

  public Collection<Film> findAll() {
    return filmStorage.findAll();
  }

  public Film create(Film film) throws ValidationException {
    return filmStorage.create(film);
  }

  public Film update(Film film) throws ValidationException, FilmNotFoundException {
    return filmStorage.update(film);
  }

  public void addLike(int userId, int filmId) throws FilmNotFoundException {
    filmStorage.getFilm(filmId).addLike(userId);
  }

  public void deleteLike(int userId, int filmId) throws FilmNotFoundException {
    filmStorage.getFilm(filmId).deleteLike(userId);
  }

  public Collection<Film> findTopFilms(int count) throws FilmNotFoundException {
    return filmStorage.findAll().stream()
            .sorted((p0, p1) -> p1.getFilmLikesCount() - p0.getFilmLikesCount())
            .limit(count)
            .collect(Collectors.toList());
  }

  public Film getFilmById(int id) {
    return filmStorage.getFilm(id);
  }
}
