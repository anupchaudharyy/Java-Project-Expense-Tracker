<<<<<<< HEAD
# Smart Expense Tracker 


### Software Requirements
- **Java Development Kit (JDK) 17 or higher**
- **MySQL Server 8.0 or higher**
- **Python 3.8+ with Ollama** (for AI integration)

### Library Dependencies
- mysql-connector jar file
- json simple jar file
- jfreechart-jar file
- jcommon-jar file
- flatlaf-jar file

## Explaination
-dao/: This package contains the Data Access Objects (DAOs), which are responsible for interacting with the database.
-DatabaseManager.java: Manages the database connection.
-ExpenseDAO.java: Handles database operations related to expenses.
-UserDAO.java: Handles database operations related to users.
-exceptions/: This package contains custom exception classes.
-DatabaseException.java: An exception for database-related errors.
-ValidationException.java: An exception for data validation errors.
-gui/: This package contains the classes for the graphical user interface (GUI).
-ExpensePanel.java: The panel for displaying and managing expenses.
-GradientPanel.java: A custom panel with a gradient background.
-LoginDialog.java: The dialog for user login.
-MainFrame.java: The main window of the application.
-MyChartPanel.java: A custom panel for displaying charts.
-RegistrationDialog.java: The dialog for user registration.
-ReportsPanel.java: The panel for displaying financial reports.
-main/: This package contains the main entry point of the application.
-ExpenseTrackerApp.java: The main class that starts the application.
-model/: This package contains the data model classes.
-Expense.java: Represents an expense.
-ExpenseCategory.java: Represents a category for expenses.
-User.java: Represents a user.
-service/: This package contains the service classes, which implement the -business logic of the application.
-ExpenseService.java: Handles business logic related to expenses.
-OllamaClient.java: A client for interacting with the Ollama AI service.
-ReportService.java: Handles the generation of reports.
-utils/: This package contains utility classes.
-BackgroundSaver.java: A utility for saving data in the background.
-ExceptionHandler.java: A utility for handling exceptions.
-FileManager.java: A utility for managing files.


- /database/
This folder contains database-related files.
schema.sql: An SQL script for creating the database schema.

- /lib/
This folder contains the external libraries (JAR files) used by the project.
  -flatlaf-3.6.jar: A library for creating modern look and feels for Swing applications.
  -jcommon-1.0.24.jar: A library of common utility classes.
  -jfreechart-1.5.3.jar: A library for creating charts.
  -json-simple-1.1.1.jar: A library for working with JSON data.
  -mysql-connector-j-9.4.0.jar: The JDBC driver for connecting to a MySQL database.

- /python/
This folder contains Python scripts.
ollama_server.py: A Python script that likely runs a server for the Ollama AI service.


## Step-by-Step Setup

### 1. Database Setup
-- Create database and tables
mysql -u root -p < database/schema.sql

### 2. Compile the file 
- .\compile.bat

### 3. Run the file
- .\run.bat

### 4. AI Setup and Troubleshooting
- **Start Ollama**: Make sure Ollama is running (`ollama serve`)
- **Install AI Model**: Pull the required model (`eg: ollama pull gemma3:270m`)
- **Start AI Server**: `python python/ollama_server.py`

#### Troubleshooting AI Issues
If the AI suggestion feature isn't working:

1. **Check Python Server**: Ensure `python python/ollama_server.py` is running
2. **Check Ollama**: Verify Ollama is running with `ollama serve`
3. **Check Ports**: Verify port 5000 is available and not blocked by firewall
4. **Check Logs**: Review `python_server.log` for detailed error messages

=======
# Java-Project-Expense-Tracker
This is AI powered java swing desktop application `Expense Tracker System` that uses ollama python library for AI integration locally.
>>>>>>> e074a051a5c33d9962b274508e3e33eae07a896d
