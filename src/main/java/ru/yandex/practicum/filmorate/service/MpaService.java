package ru.yandex.practicum.filmorate.service;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IllegalIdException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.MpaDao;

import java.util.List;

@Service
public class MpaService {

    private final MpaDao mpaDao;

    public MpaService(MpaDao mpaRatingDao) {
        this.mpaDao = mpaRatingDao;
    }

    public List<Mpa> findAll() {
        return mpaDao.findAll();
    }

    public Mpa findMpaById(int id) {
        try {
            return mpaDao.findMpaById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalIdException("Не найдено mpa c id =" + id);
        }

    }
}
