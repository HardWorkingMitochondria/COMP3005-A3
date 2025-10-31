Running Steps:
    Edit url, username and password as needed at start of main for personal PostgreSQL server

    From IntelliJ:
        Open Project folder in IntelliJ
        Select Main file from src
        Run Program

    From command line (Windows):
        Navigate to project folder
        change directory to src 
        run:
        java -cp .;C:<YOURPOSTGRESQLJARPATH>\postgresql-42.7.8.jar Main.java

NOTE:
Program was made for use on existing databases.
Before running this application, Use the DDL.SQL and DML.SQL to create the database in PostgreSQL