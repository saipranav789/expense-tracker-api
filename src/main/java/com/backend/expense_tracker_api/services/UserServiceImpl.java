package com.backend.expense_tracker_api.services;

import com.backend.expense_tracker_api.domain.User;
import com.backend.expense_tracker_api.exceptions.EtAuthException;
import com.backend.expense_tracker_api.respositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@Transactional
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Override
    public User validateUser(String email, String password) throws EtAuthException {
        if(email!=null) email = email.toLowerCase();
        return userRepository.findByEmailAndPassword(email, password);
    }

    @Override
    public User registerUser(String firstName, String lastName, String email, String password) throws EtAuthException {
        Pattern pattern = Pattern.compile("^(.*)@(.*)$");
        if(email!=null) email = email.toLowerCase();
        System.out.println("Printing email after lowercaseing: "+email);
        if(!pattern.matcher(email).matches()) throw new EtAuthException("Invalid Email Format");
        Integer count = userRepository.getCountByEmail(email);
        System.out.println("Checking if we get count by email: Count ="+ count);
        if(count > 0){
            throw new EtAuthException("Email Already In Use");
        }
        System.out.println("Proceeding to create user in db");
        Integer userId = userRepository.create(firstName,lastName,email,password);
        return  userRepository.findById(userId);
    }
}

