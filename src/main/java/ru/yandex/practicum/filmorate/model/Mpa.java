package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Mpa {
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
}
