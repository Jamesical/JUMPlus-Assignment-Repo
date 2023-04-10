package com.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import connections.ConnectionManager;

public class BankUtil {

	public static final Scanner in = new Scanner(System.in);

	public static int univId = -1;
	public static int count = 3; // to be replaced with randomised ID

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

			System.out.println(ANSI_GREEN + "__________________________________________");
			System.out.println("|        DOLLARSBANK GREETS YOU           |");
			System.out.println("__________________________________________\n" + ANSI_RESET);

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

	public static void signup() {

		System.out.println(ANSI_GREEN + "__________________________________________");
		System.out.println("|           Signup                        |");
		System.out.println("__________________________________________\n" + ANSI_RESET);

		System.out.println("Name: ");
		String name = in.nextLine();

		System.out.println("Username: ");
		String username = in.nextLine();

		System.out.println("Password: ");
		String password = in.nextLine();

		System.out.println("Starting Funds: ");
		double funds = in.nextDouble();
		in.nextLine();

		try (Connection conn = ConnectionManager.getConnection()) {
			PreparedStatement signup = conn.prepareStatement("insert into customer values(null, ?, ?, ?, ?, ?)");

			signup.setInt(1, count++);
			signup.setString(2, name);
			signup.setString(3, username);
			signup.setString(4, password);
			signup.setDouble(5, funds);

			signup.execute();
			// insert into customer values(null, ?, 'P, "philip", 'pass2', 200.25);

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Could not make connection.");
		}

	}

////////////////////////////////////////////////////////////////////////////////////////////	

