package com.backend.expense_tracker_api.respositories;

import com.backend.expense_tracker_api.domain.Category;
import com.backend.expense_tracker_api.exceptions.EtBadRequestException;
import com.backend.expense_tracker_api.exceptions.EtResourceNotFoundException;

import java.util.List;

public interface CategoryRepository {

    List<Category> findAll(Integer userId) throws EtResourceNotFoundException;

    Category findById(Integer userId , Integer categoryId) throws EtResourceNotFoundException;

    Integer create(Integer userId, String title, String description) throws EtBadRequestException;

    void update(Integer userId, Integer categoryId, Category category) throws EtBadRequestException;

    void removeById(Integer userId, Integer categoryId);


}
