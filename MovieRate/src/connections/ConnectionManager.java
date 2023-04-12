package connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
	
	private static final String URL = "jdbc:mysql://localhost:3306/movie_db";	
	private static final String USERNAME = "root";
	private static final String PASSWORD = "root";

	public static Connection getConnection() {
		
		Connection conn = null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);			
		} catch(SQLException e) {
			System.out.println("Could not make connection.");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return conn;
	}	
	
}