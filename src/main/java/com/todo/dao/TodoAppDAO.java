package com.todo.dao;

import com.todo.model.Todo;
import com.todo.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TodoAppDAO {

    private static final String SELECT_ALL_TODOS = "SELECT * FROM todos ORDER BY created_at DESC";
    private static final String INSERT_TODO = "INSERT INTO todos(title, description, completed, created_at, updated_at) VALUES(?,?,?,?,?)";

    // Create a new todo in the database
    public int createTodo(Todo todo) throws SQLException {
        try (
                Connection conn = DatabaseConnection.getDBConnection();
                PreparedStatement stmt = conn.prepareStatement(INSERT_TODO, Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setString(1, todo.getTitle());
            stmt.setString(2, todo.getDescription());
            stmt.setBoolean(3, todo.isCompleted());

            // If timestamps are null, set current time
            LocalDateTime now = LocalDateTime.now();
            if (todo.getCreated_at() == null) todo.setCreated_at(now);
            if (todo.getUpdated_at() == null) todo.setUpdated_at(now);

            stmt.setTimestamp(4, Timestamp.valueOf(todo.getCreated_at()));
            stmt.setTimestamp(5, Timestamp.valueOf(todo.getUpdated_at()));

            int rowAffected = stmt.executeUpdate();
            if (rowAffected == 0) {
                throw new SQLException("Creating todo failed, no rows inserted.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating todo failed, no ID obtained.");
                }
            }
        }
    }

    // Map a ResultSet row to a Todo object
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

        try (
                Connection conn = DatabaseConnection.getDBConnection();
                PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_TODOS);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                todos.add(getTodoRow(rs));
            }
        }

        return todos;
    }
}
