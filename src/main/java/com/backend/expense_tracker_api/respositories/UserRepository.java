package com.backend.expense_tracker_api.respositories;

import com.backend.expense_tracker_api.domain.User;
import com.backend.expense_tracker_api.exceptions.EtAuthException;

public interface UserRepository {

    Integer create(String firstName, String lastName, String email, String password) throws EtAuthException;

    User findByEmailAndPassword(String email, String password) throws EtAuthException;

    Integer getCountByEmail(String email);

    User findById(Integer userid);
}
