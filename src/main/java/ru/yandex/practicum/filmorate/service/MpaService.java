package ru.yandex.practicum.filmorate.service;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IllegalIdException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;

import java.util.List;

@Service
public class MpaService {

    private final MpaDao mpaRatingDao;

    public MpaService(MpaDao mpaRatingDao) {
        this.mpaRatingDao = mpaRatingDao;
    }

    public List<Mpa> findAll() {
        return mpaRatingDao.findAll();
    }

    public Mpa findMpaServiceById(int id) {
        try {
            return mpaRatingDao.findMpaServiceById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalIdException("Не найдено mpa c id =" + id);
        }

    }
}
