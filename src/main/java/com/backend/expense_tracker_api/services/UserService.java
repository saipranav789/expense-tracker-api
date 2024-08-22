package com.backend.expense_tracker_api.services;

import com.backend.expense_tracker_api.domain.User;
import com.backend.expense_tracker_api.exceptions.EtAuthException;

public interface UserService {

    User validateUser(String email, String password) throws EtAuthException;

    User registerUser(String firstName, String lastName, String email, String password ) throws EtAuthException;

}
