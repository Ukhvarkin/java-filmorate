package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    @Test
    public void testUpdateUser() throws ValidationException {
        userStorage.create(generateUser());
        userStorage.update(generateUpdateUser());
        Optional<User> userOptional = userStorage.findUserById(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u -> assertThat(u).hasFieldOrPropertyWithValue("login", "test2")
                );
    }

    @Test
    public void testFindUserById() {
        userStorage.create(generateUser());
        Optional<User> userOptional = userStorage.findUserById(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void testFindAllUser() {
        userStorage.create(generateUser());
        List<User> userList = userStorage.findAll();
        assertEquals(2, userList.size());
    }

    @Test
    public void testUpdateFilm() throws ValidationException {
        filmStorage.create(generateFilm());
        filmStorage.update(generateUpdateFilm());
        Optional<Film> userOptional = filmStorage.findFilmById(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u -> assertThat(u).hasFieldOrPropertyWithValue("name", "film2")
                );
    }

    private User generateUser() {
        return User.builder()
                .id(1)
                .login("test1")
                .name("Test Name")
                .email("test@email.ru")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();
    }

    private User generateUpdateUser() {
        return User.builder()
                .id(1)
                .login("test2")
                .name("Name Test")
                .email("email@test.ru")
                .birthday(LocalDate.of(2000, 2, 20))
                .build();
    }

    private Film generateFilm() {
        return Film.builder()
                .id(1)
                .name("film1")
                .description("description")
                .duration(120)
                .releaseDate(LocalDate.of(2000, 2, 20))
                .mpa(Mpa.builder()
                        .id(1)
                        .name("G")
                        .description("У фильма нет возрастных ограничений.").build())
                .build();
    }

    private Film generateUpdateFilm() {
        return Film.builder()
                .id(1)
                .name("film2")
                .description("description")
                .duration(120)
                .releaseDate(LocalDate.of(2000, 2, 20))
                .mpa(Mpa.builder()
                        .id(5)
                        .name("NC-17")
                        .description("Лицам до 18 лет просмотр запрещён.").build())
                .build();
    }
}