package com.todo.dao;

import java.sql.Connection;
import com.todo.model.Todo;
import com.todo.util.DatabaseConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;

public class TodoAppDAO {

    private static final String SELECT_ALL_TODOS = "SELECT * FROM todos ORDER BY created_at DESC";
    private static final String INSERT_TODO = "INSERT INTO todos(title,description,completed,created_at,updated_at) VALUES(?,?,?,?,?)";
    

    
    // Helper method to map a ResultSet row to a Todo object
    private Todo getTodoRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        boolean completed = rs.getBoolean("completed");
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
        LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();

        return new Todo(id, title, description, completed, createdAt, updatedAt);
    }

    // Fetch all todos from the database
    public List<Todo> getAllTodos() throws SQLException {
        List<Todo> todos = new ArrayList<>();

        String sql = "SELECT * FROM todos ORDER BY created_at DESC";

        try (Connection cn = DatabaseConnection.getDBConnection();
             PreparedStatement ps = cn.prepareStatement(SELECT_ALL_TODOS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                todos.add(getTodoRow(rs));

            }
        }

        return todos;
    }
}
