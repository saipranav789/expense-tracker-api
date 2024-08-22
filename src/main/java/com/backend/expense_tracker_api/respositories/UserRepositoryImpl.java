package com.backend.expense_tracker_api.respositories;

import com.backend.expense_tracker_api.domain.User;
import com.backend.expense_tracker_api.exceptions.EtAuthException;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository{

    private static final String SQL_CREATE ="INSERT INTO et_users(user_id, first_name, last_name, email, password) VALUES (nextval('et_users_seq'), ?, ?, ?, ?)";
    private static final String SQL_COUNT_BY_EMAIL = "SELECT COUNT(*) FROM et_users WHERE email = ?";
    private static final String SQL_FIND_BY_ID = "SELECT user_id, first_name, last_name, email, password FROM et_users WHERE user_id = ?";
    private static final String SQL_FIND_BY_EMAIL = "SELECT user_id, first_name, last_name, email, password FROM et_users WHERE email = ?";
    @Autowired
    JdbcTemplate jdbcTemplate;

//    @Override
//    public Integer create(String firstName, String lastName, String email, String password) throws EtAuthException {
//        try{
//            KeyHolder keyHolder = new GeneratedKeyHolder();
//            jdbcTemplate.update(connection ->{
//                PreparedStatement ps = connection.prepareStatement(SQL_CREATE, PreparedStatement.RETURN_GENERATED_KEYS);
//                ps.setString(1,firstName);
//                ps.setString(2,lastName);
//                ps.setString(3,email);
//                ps.setString(4,password);
//                return ps;
//            },keyHolder);
//            //return (Integer) keyHolder.getKey().get("USER_ID");
//            return (Integer) keyHolder.getKey().intValue();
//        }catch (Exception e){
//            throw new EtAuthException("Invalid details. Failed to create account");
//        }
//    }

//gpt

    @Override
    public Integer create(String firstName, String lastName, String email, String password) throws EtAuthException {
        String hashedPassword = BCrypt.hashpw(password,BCrypt.gensalt(10));

        try {
            System.out.println("Creating user with details:");
            System.out.println("First Name: " + firstName);
            System.out.println("Last Name: " + lastName);
            System.out.println("Email: " + email);

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                System.out.println("Preparing SQL statement: " + SQL_CREATE);
                PreparedStatement ps = connection.prepareStatement(SQL_CREATE, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, firstName);
                ps.setString(2, lastName);
                ps.setString(3, email);
                ps.setString(4, hashedPassword);
                System.out.println("PreparedStatement created with parameters: " + ps);
                return ps;
            }, keyHolder);

            System.out.println("Insert executed, retrieving generated key.");

            // Extract the generated user ID from the keyHolder
            Map<String, Object> keyMap = keyHolder.getKeys(); // Get all keys returned
            int userId = (int) keyMap.get("user_id"); // Extract the user_id

            System.out.println("Generated User ID: " + userId);
            return userId;
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getMessage());
            throw new EtAuthException("Invalid details. Failed to create account");
        }
    }


//@Override
//public Integer create(String firstName, String lastName, String email, String password) throws EtAuthException {
//    try {
//        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
//                .withTableName("ET_USER")
//                .usingGeneratedKeyColumns("USER_ID");
//
//        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("FIRST_NAME", firstName);
//        parameters.put("LAST_NAME", lastName);
//        parameters.put("EMAIL", email);
//        parameters.put("PASSWORD", password);
//
//        Number key = insert.executeAndReturnKey(new MapSqlParameterSource(parameters));
//        return key.intValue();
//    } catch (Exception e) {
//        throw new EtAuthException("Invalid details. Failed to create account");
//    }
//}

    @Override
    public User findByEmailAndPassword(String email, String password) throws EtAuthException {
        try {
            User user = jdbcTemplate.queryForObject(SQL_FIND_BY_EMAIL, new Object[]{email}, userRowMapper);
//            if(!password.equals(user.getPassword()))
            if(!BCrypt.checkpw(password,user.getPassword()))
                throw new EtAuthException("Invalid Email/Password");
            return user;
        }catch (EmptyResultDataAccessException e){
            throw new EtAuthException("Invalid email/password");
        }
    }

    @Override
    public Integer getCountByEmail(String email) {
        return jdbcTemplate.queryForObject(SQL_COUNT_BY_EMAIL, new Object[]{email}, Integer.class);
    }

    @Override
    public User findById(Integer userId) {
        return jdbcTemplate.queryForObject(SQL_FIND_BY_ID, new Object[]{userId},userRowMapper);
    }

    private final RowMapper<User> userRowMapper = ((rs, rowNum)->{
        return new User(rs.getInt("USER_ID"), rs.getString("FIRST_NAME"), rs.getString("LAST_NAME"), rs.getString("EMAIL"), rs.getString("PASSWORD"));

    });

    //gpt
//@Autowired
//NamedParameterJdbcTemplate namedParameterJdbcTemplate;
//
//    @Override
//    public Integer getCountByEmail(String email) {
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//        parameters.addValue("email", email);
//        return namedParameterJdbcTemplate.queryForObject(SQL_COUNT_BY_EMAIL, parameters, Integer.class);
//    }
//
//    @Override
//    public User findById(Integer userId) {
//        MapSqlParameterSource parameters = new MapSqlParameterSource();
//        parameters.addValue("userId", userId);
//        return namedParameterJdbcTemplate.queryForObject(SQL_FIND_BY_ID, parameters, userRowMapper);
//    }
//
//    private final RowMapper<User> userRowMapper = (rs, rowNum) -> new User(
//            rs.getInt("USER_ID"),
//            rs.getString("FIRST_NAME"),
//            rs.getString("LAST_NAME"),
//            rs.getString("EMAIL"),
//            rs.getString("PASSWORD")
//    );
}

