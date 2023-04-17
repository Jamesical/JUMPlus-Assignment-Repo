package com.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import com.util.Regex;
import connections.ConnectionManager;

public class MovieFunctions {
	
	public static final Scanner in = new Scanner(System.in);

	public static int univId = -1;
	public static int count = 0; // to be replaced with randomised ID

// Declaring ANSI_RESET so that we can reset the color
	public static final String ANSI_RESET = "\u001B[0m";

// Declaring the color
// Custom declaration
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_BLUE = "\u001B[34m";

////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public static int startMenu() { // menu that starts the program
		
		int selectedOption = -1;

		while (selectedOption != 3) {

			System.out.println(ANSI_GREEN + "____________________________________________");
			System.out.println("|        THE MOVIEMARK GREETS YOU           |");
			System.out.println("____________________________________________\n" + ANSI_RESET);

			System.out.println("1. Signup\n2. Login\n3. Exit");
			try {

				selectedOption = in.nextInt();
				in.nextLine();

				switch (selectedOption) {
				case 1:
					signup(); // creates a new user
					break;
				case 2:
					if (!loginDB()) {
						startMenu();
					} // logs in an already existing user
					break;
				case 3:
					System.out.println("\nGoodbye :)"); // completely exits the program
					System.exit(0);
				default:
					System.out.println("Choose a proper option please");
				}
			} catch (Exception e) {
				System.out.println("Invalid Option");
				in.next();
				startMenu();
			}

		} // while loop
		return -1;
	}
	
////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void signup() throws Exception {

		System.out.println(ANSI_GREEN + "__________________________________________");
		System.out.println("|           Signup                        |");
		System.out.println("__________________________________________\n" + ANSI_RESET);

		try (Connection conn = ConnectionManager.getConnection()) {
		
		System.out.println("Name: ");
		String name = in.nextLine();

		System.out.println("Email: ");
		String email = in.nextLine();
		
		//Put regex HERE
		if(!Regex.isEmailValid(email)) {
			
			throw new Exception();
		
		}

		System.out.println("Password: ");
		String password = in.nextLine();


		
			PreparedStatement signup = conn.prepareStatement("insert into customer values(null, ?, ?, ?)");

			signup.setString(1, name);
			signup.setString(2, email);
			signup.setString(3, password);

			signup.execute();
		
			System.out.println("\nAccount Made");

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Could not make connection.");
		} catch (Exception e) {

			System.out.println("\nERROR");
		}

	}
	
////////////////////////////////////////////////////////////////////////////////////////////
	
	public static boolean loginDB() {

		System.out.println(ANSI_YELLOW + "__________________________________________");
		System.out.println("|           Login Details                 |");
		System.out.println("__________________________________________\n" + ANSI_RESET);

		System.out.println("Email: ");
		String email = in.nextLine();
		System.out.println("Password: ");
		String password = in.nextLine();
		
		

		try (Connection conn = ConnectionManager.getConnection()) {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT cust_id, email, password FROM customer");

			while (rs.next()) {
				String id = rs.getString("cust_id");
				String userEmail = rs.getString("email");
				String pass = rs.getString("password");

				if (userEmail.equals(email) && pass.equals(password)) {
					univId = Integer.parseInt(id);
					System.out.println("Successfully logged in!");
					loggedIn();
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Could not make connection.");
		}

		System.out.println("\nInvalid Credentials");
		return false;

	}
	
////////////////////////////////////////////////////////////////////////////////////////////
	
	static void loggedIn() {

		// System.out.println("ID: " + id);
		Integer selectedOption = -1;

		while (selectedOption != 3) {

			try {

				System.out.println(ANSI_BLUE + "\n__________________________________________");
				System.out.println("|            Hello CUSTOMER                |");
				System.out.println("__________________________________________\n" + ANSI_RESET);

				System.out.println(
						"1. View Movies\n2. Rate Movies\n3. Logout");

				selectedOption = in.nextInt();

				switch (selectedOption) {
				case 1:
					viewMovie();
					break;
				case 2:
					rateMovie();
					break;
				case 3:
					System.out.println("Logged Out");
					break;
				default:
					System.out.println("Try again chief");
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("\nPlease enter a proper option.");
				in.nextLine();
				// loggedIn();
			}

		} // while loop
	}
	
////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void viewMovie() {
		
		try (Connection conn = ConnectionManager.getConnection()) {
			PreparedStatement pstmt = conn
					.prepareStatement("SELECT * FROM movie");
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("movie_id");
				String name= rs.getString("name");
				double rating = rs.getDouble("rating");
				int timesRated = rs.getInt("time_rated");
				
				if(timesRated == 0) {
					System.out.println("\nID: " + id + " | Name: " + name + " | Rating AVG: " + "N/A" + " | Times Rated: " + timesRated);
				}
				else {
					double avgRating = rating/timesRated;
					System.out.println("\nID: " + id + " | Name: " + name + " | Rating AVG: " + avgRating + " | Times Rated: " + timesRated);
				}
				

			}
		} catch (SQLException e) {
			System.out.println("Could not make connection.");
		}
		
	}
	
////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void rateMovie() {
		
		viewMovie();
		
		System.out.println("\nSelect which movie to rate: ");
		int choice = in.nextInt();
		in.nextLine();
		
		System.out.println("Choose a rating (1-5)");
		int rateNum = in.nextInt();
		in.nextLine();
		
		try (Connection conn = ConnectionManager.getConnection()) {
			
			PreparedStatement pstmt = conn.prepareStatement("update movie set rating = ?, time_rated = ? where movie_id = ?");
			PreparedStatement rating = conn.prepareStatement("select rating, time_rated from movie where movie_id = ?");
			rating.setInt(1, choice);
			
			ResultSet rs = rating.executeQuery();
			
			while(rs.next()) {
				double addRating = rs.getInt("rating");
				int count1 = rs.getInt("time_rated");
				//System.out.println("RATING: " + addRating);
				
				pstmt.setDouble(1, rateNum + addRating);
				pstmt.setInt(2, ++count1);
				pstmt.setInt(3, choice);
			}
				
			

			int run = pstmt.executeUpdate();

			
		} catch (SQLException e) {
			System.out.println("Could not make connection.");
		}
		
	}
	
////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////
	

}
