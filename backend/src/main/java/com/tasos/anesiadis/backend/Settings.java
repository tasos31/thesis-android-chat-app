package com.tasos.anesiadis.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.lang.ClassNotFoundException;

public class Settings {

    public final static String DB_URL = "jdbc:mysql://localhost/hatalink";
    public final static String DB_USER_NAME = "root";
    public final static String DB_PASSWORD = "tasos";

    public static Connection getDbConnection() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        return DriverManager.getConnection(DB_URL, DB_USER_NAME, DB_PASSWORD);
    }

}