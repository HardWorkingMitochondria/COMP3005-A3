import java.sql.PreparedStatement;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    //Global Connection for all db operations
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
                System.out.printf("%d|%s|%s|%s|%s\n", id,first_name,last_name,email, date);//format output
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
        //Prepare query
        String insert = String.format(" INSERT INTO students (first_name, last_name, email, enrollment_date) VALUES(?,?,?,?)");
        try (PreparedStatement pstmt = conn.prepareStatement(insert)) {
            //Set vales for insertion
            pstmt.setString(1,first_name);
            pstmt.setString(2,last_name);
            pstmt.setString(3,email);
            pstmt.setDate(4, java.sql.Date.valueOf(date));

            //log rows changed
            int rows = pstmt.executeUpdate();

            //if 1 row is modified the query ran successfully
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

    /*
     *Update a student by their ID with new email
     */
    public static void updateStudentEmail(int student_id, String new_email){
        //Prepare update
        String update = String.format(" UPDATE students SET email = ? WHERE student_id = ?" );
        try (PreparedStatement pstmt = conn.prepareStatement(update)) {
            //Set email
            pstmt.setString(1,new_email);

            //Set student to change
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
        //Prepare delete
        String delete = String.format("DELETE FROM students WHERE student_id = ?");
        try (PreparedStatement pstmt = conn.prepareStatement(delete)) {
            //Set student to delete
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
    /*
    * For additional format/error handling in Java, the following resources were used for Pattern and Matcher and REGEX usage
    * https://www.w3schools.com/java/java_regex.asp
    * https://docs.oracle.com/javase/8/docs/api/java/util/regex/Matcher.html
    * */
    public static String valid_email(){
        Scanner input = new Scanner(System.in);
        //Define REGEX pattern
        Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        String email;

        while(true){//loop for valid email pattern
            System.out.println("EMAIL: ");
            email = input.next();

            //Check for email pattern
            Matcher matcher = pattern.matcher(email);
            if(matcher.matches()){
                return email;
            }
            else {
                System.out.println("INVALID FORMAT. TRY AGAIN");
            }
        }
    }
    /*
     * Return valid user date
     * NOTE: DOES NOT check for logical date, only checks format of date
     */
    public static String valid_date(){
        Scanner input = new Scanner(System.in);

        //Define date pattern
        Pattern pattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
        String date;

        while(true){//loop until valid date format is entered
            System.out.println("DATE (YYYY-MM-DD): ");
            date = input.next();
            Matcher matcher = pattern.matcher(date);
            if(matcher.matches()){
                return date;
            }
            else {
                System.out.println("INVALID FORMAT. TRY AGAIN");
            }
        }
    }
    public static int valid_integer(){
        Scanner input = new Scanner(System.in);
        while(true){
            System.out.println("Enter Number: ");
            if(input.hasNextInt()){
                int choice = input.nextInt();
                return choice;
            }
            else{
                System.out.println("INVALID TYPE. TRY AGAIN");
                input.next();
            }
        }
    }


    public static void main(String[] args) {
        // JDBC & Database credentials: EDIT CREDENTIALS HERE
        String url = "jdbc:postgresql://localhost:5432/students";
        String user = "postgres";
        String password = "postgres";
        //Set default choice and set up input
        int choice = -1;
        Scanner input = new Scanner(System.in);

        //Lecture 12 code
        try { // Load PostgreSQL JDBC Driver
            Class.forName("org.postgresql.Driver");
            // Connect to the database
            conn = DriverManager.getConnection(url, user, password);
            if (conn != null) {
                //Main program loop
                System.out.println("Connected to PostgreSQL successfully!");
                while(choice != 0){
                    //Show Control Flow
                    System.out.println("Options\n" +
                            "(1) Get All Students\n" +
                            "(2) Add Student\n" +
                            "(3) Update Student\n" +
                            "(4) Delete Student\n" +
                            "(0) Quit\n");
                    choice = valid_integer();
                    if (choice == 1){
                        getAllStudents();
                    }
                    else if (choice == 2){
                        System.out.println("FIRST NAME: ");
                        String first_name = input.next();
                        System.out.println("LAST NAME: ");
                        String last_name = input.next();
                        String email = valid_email();
                        String date = valid_date();
                        addStudent(first_name,last_name,email,date);
                    }
                    else if (choice == 3){
                        int id = valid_integer();
                        String email = valid_email();
                        updateStudentEmail(id,email);
                    }
                    else if (choice == 4){
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