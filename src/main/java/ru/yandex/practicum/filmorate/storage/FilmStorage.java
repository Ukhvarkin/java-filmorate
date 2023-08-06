package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> findAll();

    Film create(Film film) throws ValidationException;

    Film update(Film film) throws ValidationException, FilmNotFoundException;

    Optional<Film> findFilmById(int filmId) throws FilmNotFoundException;

    boolean containsFilm(int filmId);

    Collection<Film> findTopFilms(int count);
}
