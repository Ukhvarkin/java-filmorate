package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;

@Component
public class UserIdGenerator {
    private int id = 1;

    public int generateId() {
        int generatedId = id;
        id++;
        return generatedId;
    }
}
