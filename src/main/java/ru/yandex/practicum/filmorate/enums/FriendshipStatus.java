package ru.yandex.practicum.filmorate.enums;

import lombok.Getter;

@Getter
public enum FriendshipStatus {
    PENDING("неподтвержденная"),
    CONFIRMED("подтвержденная");

    private final String title;

    FriendshipStatus(String title) {
        this.title = title;
    }
}
