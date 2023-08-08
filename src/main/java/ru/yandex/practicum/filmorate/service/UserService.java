package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.FriendshipDao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipDao friendshipDao;
    private final UserIdGenerator userIdGenerator;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage,
                       UserIdGenerator userIdGenerator,
                       FriendshipDao friendshipDao) {
        this.userStorage = userStorage;
        this.userIdGenerator = userIdGenerator;
        this.friendshipDao = friendshipDao;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) throws ValidationException {
        user.setId(userIdGenerator.generateId());
        userValidator(user);
        return userStorage.create(user);
    }

    public User update(User user) throws ValidationException, UserNotFoundException {
        userValidator(user);
        if (userStorage.containsUser(user.getId())) {
            return userStorage.update(user);
        } else {
            String message = "Пользователя с id = " + user.getId() + ", не найдено.";
            log.warn(message);
            throw new UserNotFoundException(message);
        }
    }

    public void addFriend(int userId, int friendId) throws UserNotFoundException {
        validateUserId(userId);
        validateUserId(friendId);
        friendshipDao.addFriend(userId, friendId);
        log.debug("Пользователь с id:{} отправил запрос пользователю с id:{}", userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) throws UserNotFoundException {
        validateUserId(userId);
        validateUserId(friendId);
        friendshipDao.deleteFriend(userId, friendId);
        log.debug("Пользователь с id:{} отправил отметил запрос пользователю с id:{}", userId, friendId);
    }

    public Collection<User> getFriendsList(int userId) throws UserNotFoundException {
        validateUserId(userId);
        List<User> allFriends = new ArrayList<>();

        Optional<User> optionalUser = userStorage.findUserById(userId);
        if (optionalUser.isPresent()) {
            List<User> friends = friendshipDao.getFriends(userId);
            allFriends.addAll(friends);
        }
        return allFriends;
    }

    public Collection<User> getCommonFriends(int userId, int friendId) throws UserNotFoundException {
        return userStorage.getCommonFriends(userId, friendId);
    }

    public User getUserById(int userId) throws UserNotFoundException {
        validateUserId(userId);
        return userStorage.findUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("Пользователь с id = %d, не найден.", userId)));
    }

    private void userValidator(User user) throws ValidationException {
        if (user == null) {
            String message = "Некорректный ввод. Передан пустой пользователь.";
            log.warn(message);
            throw new ValidationException(message);
        }
        if (user.getEmail() == null || user.getLogin() == null || user.getBirthday() == null) {
            String message = "Некорректный ввод, есть пустые поля.";
            log.warn(message);
            throw new ValidationException(message);
        }
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            String message = "Некорректный ввод электронной почты.";
            log.warn(message);
            throw new ValidationException(message);
        }
        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            String message = "Некорректный ввод. Логин не должен содержать пробелов.";
            log.warn(message);
            throw new ValidationException(message);
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.debug("Пользователю присвоено имя: {}.", user.getName());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            String message = "Дата рождения не может быть в будущем.";
            log.warn(message);
            throw new ValidationException(message);
        }
    }

    private void validateUserId(int userId) {
        if (userId <= 0) {
            throw new UserNotFoundException("Не найден пользователь с id: " + userId);
        }
    }
}
