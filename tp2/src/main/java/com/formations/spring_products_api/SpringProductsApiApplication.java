package com.formations.spring_products_api;

import java.lang.System.Logger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringProductsApiApplication {

	public static void main(String[] args) {
		Connection connection = null;
		Statement statement = null;
		try {
			connection = DriverManager.getConnection(
				"jdbc:postgresql://localhost:5432/",
				"admin",
				"admin123"
			);
			statement = connection.createStatement();
			statement.executeQuery(
				"SELECT count(*) FROM pg_database WHERE datname = 'products'"
			);
			ResultSet resultSet = statement.getResultSet();
			resultSet.next();
			int count = resultSet.getInt(1);

			if (count <= 0) {
				statement.executeUpdate("CREATE DATABASE products");
			} else {
			}
		} catch (SQLException e) {
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {}
		}
		SpringApplication.run(SpringProductsApiApplication.class, args);
	}
}
