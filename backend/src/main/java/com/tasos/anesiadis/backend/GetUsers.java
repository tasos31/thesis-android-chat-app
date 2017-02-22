package com.tasos.anesiadis.backend;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GetUsers extends HttpServlet {

    public GetUsers() {
        super();
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        Connection connection = null;
        Statement statment = null; 
        ResultSet rs;

        String UserId = request.getParameter("UserID");

        try {
            // Connect to database retrieve user credentials
            connection = Settings.getDbConnection();
            statment = connection.createStatement();


            rs = statment.executeQuery("SELECT user_id,user_name,first_name,last_name FROM users WHERE NOT user_id=" + UserId + " ORDER BY first_name ASC");

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.println("{\"Users\":[");

            rs.last();
            int count = rs.getRow();
            rs.beforeFirst();
            int i = 0;
            while (rs.next()) {

                i++;
                int userId = rs.getInt(1);
                String userName = rs.getString(2);
                String firstName = rs.getString(3);
                String lastName = rs.getString(4);

                if (count != i) {

                    out.println(
                            "{" +
                                    "\"UserID\": \"" + userId + "\"," +
                                    "\"UserName\": \"" + userName + "\"," +
                                    "\"FirstName\": \"" + firstName + "\"," +
                                    "\"LastName\": \"" + lastName + "\"" +
                                    "},");

                } else {

                    out.println(
                            "{" +
                                    "\"UserID\": \"" + userId + "\"," +
                                    "\"UserName\": \"" + userName + "\"," +
                                    "\"FirstName\": \"" + firstName + "\"," +
                                    "\"LastName\": \"" + lastName + "\"" +
                                    "}");

                }
            }
            out.println("]}");

        } catch (Exception e) {
            response.setContentType("text/html");
            ObjectOutputStream oos = new ObjectOutputStream(response
                    .getOutputStream());
            oos.writeObject(e.getMessage());
            oos.flush();
            oos.close();
        } finally {
            try {
                if (statment != null) {
                    statment.close();
                    statment = null;
                }
                if (connection != null) {
                    connection.close();
                    connection = null;
                }
            } catch (SQLException e) {
            }
        }

    }

}