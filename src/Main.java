import java.sql.PreparedStatement;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static Connection conn;
    public static void getAllStudents(){
        String selectAllSQL = " SELECT * FROM students" ;
        try (PreparedStatement pstmt = conn.prepareStatement(selectAllSQL)) {
            java.sql.ResultSet result = pstmt.executeQuery();
            System.out.println("ID|FIRSTNAME|LASTNAME|EMAIL|ENROLLDATE\n");
            while(result.next()){
                int id = result.getInt("student_id");
                String first_name = result.getString("first_name");
                String last_name = result.getString("last_name");
                String email = result.getString("email");
                java.sql.Date date = result.getDate("enrollment_date");

                System.out.printf("%d|%s|%s|%s|%s\n", id,first_name,last_name,email, date);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /*
    * DOCUMENTATION NOTE
    * Using prepared statement, question marks are replaced with pstmt set functions with parameterIndex
    * */
    public static void addStudent(String first_name, String last_name, String email,String date){
        String insert = String.format(" INSERT INTO students (first_name, last_name, email, enrollment_date) VALUES(?,?,?,?)");
        try (PreparedStatement pstmt = conn.prepareStatement(insert)) {
            pstmt.setString(1,first_name);
            pstmt.setString(2,last_name);
            pstmt.setString(3,email);
            pstmt.setDate(4, java.sql.Date.valueOf(date));
            int rows = pstmt.executeUpdate();
            if(rows > 0){
                System.out.println("STUDENT ADDED");
            }
            else{
                System.out.println("OPERATION FAILED");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *Update a student by their ID with new email
     */
    public static void updateStudentEmail(int student_id, String new_email){
        //set query
        String update = String.format(" UPDATE students SET email = ? WHERE student_id = ?" );
        try (PreparedStatement pstmt = conn.prepareStatement(update)) {
            pstmt.setString(1,new_email);
            pstmt.setInt(2,student_id);
            int rows = pstmt.executeUpdate();
            if(rows > 0){
                System.out.println("STUDENT UPDATED");
            }
            else{
                System.out.println("STUDENT NOT FOUND");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Delete specified student by ID
    public static void deleteStudent(int student_id){
        String delete = String.format("DELETE FROM students WHERE student_id = ?");
        try (PreparedStatement pstmt = conn.prepareStatement(delete)) {
            pstmt.setInt(1,student_id);
            int rows = pstmt.executeUpdate();
            if(rows > 0){
                System.out.println("STUDENT DELETED");
            }
            else{
                System.out.println("STUDENT NOT FOUND");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        // JDBC & Database credentials: EDIT CREDENTIALS HERE
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "postgres";
        int choice = -1;
        Scanner input = new Scanner(System.in);
        try { // Load PostgreSQL JDBC Driver
            Class.forName("org.postgresql.Driver");
            // Connect to the database
            conn = DriverManager.getConnection(url, user, password);
            if (conn != null) {
                //Main program flow
                System.out.println("Connected to PostgreSQL successfully!");
                while(choice != 0){
                    //Show Control Flow
                    System.out.println("Options\n" +
                            "(1) Get All Students\n" +
                            "(2) Add Student\n" +
                            "(3) Update Student\n" +
                            "(4) Delete Student\n" +
                            "(0) Quit\n");

                    //NOTE: No error handling was done for types
                    System.out.println("Enter Choice: ");
                    choice = input.nextInt();

                    if (choice == 1){
                        getAllStudents();
                    }
                    else if (choice == 2){

                        System.out.println("FIRST NAME: ");
                        String first_name = input.next();
                        System.out.println("LAST NAME: ");
                        String last_name = input.next();
                        System.out.println("EMAIL: ");
                        String email = input.next();
                        System.out.println("DATE (YYYY-MM-DD): ");
                        String date = input.next();

                        addStudent(first_name,last_name,email,date);

                    }
                    else if (choice == 3){

                        System.out.println("ID: ");
                        int id = input.nextInt();
                        System.out.println("NEW EMAIL: ");
                        String email = input.next();

                        updateStudentEmail(id,email);
                    }
                    else if (choice == 4){
                        System.out.println("ID: ");
                        int id = input.nextInt();

                        deleteStudent(id);
                    }
                    else if (choice == 0){
                        System.out.println("EXITING");
                    }
                    else {
                        System.out.println("Invalid option. Try again");
                    }

                }

                input.close();


            } else {
                System.out.println("Failed to establish connection.");
            } // Close the connection (in a real scenario, do this in a finally)
            conn.close();


        }
        catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}