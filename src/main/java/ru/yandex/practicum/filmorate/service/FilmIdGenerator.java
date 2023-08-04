package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;

@Component
public class FilmIdGenerator {
    private int id = 1;

    public int generateId() {
        int generatedId = id;
        id++;
        return generatedId;
    }
}
