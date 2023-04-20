package com.functions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import connections.ConnectionManager;

public class GradeBookFunctions {

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
	

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static int startMenu() { // menu that starts the program

		int selectedOption = -1;

		while (selectedOption != 3) {

			System.out.println(ANSI_GREEN + "____________________________________________");
			System.out.println("|        THE SCHOOOOL GREETS YOU!           |");
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

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static boolean loginDB() {

		System.out.println(ANSI_YELLOW + "__________________________________________");
		System.out.println("|           Login Details                 |");
		System.out.println("__________________________________________\n" + ANSI_RESET);

		System.out.println("Username: ");
		String usernameInput = in.nextLine();
		System.out.println("Password: ");
		String password = in.nextLine();

		try (Connection conn = ConnectionManager.getConnection()) {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT teacher_id, username, pass FROM teacher");

			while (rs.next()) {

				String id = rs.getString("teacher_id");
				String usernameDB = rs.getString("username");
				String passDB = rs.getString("pass");

				if (usernameDB.equals(usernameInput) && passDB.equals(password)) {
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

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	static void loggedIn() {

		// System.out.println("ID: " + id);
		Integer selectedOption = -1;

		while (selectedOption != 3) {

			try {

				System.out.println(ANSI_BLUE + "\n__________________________________________");
				System.out.println("|            Hello Teacher                 |");
				System.out.println("__________________________________________\n" + ANSI_RESET);

				System.out.println("1. View Classes\n2. Add Classes\n3. Logout");

				selectedOption = in.nextInt();

				switch (selectedOption) {
				case 1:
					viewClasses();
					break;
				case 2:
					addClasses();
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

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static void viewClasses() {

		try (Connection conn = ConnectionManager.getConnection()) {
			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM class where teacher_id=?");
			pstmt.setInt(1, univId);
			ResultSet rs = pstmt.executeQuery();
			
			boolean empty = false;
			System.out.println(ANSI_BLUE + "\n__________________________________________");
			System.out.println("|            View Classes                  |");
			System.out.println("__________________________________________\n" + ANSI_RESET);

			int classCount = 1;
			while (rs.next()) {

				String className = rs.getString("name");
				System.out.println("\n" + classCount++ + ": " + className);
				empty = true;
			}
			if(empty) {selectClass();} // moves to selecting the class and next form of functionality
			//add else redirect since empty
			
		} catch (SQLException e) {
			System.out.println("Could not make connection.");
		}

	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static void addClasses() {

		System.out.println("Name of the class you want to add?: ");
		String className = in.nextLine();
		in.nextLine();

		System.out.println(className);

		try (Connection conn = ConnectionManager.getConnection()) {
			PreparedStatement pstmt = conn.prepareStatement("insert into class values(null, ?, ?)");

			pstmt.setString(1, className);
			pstmt.setInt(2, univId);

			pstmt.execute();

		} catch (SQLException e) {
			System.out.println("Could not make connection.");
		}

	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static void selectClass() {

		try {

			System.out.println(ANSI_YELLOW + "\n__________________________________________");
			System.out.println("|            Student List                  |");
			System.out.println("__________________________________________\n" + ANSI_RESET);

			boolean empty = false;
			System.out.println("Which class to choose?: ");
			int classNumber = in.nextInt();

			try (Connection conn = ConnectionManager.getConnection()) {
				PreparedStatement pstmt = conn.prepareStatement(
						"select student_id, stu_name, grade from student join student_class using (student_id) join class using (class_id) where class_id = ?");
				pstmt.setInt(1, classNumber);
				ResultSet rs = pstmt.executeQuery();

				while (rs.next()) {

					int studentId = rs.getInt("student_id");
					String name = rs.getString("stu_name");
					int grade = rs.getInt("grade");

					System.out.println("Student Id: " + studentId + " Name: " + name + " Grade: " + grade);
					empty = true;
				}

				if (empty) {classMenu(classNumber);}

			} catch (SQLException e) {
				System.out.println("Could not make connection.");
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("\nPlease enter a proper option.");
			in.nextLine();
			// loggedIn();
		}

	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static void classMenu(int classId) {

		Integer selectedOption = -1;

		while (selectedOption != 7) {

			try {

				System.out.println(ANSI_BLUE + "\n__________________________________________");
				System.out.println("|             Class Menu                  |");
				System.out.println("__________________________________________\n" + ANSI_RESET);

				System.out.println(
						"1. Average and Median\n2. Order by Name\n3. Order By Grade\n4. Add Student\n5. Update Student\n6. Remove Student\n7. Return to previous menu");

				selectedOption = in.nextInt();

				switch (selectedOption) {
				case 1:
					averageMedian(classId);
					break;
				case 2:
					orderedByName(classId);
					break;
				case 3:
					orderedByGrade(classId);
					break;
				case 4:
					addStudent(classId, 1);
					break;
				case 5:
					addStudent(classId, 2);
					break;
				case 6:
					addStudent(classId, 3);
					break;
				case 7:
					System.out.println("Back to class list");
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
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void averageMedian(int classId) {
		
		try (Connection conn = ConnectionManager.getConnection()) {
			
			List<Integer> median = new ArrayList<Integer>();
			
			PreparedStatement avgStmt = conn.prepareStatement(
					"SELECT \r\n"
					+ "     avg(grade)\r\n"
					+ "FROM\r\n"
					+ "    student\r\n"
					+ "        JOIN\r\n"
					+ "    student_class USING (student_id)\r\n"
					+ "        JOIN\r\n"
					+ "    class USING (class_id)\r\n"
					+ "WHERE\r\n"
					+ "    class_id = ?;");
			
			PreparedStatement medStmt = conn.prepareStatement(
					"SELECT \r\n"
					+ "     grade\r\n"
					+ "FROM\r\n"
					+ "    student\r\n"
					+ "        JOIN\r\n"
					+ "    student_class USING (student_id)\r\n"
					+ "        JOIN\r\n"
					+ "    class USING (class_id)\r\n"
					+ "WHERE\r\n"
					+ "    class_id = ?;"
					
					);
			
			avgStmt.setInt(1, classId);
			medStmt.setInt(1, classId);
			
			ResultSet rs = avgStmt.executeQuery();
			ResultSet rsMed = medStmt.executeQuery();
			
			while(rsMed.next()) {
			
			median.add(rsMed.getInt("grade"));
		
			}
			rs.next();
			int average = rs.getInt("avg(grade)");
			System.out.println("Average: " + average);
			System.out.println("Median: " + median(median));
			
			

		} catch (SQLException e) {
			System.out.println("Could not make connection.");
			e.printStackTrace();
		}
		
		
	}
	
	public static int median(List<Integer> median) {
		
		if (median.size() == 1) { return median.get(0); }
		else {return (median.get(median.size()/2) + median.get(median.size()/2 - 1))/2;}
	
	}
	

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static void orderedByName(int classId) {

		try (Connection conn = ConnectionManager.getConnection()) {
			PreparedStatement pstmt = conn.prepareStatement("	SELECT \r\n" + "    student_id, stu_name, grade\r\n"
					+ "FROM\r\n" + "    student\r\n" + "        JOIN\r\n" + "    student_class USING (student_id)\r\n"
					+ "        JOIN\r\n" + "    class USING (class_id)\r\n" + "WHERE\r\n" + "    class_id = ?\r\n"
					+ "order by\r\n" + "	stu_name"); // only small change here can optimize space

			pstmt.setInt(1, classId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {

				String name = rs.getString("stu_name");
				int grade = rs.getInt("grade");

				System.out.println(name + " " + grade);

			}

		} catch (SQLException e) {
			System.out.println("Could not make connection.");
			e.printStackTrace();
		}

	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static void orderedByGrade(int classId) {

		try (Connection conn = ConnectionManager.getConnection()) {
			PreparedStatement pstmt = conn.prepareStatement("	SELECT \r\n" + "    student_id, stu_name, grade\r\n"
					+ "FROM\r\n" + "    student\r\n" + "        JOIN\r\n" + "    student_class USING (student_id)\r\n"
					+ "        JOIN\r\n" + "    class USING (class_id)\r\n" + "WHERE\r\n" + "    class_id = ?\r\n"
					+ "order by\r\n" + "	grade desc");

			pstmt.setInt(1, classId);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {

				String name = rs.getString("stu_name");
				int grade = rs.getInt("grade");

				System.out.println(name + " " + grade);

			}

		} catch (SQLException e) {
			System.out.println("Could not make connection.");
			e.printStackTrace();
		}

	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static void addStudent(int classId, int choice) {

		System.out.println(ANSI_YELLOW + "\n__________________________________________");
		System.out.println("|          Student Manipulation            |");
		System.out.println("__________________________________________\n" + ANSI_RESET);

		try (Connection conn = ConnectionManager.getConnection()) {

			if (choice == 1) { // add

				in.nextLine();
				System.out.println("\nEnter Name: ");
				String name = in.nextLine();
				System.out.println("\nEnter grade: ");
				int grade = in.nextInt();

				PreparedStatement pstmt = conn.prepareStatement("insert into student values(null, ?, ?)");
				pstmt.setString(1, name); // name
				pstmt.setInt(2, grade); // grade
				pstmt.execute();

				PreparedStatement idGet = conn.prepareStatement("select student_id from student where stu_name = ?");
				idGet.setString(1, name);

				ResultSet rs = idGet.executeQuery();

				rs.next();
				int tempId = rs.getInt("student_id");

				PreparedStatement manyTwoMany = conn.prepareStatement("insert into student_class values(null, ?, ?)");
				manyTwoMany.setInt(1, tempId); // student id
				manyTwoMany.setInt(2, classId); // class id

				manyTwoMany.execute();

			} else if (choice == 2) { // update

				System.out.println("\nEnter student id to update: ");
				int id = in.nextInt();
				System.out.println("\nEnter new grade: ");
				int newGrade = in.nextInt();

				PreparedStatement update = conn.prepareStatement("update student set grade = ? where student_id = ?");
				update.setInt(1, newGrade);
				update.setInt(2, id);

				update.execute();

			}

			else if (choice == 3) { // remove

				System.out.println("\nId of Student to Delete");
				int idToDelete = in.nextInt();

				PreparedStatement delete = conn.prepareStatement("delete from student where student_id = ?");
				PreparedStatement deleteMany = conn.prepareStatement("delete from student_class where student_id = ?");

				delete.setInt(1, idToDelete);
				deleteMany.setInt(1, idToDelete);

				deleteMany.execute();
				delete.execute();

//					delete from student_class where student_id = 5;
//					delete from student where student_id = 5;
			}

		} catch (SQLException e) {
			System.out.println("Could not make connection.");
			e.printStackTrace();
		}

		// insert into student values(null, 'Travis Saulat', 90);
		// delete from student where student_id = 5;

	}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static void signup() {
		
		System.out.println(ANSI_GREEN + "__________________________________________");
		System.out.println("|           Signup                        |");
		System.out.println("__________________________________________\n" + ANSI_RESET);

		try (Connection conn = ConnectionManager.getConnection()) {
		
		System.out.println("Name: ");
		String name = in.nextLine();

		System.out.println("Username: ");
		String username = in.nextLine();

		System.out.println("Password: ");
		String password = in.nextLine();


		
			PreparedStatement signup = conn.prepareStatement("insert into teacher values(null, ?, ?, ?)");

			signup.setString(1, name);
			signup.setString(2, username);
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
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}