	public static boolean loginDB() {

		System.out.println(ANSI_YELLOW + "__________________________________________");
		System.out.println("|           Login Details                 |");
		System.out.println("__________________________________________\n" + ANSI_RESET);

		System.out.println("Username: ");
		String username = in.nextLine();
		System.out.println("Password: ");
		String password = in.nextLine();
		
		

		try (Connection conn = ConnectionManager.getConnection()) {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT user_id, username, password FROM customer");

			while (rs.next()) {
				String id = rs.getString("user_id");
				String user = rs.getString("username");
				String pass = rs.getString("password");

				if (user.equals(username) && pass.equals(password)) {
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

		while (selectedOption != 6) {

			try {

				System.out.println(ANSI_BLUE + "\n__________________________________________");
				System.out.println("|            Hello CUSTOMER                |");
				System.out.println("__________________________________________\n" + ANSI_RESET);

				System.out.println(
						"1. Deposit\n2. Withdraw\n3. Funds Transfer\n4. Last 5 transactions\n5. Display Info\n6. Logout");

				selectedOption = in.nextInt();

				switch (selectedOption) {
				case 1:
					deposit();
					break;
				case 2:
					withdraw();
					break;
				case 3:
					transfer();
					break;
				case 4:
					viewTrans();
					break;
				case 5:
					viewInfo();
					break;
				case 6:
					System.out.println("Logged Out");
					break;
				default:

					System.out.println("Try again chief");
				}
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("\nPlease enter a proper option.");
				in.nextLine();
				// loggedIn();
			}

		} // while loop
	}

////////////////////////////////////////////////////////////////////////////////////////////

	public static void deposit() {

		try (Connection conn = ConnectionManager.getConnection()) {
			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM customer WHERE user_id = ?");
			pstmt.setInt(1, univId);
			ResultSet rs = pstmt.executeQuery();

			System.out.println("How much to deposit?");
			double deposit = in.nextDouble();

			while (rs.next()) {
				String name = rs.getString("name");
				String funds = rs.getString("funds");

				//
				double fundsDouble = Double.parseDouble(funds);
				fundsDouble += deposit;
				funds = String.valueOf(fundsDouble);
				//

				System.out.println("SHOW ID: " + name + " | " + fundsDouble);

				PreparedStatement pstmt2 = conn.prepareStatement("update customer set funds = ? where cust_id = ?");
				pstmt2.setString(1, funds);
				pstmt2.setInt(2, univId);

				int run = pstmt2.executeUpdate();

				PreparedStatement transact = conn.prepareStatement("insert into transactions values(null, ?, ?, ?, ?)");
				transact.setInt(1, univId);
				transact.setString(2, "today");
				transact.setDouble(3, fundsDouble);
				transact.setString(4, "deposit");

				transact.execute();

			}
		} catch (SQLException e) {
			System.out.println("Could not make connection.");
		}

	}

////////////////////////////////////////////////////////////////////////////////////////////

	public static void withdraw() {

		try (Connection conn = ConnectionManager.getConnection()) {
			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM customer WHERE user_id = ?");
			pstmt.setInt(1, univId);
			ResultSet rs = pstmt.executeQuery();

			System.out.println("How much to withdraw?");
			double withdraw = in.nextDouble();

			while (rs.next()) {
				String name = rs.getString("name");
				String funds = rs.getString("funds");

				//
				double fundsDouble = Double.parseDouble(funds);
				fundsDouble -= withdraw;
				funds = String.valueOf(fundsDouble);
				//

				System.out.println("SHOW ID: " + name + " | " + fundsDouble);

				PreparedStatement pstmt2 = conn.prepareStatement("update customer set funds = ? where cust_id = ?");
				pstmt2.setString(1, funds);
				pstmt2.setInt(2, univId);

				int run = pstmt2.executeUpdate();

				PreparedStatement transact = conn.prepareStatement("insert into transactions values(null, ?, ?, ?, ?)");
				transact.setInt(1, univId);
				transact.setString(2, "today");
				transact.setDouble(3, fundsDouble);
				transact.setString(4, "withdraw");

				transact.execute();

			}
		} catch (SQLException e) {
			System.out.println("Could not make connection.");
		}

	}

////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean idCheck(String idOther) {

		try (Connection conn = ConnectionManager.getConnection()) {
			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM customer");
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {

				String id = rs.getString("user_id");

				if (id.equals(idOther)) {
					return true;
				}
			}

		} catch (Exception e) {

			System.out.println("ERROR");
		}
		return false;
	}

////////////////////////////////////////////////////////////////////////////////////////////

	public static void transfer() {

		try (Connection conn = ConnectionManager.getConnection()) {
			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM customer WHERE user_id = ?");
			pstmt.setInt(1, univId);
			ResultSet rs = pstmt.executeQuery();

			System.out.println("Id of the one to get da money: ");
			int idOther = in.nextInt();
			String idOther2 = Integer.toString(idOther); // avoid warning

			System.out.println(idCheck(idOther2));
			if (!idCheck(idOther2)) {

				throw new Exception();

			}

			System.out.println("How much money to transfer: ");
			double transferAmount = in.nextDouble();

			PreparedStatement pstmtRecip = conn.prepareStatement("SELECT * FROM customer WHERE user_id = ?");
			pstmtRecip.setInt(1, idOther);
			ResultSet rsOther = pstmtRecip.executeQuery();

			while (rs.next()) {
				String name = rs.getString("name");
				String funds = rs.getString("funds");

				//
				double fundsDouble = Double.parseDouble(funds);
				fundsDouble -= transferAmount;
				funds = String.valueOf(fundsDouble);
				//

				System.out.println("SHOW ID: " + name + " | " + fundsDouble);

				PreparedStatement pstmt2 = conn.prepareStatement("update customer set funds = ? where cust_id = ?");
				pstmt2.setString(1, funds);
				pstmt2.setInt(2, univId);

				int run = pstmt2.executeUpdate();

				PreparedStatement transact = conn.prepareStatement("insert into transactions values(null, ?, ?, ?, ?)");
				transact.setInt(1, univId);
				transact.setString(2, "today");
				transact.setDouble(3, fundsDouble);
				transact.setString(4, "transfer");

				transact.execute();

			}

			while (rsOther.next()) { // THE ONE WHO GETS THE MONEY (OTHER)
				String name = rsOther.getString("name");
				String funds = rsOther.getString("funds");

				//
				double fundsDouble = Double.parseDouble(funds);
				fundsDouble += transferAmount;
				funds = String.valueOf(fundsDouble);
				//

				System.out.println("SHOW ID: " + name + " | " + fundsDouble);

				PreparedStatement pstmt2 = conn.prepareStatement("update customer set funds = ? where cust_id = ?");
				pstmt2.setString(1, funds);
				pstmt2.setInt(2, idOther);

				int run = pstmt2.executeUpdate();

				PreparedStatement transact2 = conn
						.prepareStatement("insert into transactions values(null, ?, ?, ?, ?)");
				transact2.setInt(1, idOther);
				transact2.setString(2, "today");
				transact2.setDouble(3, fundsDouble);
				transact2.setString(4, "transfer");

				transact2.execute();

			}
		} catch (SQLException e) {
			System.out.println("Could not make connection.");
		} catch (Exception e) {
			System.out.println("ERROR");
		}

	}

////////////////////////////////////////////////////////////////////////////////////////////

	public static void viewTrans() {

		try (Connection conn = ConnectionManager.getConnection()) {
			PreparedStatement pstmt = conn
					.prepareStatement("SELECT * FROM transactions WHERE user_id = ? order by trans_id desc limit 5");
			pstmt.setInt(1, univId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				String time = rs.getString("time_of");
				String funds = rs.getString("funds_trans");
				String type = rs.getString("type");

				System.out.println("\nFunds: " + funds + " | Time: " + time + " | Type: " + type);

			}
		} catch (SQLException e) {
			System.out.println("Could not make connection.");
		}

	}

////////////////////////////////////////////////////////////////////////////////////////////

	public static void viewInfo() {

		try (Connection conn = ConnectionManager.getConnection()) {
			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM customer WHERE user_id = ?");
			pstmt.setInt(1, univId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				String name = rs.getString("name");
				String username = rs.getString("username");
				String pass = rs.getString("password");
				String funds = rs.getString("funds");

				System.out.println(
						"\nName: " + name + " | Username: " + username + " | Password: " + pass + " | Funds: " + funds);

			}
		} catch (SQLException e) {
			System.out.println("Could not make connection.");
		}

	}

////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////

}
