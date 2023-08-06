package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.LikesDao;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final LocalDate startFilmDate = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmIdGenerator filmIdGenerator;
    private final LikesDao likesDao;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       @Qualifier("UserDbStorage") UserStorage userStorage,
                       FilmIdGenerator filmIdGenerator,
                       LikesDao likesDao) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmIdGenerator = filmIdGenerator;
        this.likesDao = likesDao;
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) throws ValidationException {
        filmValidator(film);
        film.setId(filmIdGenerator.generateId());
        return filmStorage.create(film);
    }

    public Film update(Film film) throws ValidationException, FilmNotFoundException {
        filmValidator(film);
        if (filmStorage.containsFilm(film.getId())) {
            return filmStorage.update(film);
        } else {
            String message = "Фильма с id =" + film.getId() + ", не найдено.";
            log.warn(message);
            throw new FilmNotFoundException(message);
        }
    }

    public void addLike(int userId, int filmId) throws FilmNotFoundException, UserNotFoundException {
        filmCheckerId(filmId);
        userCheckerId(userId);
        likesDao.addLike(userId, filmId);
        log.debug("Добавлен лайк к фильму с id: {}, пользователем с id: {}.", filmId, userId);
    }

    public void deleteLike(int userId, int filmId) throws FilmNotFoundException, UserNotFoundException {
        filmCheckerId(filmId);
        userCheckerId(userId);
        log.debug("Удален лайк у фильма с id: {}, пользователем с id: {}.", filmId, userId);
        likesDao.deleteLike(userId, filmId);
    }

    public Collection<Film> findTopFilms(int count) throws FilmNotFoundException {
        return filmStorage.findTopFilms(count);
    }

    public Film findFilmById(int id) throws FilmNotFoundException {
        return filmStorage.findFilmById(id).orElse(null);
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

    private void userCheckerId(int userId) throws UserNotFoundException {
        if (!userStorage.containsUser(userId)) {
            throw new UserNotFoundException("Пользователя с id: " + userId + ", не существует.");
        }
    }

    private void filmCheckerId(int filmId) throws FilmNotFoundException {
        if (!filmStorage.containsFilm(filmId)) {
            throw new FilmNotFoundException("Фильма с id: " + filmId + ", не существует.");
        }
    }
}
