# JMCP Server - Application Execution API Documentation

## Overview

This JMCP (Java Message Control Protocol) Server provides a JSON-RPC 2.0 API for discovering and executing installed applications on your system. It automatically detects applications based on your operating system (Windows, macOS, or Linux).

## Supported Operating Systems

- **Windows**: Scans PATH, Program Files, Program Files (x86), and Local AppData\Programs
- **macOS**: Scans PATH and /Applications folder
- **Linux**: Scans PATH environment variable

## Available RPC Methods

### 1. ping
**Description**: Simple health check to verify the server is running.

**Request**:
```json
{
  "jsonrpc": "2.0",
  "method": "ping",
  "id": 1
}
```

**Response**:
```json
{
  "jsonrpc": "2.0",
  "result": "pong",
  "id": 1
}
```

---

### 2. listCommands
**Description**: Lists all discovered applications and commands available on the system.

**Request**:
```json
{
  "jsonrpc": "2.0",
  "method": "listCommands",
  "id": 2
}
```

**Response**:
```json
{
  "jsonrpc": "2.0",
  "result": {
    "operatingSystem": "windows 10",
    "totalCommands": 150,
    "commands": {
      "notepad": "C:\\Windows\\System32\\notepad.exe",
      "chrome": "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe",
      "code": "C:\\Users\\YourName\\AppData\\Local\\Programs\\Microsoft VS Code\\Code.exe",
      "git": "C:\\Program Files\\Git\\cmd\\git.exe"
    }
  },
  "id": 2
}
```

---

### 3. runCommand
**Description**: Executes an installed application. Supports both simple command names and commands with arguments.

#### Simple Command Execution

**Request** (using string parameter):
```json
{
  "jsonrpc": "2.0",
  "method": "runCommand",
  "params": "notepad",
  "id": 3
}
```

**Request** (using object parameter):
```json
{
  "jsonrpc": "2.0",
  "method": "runCommand",
  "params": {
    "name": "notepad"
  },
  "id": 3
}
```

**Response**:
```json
{
  "jsonrpc": "2.0",
  "result": "Executed command: notepad",
  "id": 3
}
```

#### Command with Arguments

**Request**:
```json
{
  "jsonrpc": "2.0",
  "method": "runCommand",
  "params": {
    "name": "notepad",
    "args": ["C:\\Users\\YourName\\Documents\\file.txt"]
  },
  "id": 4
}
```

**Response**:
```json
{
  "jsonrpc": "2.0",
  "result": "Executed command: notepad",
  "id": 4
}
```

#### Command Not Found

**Response**:
```json
{
  "jsonrpc": "2.0",
  "result": "Command not found: invalidapp",
  "id": 5
}
```

---

### 4. echo
**Description**: Returns whatever you send to it (for testing).

**Request**:
```json
{
  "jsonrpc": "2.0",
  "method": "echo",
  "params": {"test": "data"},
  "id": 6
}
```

**Response**:
```json
{
  "jsonrpc": "2.0",
  "result": {"test": "data"},
  "id": 6
}
```

---

## Error Responses

### Invalid Request
```json
{
  "jsonrpc": "2.0",
  "error": {
    "code": -32600,
    "message": "Invalid Request"
  },
  "id": null
}
```

### Method Not Found
```json
{
  "jsonrpc": "2.0",
  "error": {
    "code": -32601,
    "message": "Method not found: unknownMethod"
  },
  "id": 7
}
```

### Invalid Params
```json
{
  "jsonrpc": "2.0",
  "error": {
    "code": -32602,
    "message": "Invalid params"
  },
  "id": 8
}
```

### Parse Error
```json
{
  "jsonrpc": "2.0",
  "error": {
    "code": -32700,
    "message": "Parse error"
  },
  "id": null
}
```

### Server Error
```json
{
  "jsonrpc": "2.0",
  "error": {
    "code": -32000,
    "message": "Server error",
    "data": "Detailed error message here"
  },
  "id": 9
}
```

---

## Usage Examples

### Using cURL (Windows)

#### List all available commands:
```bash
curl -X POST http://localhost:8080/rpc ^
  -H "Content-Type: application/json" ^
  -d "{\"jsonrpc\":\"2.0\",\"method\":\"listCommands\",\"id\":1}"
```

#### Execute Notepad:
```bash
curl -X POST http://localhost:8080/rpc ^
  -H "Content-Type: application/json" ^
  -d "{\"jsonrpc\":\"2.0\",\"method\":\"runCommand\",\"params\":\"notepad\",\"id\":2}"
```

