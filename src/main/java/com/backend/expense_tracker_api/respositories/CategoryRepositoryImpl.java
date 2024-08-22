package com.backend.expense_tracker_api.respositories;

import com.backend.expense_tracker_api.domain.Category;
import com.backend.expense_tracker_api.exceptions.EtBadRequestException;
import com.backend.expense_tracker_api.exceptions.EtResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {
    private static final String SQL_FIND_ALL = "SELECT c.category_id, c.user_id, c.title, c.description, " +
            "COALESCE(SUM(t.amount), 0) total_expense " +
            "FROM et_transactions t RIGHT OUTER JOIN et_categories c ON c.category_id = t.category_id " +
            "WHERE c.user_id = ? GROUP BY c.category_id";

    private static final String SQL_FIND_BY_ID = "SELECT c.category_id, c.user_id, c.title, c.description, " +
            "COALESCE(SUM(t.amount), 0) total_expense " +
            "FROM et_transactions t RIGHT OUTER JOIN et_categories c ON c.category_id = t.category_id " +
            "WHERE c.user_id = ? AND c.category_id = ? GROUP BY c.category_id";

    private static final String SQL_CREATE = "INSERT INTO et_categories (category_id, user_id, title, description) " +
            "VALUES (nextval('et_categories_seq'), ?, ?, ?)";

    private static final String SQL_UPDATE = "UPDATE et_categories SET title = ?, description = ? " +
            "WHERE user_id = ? AND category_id = ?";

    private static final String SQL_DELETE_CATEGORY = "DELETE FROM et_categories WHERE user_id = ? AND category_id = ?";

    private static final String SQL_DELETE_ALL_TRANSACTIONS = "DELETE FROM et_transactions WHERE category_id = ?";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<Category> findAll(Integer userId) throws EtResourceNotFoundException {
        return jdbcTemplate.query(SQL_FIND_ALL, new Object[]{userId}, categoryRowMapper);
    }

    @Override
    public Category findById(Integer userId, Integer categoryId) throws EtResourceNotFoundException {
        try {
            return jdbcTemplate.queryForObject(SQL_FIND_BY_ID, new Object[]{userId, categoryId}, categoryRowMapper);
        }catch (Exception e) {
            throw new EtResourceNotFoundException("Category not found");
        }
    }

    //    @Override
//    public Integer create(Integer userId, String title, String description) throws EtBadRequestException {
//        try {
//            KeyHolder keyHolder = new GeneratedKeyHolder();
//            jdbcTemplate.update(connection->{
//                PreparedStatement ps = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
//                ps.setInt(1,userId);
//                ps.setString(2,title);
//                ps.setString(3,description);
//                return ps;
//            },keyHolder);
//            return  (Integer) keyHolder.getKeys().get("category_id");
//        }catch (Exception e){
//            throw new EtBadRequestException("Invalid Request");
//        }
//    }
    @Override
    public Integer create(Integer userId, String title, String description) throws EtBadRequestException {
        try {
            System.out.println("Starting create method");
            System.out.println("User ID: " + userId);
            System.out.println("Title: " + title);
            System.out.println("Description: " + description);

            KeyHolder keyHolder = new GeneratedKeyHolder();

            int rowsAffected = jdbcTemplate.update(connection -> {
                System.out.println("Inside jdbcTemplate.update");
                PreparedStatement ps = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, userId);
                ps.setString(2, title);
                ps.setString(3, description);
                System.out.println("PreparedStatement: " + ps.toString());
                return ps;
            }, keyHolder);

            System.out.println("Rows affected: " + rowsAffected);

            if (keyHolder.getKeys() != null) {
                System.out.println("Generated keys: " + keyHolder.getKeys());
                Integer generatedId = (Integer) keyHolder.getKeys().get("category_id");
                System.out.println("Generated category_id: " + generatedId);
                return generatedId;
            } else {
                System.out.println("No keys generated.");
                throw new EtBadRequestException("Failed to create the category. No ID obtained.");
            }
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getMessage());
            e.printStackTrace(); // This will give more information on the error
            throw new EtBadRequestException("Invalid Request");
        }
    }


    @Override
    public void update(Integer userId, Integer categoryId, Category category) throws EtBadRequestException {
        try {
            jdbcTemplate.update(SQL_UPDATE, new Object[]{category.getTitle(), category.getDescription(), userId, categoryId});
        }catch (Exception e) {
            throw new EtBadRequestException("Invalid request");
        }
    }


    @Override
    public void removeById(Integer userId, Integer categoryId) {
        this.removeAllCatTransactions(categoryId);
        jdbcTemplate.update(SQL_DELETE_CATEGORY, new Object[]{userId, categoryId});
    }

    private void removeAllCatTransactions(Integer categoryId) {
        jdbcTemplate.update(SQL_DELETE_ALL_TRANSACTIONS, new Object[]{categoryId});
    }
    private RowMapper<Category> categoryRowMapper = ((rs, rowNum) -> {
        return new Category(
                rs.getInt("user_id"),
                rs.getInt("category_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getDouble("total_expense"));
    });
}
