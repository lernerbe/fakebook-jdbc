# EECS 484 Project 2: Fakebook JDBC

## Overview
This project is part of EECS 484 at the University of Michigan. It involves building a Java application that executes SQL queries against a relational database and processes the results using predefined data structures. The project provides hands-on experience with SQL query writing and real-world database application programming using JDBC.

## Technologies Used
- **Java** (JDK 1.7/1.8)
- **Oracle SQL** (SQL*Plus CLI)
- **JDBC** (ojdbc6.jar)
- **Makefile** for compilation and execution

## Project Structure
The project consists of multiple files, some of which are provided as a part of the starter code. Key files include:

- **`PublicFakebookOracleConstants.java`**: Defines table names and database schema constants. Do not modify this file.
- **`FakebookOracleUtilities.java`**: Provides utility classes for handling lists and printing results.
- **`FakebookOracleDataStructures.java`**: Defines data structures used to store query results.
- **`FakebookOracle.java`**: The abstract parent class that defines query function structures.
- **`StudentFakebookOracle.java`**: The main file where SQL queries are implemented.
- **`FakebookOracleMain.java`**: The main driver for running the application.
- **`Makefile`**: Provides easy compilation, execution, and cleaning commands.

## SQL Execution and Testing
### Using SQL*Plus
To debug and test queries before integrating them into Java, I used **SQL*Plus** with **Oracle SQL syntax**. This was particularly useful for:
- Checking query correctness in isolation
- Viewing table schemas using `DESC` commands
- Running queries interactively before translating them into Java

## Compilation and Execution
To compile and run the project, use the provided `Makefile`:
```sh
make          # Compiles the project
make query-N  # Runs query N and displays results
make time-N   # Runs query N and measures runtime
make clean    # Removes compiled files
```

