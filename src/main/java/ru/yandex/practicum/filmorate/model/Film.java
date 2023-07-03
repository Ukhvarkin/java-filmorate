package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

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

}
