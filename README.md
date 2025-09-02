# 🤖📊Smart Expense Tracker 

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

##🚀 Clone git repo
```bash
git clone https://github.com/anupchaudharyy/Java-Project-Expense-Tracker.git
```

### 1. Database Setup
-- Create database and tables
```bash
mysql -u root -p < database/schema.sql
```
### 2. Compile the file 
```bash
.\compile.bat
```
### 3. Run the file
```bash
.\run.bat
```
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


# Java-Project-Expense-Tracker
This is AI powered java swing desktop application `Expense Tracker System` that uses ollama python library for AI integration locally.

