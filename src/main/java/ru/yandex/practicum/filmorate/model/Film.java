package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
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
  private long duration;
  private final Set<Integer> likes = new HashSet<>();

  public void addLike(int userId) {
    likes.add(userId);
  }

  public void deleteLike(int userId) {
    likes.remove(userId);
  }

  public int getFilmLikesCount() {
    return likes.size();
  }

}
