package student;

// StudentGradeManagmentSystem Project

import java.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class students {

	public static void main(String[] args) {
		try {
			// Establishing the database connection
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/student_db", "root",
					"root");

			// Creating the table if not exists
			createTable(connection);

			Scanner scanner = new Scanner(System.in);

			while (true) {
				System.out.println("\n1. Add Student\n2. Update Student\n3. Delete Student\n4. View Students\n5. Exit");
				System.out.print("Enter your choice: ");
				int choice = scanner.nextInt();

				switch (choice) {
				case 1:
					addStudent(connection, scanner);
					break;
				case 2:
					updateStudent(connection, scanner);
					break;
				case 3:
					deleteStudent(connection, scanner);
					break;
				case 4:
					viewStudents(connection);
					break;
				case 5:
					System.out.println("Exiting program...");
					System.exit(0);
					break;
				default:
					System.out.println("Invalid choice. Please try again.");
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void createTable(Connection connection) throws SQLException {
		String createTableQuery = "CREATE TABLE IF NOT EXISTS students (" + "roll_number INT PRIMARY KEY,"
				+ "name VARCHAR(255)," + "subject1 INT," + "subject2 INT," + "subject3 INT)";

		try (PreparedStatement statement = connection.prepareStatement(createTableQuery)) {
			statement.execute();
		}
	}

	private static void addStudent(Connection connection, Scanner scanner) throws SQLException {
		System.out.print("Enter Roll Number: ");
		int rollNumber = scanner.nextInt();
		scanner.nextLine(); // consume the newline
		System.out.print("Enter Name: ");
		String name = scanner.nextLine();
		System.out.print("Enter Subject 1 Marks: ");
		int subject1 = scanner.nextInt();
		System.out.print("Enter Subject 2 Marks: ");
		int subject2 = scanner.nextInt();
		System.out.print("Enter Subject 3 Marks: ");
		int subject3 = scanner.nextInt();

		String insertQuery = "INSERT INTO students (roll_number, name, subject1, subject2, subject3) VALUES (?, ?, ?, ?, ?)";

		try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
			statement.setInt(1, rollNumber);
			statement.setString(2, name);
			statement.setInt(3, subject1);
			statement.setInt(4, subject2);
			statement.setInt(5, subject3);

			int rowsAffected = statement.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("Student added successfully!");

				// Calculate and display grade
				calculateGrade(subject1, subject2, subject3);
			} else {
				System.out.println("Failed to add student.");
			}
		}
	}

	private static void updateStudent(Connection connection, Scanner scanner) throws SQLException {
		System.out.print("Enter Roll Number of the student to update: ");
		int rollNumberToUpdate = scanner.nextInt();
		scanner.nextLine(); // consume the newline

		// Check if the student with the given roll number exists
		if (isStudentExists(connection, rollNumberToUpdate)) {
			System.out.print("Enter updated Name: ");
			String updatedName = scanner.nextLine();
			System.out.print("Enter updated Subject 1 Marks: ");
			int updatedSubject1 = scanner.nextInt();
			System.out.print("Enter updated Subject 2 Marks: ");
			int updatedSubject2 = scanner.nextInt();
			System.out.print("Enter updated Subject 3 Marks: ");
			int updatedSubject3 = scanner.nextInt();

			String updateQuery = "UPDATE students SET name = ?, subject1 = ?, subject2 = ?, subject3 = ? WHERE roll_number = ?";
			try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
				statement.setString(1, updatedName);
				statement.setInt(2, updatedSubject1);
				statement.setInt(3, updatedSubject2);
				statement.setInt(4, updatedSubject3);
				statement.setInt(5, rollNumberToUpdate);

				int rowsAffected = statement.executeUpdate();
				if (rowsAffected > 0) {
					System.out.println("Student updated successfully!");

					// Calculate and display grade
					calculateGrade(updatedSubject1, updatedSubject2, updatedSubject3);
				} else {
					System.out.println("Failed to update student.");
				}
			}
		} else {
			System.out.println("Student with Roll Number " + rollNumberToUpdate + " does not exist.");
		}
	}

	private static void deleteStudent(Connection connection, Scanner scanner) throws SQLException {
		System.out.print("Enter Roll Number of the student to delete: ");
		int rollNumberToDelete = scanner.nextInt();

		// Check if the student with the given roll number exists
		if (isStudentExists(connection, rollNumberToDelete)) {
			String deleteQuery = "DELETE FROM students WHERE roll_number = ?";
			try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
				statement.setInt(1, rollNumberToDelete);

				int rowsAffected = statement.executeUpdate();
				if (rowsAffected > 0) {
					System.out.println("Student deleted successfully!");
				} else {
					System.out.println("Failed to delete student.");
				}
			}
		} else {
			System.out.println("Student with Roll Number " + rollNumberToDelete + " does not exist.");
		}
	}

	private static void viewStudents(Connection connection) throws SQLException {
		String selectQuery = "SELECT * FROM students";
		try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
			ResultSet resultSet = statement.executeQuery();

			System.out.println("\nStudent Records:");
			System.out.printf("%-15s%-25s%-15s%-15s%-15s%-15s\n", "Roll Number", "Name", "Subject 1", "Subject 2",
					"Subject 3", "Grade");
			while (resultSet.next()) {

				int rollNumber = resultSet.getInt("roll_number");
				String name = resultSet.getString("name");
				int subject1 = resultSet.getInt("subject1");
				int subject2 = resultSet.getInt("subject2");
				int subject3 = resultSet.getInt("subject3");

				// Calculate and display grade
				calculateGrade(subject1, subject2, subject3);

				System.out.printf("%-15d%-25s%-15d%-15d%-15d\n", rollNumber, name, subject1, subject2, subject3);
			}
		}
	}

	private static void calculateGrade(int subject1, int subject2, int subject3) {
		// Define your grade criteria here
		int totalMarks = 300;
		double percentage = ((double) (subject1 + subject2 + subject3) / totalMarks) * 100;

		System.out.println("Overall Percentage: " + percentage + "%");

		// Assign grades based on percentage
		if (percentage >= 90) {
			System.out.println("Grade: A");
		} else if (percentage >= 80) {
			System.out.println("Grade: B");
		} else if (percentage >= 70) {
			System.out.println("Grade: C");
		} else if (percentage >= 60) {
			System.out.println("Grade: D");
		} else {
			System.out.println("Grade: F");
		}

	}

	private static boolean isStudentExists(Connection connection, int rollNumber) throws SQLException {
		String checkQuery = "SELECT * FROM students WHERE roll_number = ?";
		try (PreparedStatement statement = connection.prepareStatement(checkQuery)) {
			statement.setInt(1, rollNumber);
			ResultSet resultSet = statement.executeQuery();
			return resultSet.next();
		}
	}
}
