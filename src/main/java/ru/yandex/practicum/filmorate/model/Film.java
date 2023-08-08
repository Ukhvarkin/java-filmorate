package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Film {
    private int id;
    @NotNull(message = "Название не может быть пустым.")
    @NotBlank(message = "Название не может быть пустым.")
    private String name;
    @Size(max = 200, message = "Название может содержать не более 200 символов.")
    @NotNull(message = "Описание не может быть пустым.")
    private String description;
    @NotNull(message = "Дата релиза не может быть пустым.")
    private LocalDate releaseDate;
    @Min(1)
    @NotNull(message = "Продолжительность не может быть пустым.")
    private int duration;
    private Mpa mpa;
    private List<Genre> genres = new ArrayList<>();

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public void deleteGenre(Genre genre) {
        genres.remove(genre);
    }
}
