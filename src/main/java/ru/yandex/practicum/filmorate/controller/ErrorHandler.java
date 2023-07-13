package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.service.ErrorResponse;

@RestControllerAdvice
public class ErrorHandler {

  @ExceptionHandler()
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handlePostNotFoundException(FilmNotFoundException e) {
    return new ErrorResponse("Фильм не найден.", e.getMessage());
  }

  @ExceptionHandler()
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleUserNotFoundException(UserNotFoundException e) {
    return new ErrorResponse("Пользователь не найден.", e.getMessage());
  }

  @ExceptionHandler()
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleUserAlreadyExistException(ValidationException e) {
    return new ErrorResponse("Ошибка ввода  данных.", e.getMessage());
  }


  @ExceptionHandler()
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleAnyElseException(final Exception e) {
    return new ErrorResponse("Cервер не смог обработать запрос", e.getMessage());
  }
}
