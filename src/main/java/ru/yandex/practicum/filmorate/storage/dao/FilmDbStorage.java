package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, this::makeFilm);
    }

    @Override
    public Film create(Film film) throws ValidationException {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());

        if (film.getGenres() != null) {
            insertFilmGenres(film);
        }
        film.setGenres(findGenresByFilmId(film.getId()));
        return film;
    }

    private void insertFilmGenres(Film film) {
        String sql = "MERGE INTO  film_genres (film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(sql, film.getId(), genre.getId());
        }
    }

    @Override
    public Film update(Film film) throws ValidationException, FilmNotFoundException {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "WHERE film_id = ?";

        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        if (film.getGenres() != null) updateFilmGenres(film);
        film.setGenres(findGenresByFilmId(film.getId()));
        return film;
    }

    private void updateFilmGenres(Film film) {
        cleanOldFilmGenresRecords(film);
        insertFilmGenres(film);
    }

    public void cleanOldFilmGenresRecords(Film film) {
        String sqlQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    @Override
    public Optional<Film> findFilmById(int filmId) throws FilmNotFoundException {
        String sql = "SELECT * FROM films WHERE film_id = ?";
        Film film = jdbcTemplate.query(sql, rs -> rs.next() ? makeFilm(rs, 0) : null, filmId);
        if (film == null) {
            throw new FilmNotFoundException(String.format("Пользователь c id = %d, не найден", filmId));
        }
        return Optional.of(film);
    }

    @Override
    public boolean containsFilm(int filmId) {
        String sql = "SELECT COUNT(*) FROM films WHERE film_id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, filmId);
        return count > 0;
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");

        Mpa mpa = findMpaById(rs.getInt("mpa_id"));
        List<Genre> genres = findGenresByFilmId(id);

        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .mpa(mpa)
                .genres(genres)
                .build();
    }

    private Mpa findMpaById(int mpaId) {
        String sql = "SELECT * FROM mpa WHERE mpa_id = ?";
        return jdbcTemplate.queryForObject(sql, this::makeMpa, mpaId);
    }

    private List<Genre> findGenresByFilmId(int filmId) {
        String sql = "SELECT DISTINCT g.genre_id, g.name FROM film_genres AS fg" +
                " JOIN genres AS g ON fg.genre_id = g.genre_id WHERE fg.film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), filmId);
    }

    private Mpa makeMpa(ResultSet rs, int rowNum) throws SQLException {
        int id = rs.getInt("mpa_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        return Mpa.builder()
                .id(id)
                .name(name)
                .description(description)
                .build();
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        int id = rs.getInt("genre_id");
        String name = rs.getString("name");

        return Genre.builder()
                .id(id)
                .name(name)
                .build();
    }

    public Collection<Film> findTopFilms(int count) throws FilmNotFoundException {
        String sql = "SELECT f.*, COUNT(l.film_id) as likes_count " +
                "FROM films f " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY likes_count DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, this::makeFilm, count);
    }
}