#### Execute Chrome with URL:
```bash
curl -X POST http://localhost:8080/rpc ^
  -H "Content-Type: application/json" ^
  -d "{\"jsonrpc\":\"2.0\",\"method\":\"runCommand\",\"params\":{\"name\":\"chrome\",\"args\":[\"https://google.com\"]},\"id\":3}"
```

### Using cURL (Linux/macOS)

#### List all available commands:
```bash
curl -X POST http://localhost:8080/rpc \
  -H "Content-Type: application/json" \
  -d '{"jsonrpc":"2.0","method":"listCommands","id":1}'
```

#### Execute a command:
```bash
curl -X POST http://localhost:8080/rpc \
  -H "Content-Type: application/json" \
  -d '{"jsonrpc":"2.0","method":"runCommand","params":"notepad","id":2}'
```

### Using JavaScript/Fetch

```javascript
async function listCommands() {
  const response = await fetch('http://localhost:8080/rpc', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      jsonrpc: '2.0',
      method: 'listCommands',
      id: 1
    })
  });
  
  const data = await response.json();
  console.log(data.result);
}

async function runCommand(commandName, args = null) {
  const params = args ? { name: commandName, args } : commandName;
  
  const response = await fetch('http://localhost:8080/rpc', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      jsonrpc: '2.0',
      method: 'runCommand',
      params,
      id: 2
    })
  });
  
  const data = await response.json();
  return data.result;
}

// Usage
listCommands();
runCommand('notepad');
runCommand('chrome', ['https://google.com']);
```

### Using Python

```python
import requests
import json

def list_commands():
    payload = {
        "jsonrpc": "2.0",
        "method": "listCommands",
        "id": 1
    }
    
    response = requests.post(
        'http://localhost:8080/rpc',
        headers={'Content-Type': 'application/json'},
        data=json.dumps(payload)
    )
    
    return response.json()

def run_command(command_name, args=None):
    params = command_name if args is None else {
        "name": command_name,
        "args": args
    }
    
    payload = {
        "jsonrpc": "2.0",
        "method": "runCommand",
        "params": params,
        "id": 2
    }
    
    response = requests.post(
        'http://localhost:8080/rpc',
        headers={'Content-Type': 'application/json'},
        data=json.dumps(payload)
    )
    
    return response.json()

# Usage
commands = list_commands()
print(f"Found {commands['result']['totalCommands']} commands")

result = run_command('notepad')
print(result['result'])
```

---

## Running the Server

### Start the Server
```bash
cd jmcp-server
mvnw.cmd spring-boot:run    # Windows
./mvnw spring-boot:run      # Linux/macOS
```

The server will start on `http://localhost:8080` by default.

### Build the Project
```bash
cd jmcp-server
mvnw.cmd clean package      # Windows
./mvnw clean package        # Linux/macOS
```

---

## How It Works

### Command Detection

1. **PATH Scanning**: The server scans all directories in the system's PATH environment variable
   - On Windows: Looks for `.exe` files
   - On Linux/macOS: Looks for executable files

2. **OS-Specific Directories**:
   - **Windows**: 
     - `C:\Program Files`
     - `C:\Program Files (x86)`
     - `%LOCALAPPDATA%\Programs`
   - **macOS**: 
     - `/Applications` (for .app bundles)

3. **Command Execution**:
   - **Windows**: Uses `cmd /c start` to launch applications properly
   - **macOS**: Uses `open -a` for GUI applications
   - **Linux**: Direct process execution

### Notes on Application Launching

- GUI applications are launched independently and don't block the server
- Command-line tools can be executed with arguments
- Paths with spaces are automatically quoted
- Applications run in detached mode

---

## Common Use Cases

1. **Remote Application Management**: Execute applications on remote machines
2. **Automation Scripts**: Integrate with automation workflows
3. **System Administration**: Launch system utilities programmatically
4. **Development Tools**: Open IDEs, editors, or browsers with specific files/URLs
5. **Testing**: Automated testing of application launches

---

## Security Considerations

- This server allows execution of ANY installed application on the system
- It should only be run on trusted networks
- Consider implementing authentication for production use
- Be cautious about which commands you expose to external clients

---

## Troubleshooting

### Command Not Found
- Run `listCommands` to see all available commands
- Check if the application is in your PATH or standard installation directories
- On Windows, ensure the `.exe` file exists
- Command names are case-insensitive

### Application Doesn't Launch
- Check console logs for error messages
- Verify the application path is correct
- Ensure you have permissions to execute the application
- Some applications may require specific working directories

### Server Won't Start
- Check if port 8080 is already in use
- Verify Java 21+ is installed
- Check the console for error messages

---

## API Endpoint

**Base URL**: `http://localhost:8080/rpc`  
**Method**: POST  
**Content-Type**: application/json

All requests must follow JSON-RPC 2.0 specification.
