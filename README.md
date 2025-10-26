# 🚀 JMCP - Just My Command Processor

[![Java Version](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

> **Control your system with natural language.** JMCP is an intelligent CLI that understands English, Hinglish, slang, and even typos—making system automation effortless.

```bash
jmcp> yr chrome khol na
✓ Chrome launched successfully!

jmcp> create file report.txt in downloads
✓ File created: C:/Users/HP/Downloads/report.txt
```

---

## 📑 Table of Contents

- [What is JMCP?](#-what-is-jmcp)
- [Key Features](#-key-features)
- [Installation](#-installation)
- [Usage Examples](#-usage-examples)
- [Supported Commands](#-supported-commands)
- [How It Works](#-how-it-works)
- [Configuration](#-configuration)
- [Tech Stack](#-tech-stack)
- [Roadmap](#-roadmap)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🌟 What is JMCP?

**JMCP (Just My Command Processor)** is a smart command-line interface that bridges the gap between natural human language and system operations. Instead of remembering exact commands or navigating through folders, just tell JMCP what you want in plain English (or Hinglish!).

### Why JMCP?

- **🗣️ Natural Language:** Talk to your computer like you talk to a friend
- **🌐 Multi-Language:** Supports English, Hinglish, and mixed language commands
- **🎯 Typo-Tolerant:** Works even with spelling mistakes and missing spaces
- **⚡ All-in-One:** System apps, file management, and web browsing in a single interface
- **🤖 AI-Powered:** Uses advanced NLP/AI for intelligent command interpretation

---

## ✨ Key Features

### 1️⃣ **Natural Language Command Processing**

Speak naturally—JMCP understands conversational commands:

```bash
"calculator pen kr do"     → Opens Calculator
"please camera open kr do" → Opens Camera
"thoda word launch kr do"  → Opens Microsoft Word
"bhai chrome band karo"    → Closes Chrome
```

### 2️⃣ **Smart Web Integration**

Open websites and search platforms instantly:

```bash
"open google.com"               → Opens Google
"linkedin chalao"               → Opens LinkedIn
"java tutorial on youtube"     → YouTube search for "java tutorial"
"two sum problem on leetcode"  → LeetCode search for "two sum problem"
```

### 3️⃣ **File System Operations**

Manage files and folders with simple commands:

```bash
"create file report.txt in downloads"    → Creates file in Downloads
"create folder projects on desktop"      → Creates folder on Desktop
"delete file old_notes.txt from desktop" → Deletes specified file
"search for *.java in documents"         → Lists all Java files
"open downloads folder"                   → Opens Downloads in File Explorer
```

### 4️⃣ **Intelligent Parsing Engine**

- Recognizes **200+ command variations**
- Handles **typos and phonetic similarities**
- Supports **synonyms** (open/launch/start/khol/chalao)
- Falls back to **Google search** for unclear commands
- Always returns **strict JSON** responses

### 5️⃣ **Safe & Secure**

- Restricted to safe directories (Desktop, Downloads, Documents)
- Prevents dangerous system operations
- Validates all file paths before execution

---

## 🔧 Installation

### Prerequisites

- **Java 17+** ([Download](https://www.oracle.com/java/technologies/downloads/))
- **Maven 3.6+** ([Download](https://maven.apache.org/download.cgi))
- **Windows OS** (primary support; Linux/Mac coming soon)

### Quick Start

```bash
# Clone the repository
git clone https://github.com/yourusername/jmcp.git
cd jmcp

# Build the project
mvn clean install

# Run the application
java -jar target/jmcp-server.jar
```

### Alternative: Run with Maven

```bash
mvn spring-boot:run
```

The CLI will start and display:

```
╔════════════════════════════════════════╗
║   JMCP - Just My Command Processor     ║
║   Type commands naturally | Type 'exit' to quit   ║
╚════════════════════════════════════════╝

jmcp> _
```

---

## 🎯 Usage Examples

### System Applications

```bash
jmcp> chrome kholo
✓ Chrome launched successfully!

jmcp> calculator pen kr do
✓ Calculator launched successfully!

jmcp> word document open karo
✓ Microsoft Word launched successfully!

jmcp> camera chalao yaar
✓ Camera launched successfully!
```

### File Operations

```bash
jmcp> create file ankit.py in downloads
✓ File created: C:/Users/HP/Downloads/ankit.py

jmcp> create folder ReactProjects on desktop
✓ Folder created: C:/Users/HP/Desktop/ReactProjects

jmcp> delete file test.txt from downloads
✓ File deleted: C:/Users/HP/Downloads/test.txt

jmcp> search for *.pdf in documents
Found 5 files:
  - report.pdf
  - invoice.pdf
  - tutorial.pdf
```

### Web Search & Navigation

```bash
jmcp> open youtube.com
✓ Opened YouTube in default browser

jmcp> java tutorial on youtube
✓ Opened YouTube search for "java tutorial"

jmcp> dp problem on leetcode
✓ Opened LeetCode search for "dp problem"

jmcp> search spring boot tutorial on google
✓ Opened Google search for "spring boot tutorial"
```

### Mixed Language Commands

```bash
jmcp> chrome mein google khol do
✓ Opened Google in Chrome

jmcp> downloads folder dikhao
✓ Opened Downloads folder

jmcp> python file banao desktop pe
✓ File created: C:/Users/HP/Desktop/untitled.py
```

---

## 📋 Supported Commands

### System Applications

| Command Variants | Action |
|------------------|--------|
| `chrome`, `google chrome`, `chrome kholo` | Opens Google Chrome |
| `calculator`, `calc`, `calculator open karo` | Opens Calculator |
| `camera`, `webcam`, `camera chalao` | Opens Camera |
| `word`, `ms word`, `word document` | Opens Microsoft Word |
| `notepad`, `text editor` | Opens Notepad |

### File Operations

| Command Type | Examples |
|--------------|----------|
| **Create File** | `create file report.txt in downloads` |
| **Create Folder** | `create folder MyProject on desktop` |
| **Delete File** | `delete file old.txt from downloads` |
| **Search Files** | `search for *.java in documents` |
| **List Files** | `list files in downloads` |
| **Open Folder** | `open desktop folder` |

### Web Commands

| Platform | Example Command |
|----------|----------------|
| **Google** | `search java on google` |
| **YouTube** | `javascript tutorial on youtube` |
| **LeetCode** | `two sum problem on leetcode` |
| **LinkedIn** | `open linkedin` |
| **GitHub** | `spring boot repo on github` |

---

## 🧠 How It Works

```
┌─────────────────┐
│  User Input     │  "chrome khol do"
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  NLP Parser     │  Tokenization, Normalization, Intent Recognition
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Command Router │  Maps to: OPEN_APP (chrome)
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Executor       │  Runtime.exec("start chrome")
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  JSON Response  │  {"status": "success", "message": "Chrome launched"}
└─────────────────┘
```

### Core Components

1. **Natural Language Processor:** Parses informal text using regex patterns and AI models
2. **Intent Classifier:** Identifies command type (app, file, web, search)
3. **Command Executor:** Safely executes system operations
4. **Response Handler:** Returns structured JSON feedback

---

## ⚙️ Configuration

### Customizing Commands

Edit `src/main/resources/commands.json` to add new applications:

```json
{
  "applications": {
    "vscode": {
      "executable": "code",
      "aliases": ["vs code", "visual studio code", "vscode kholo"]
    }
  }
}
```

### Setting Default Search Engine

Modify `application.properties`:

```properties
jmcp.search.default=google
jmcp.search.engines=google,youtube,leetcode,github
```

---

## 💻 Tech Stack

| Technology | Purpose |
|------------|---------|
| **Java 17+** | Core application logic |
| **Spring Boot 3.2** | Framework for CLI interface |
| **Gemini AI / Custom NLP** | Natural language understanding |
| **ProcessBuilder** | System command execution |
| **Maven** | Build and dependency management |
| **JUnit 5** | Unit testing |

---

## 🗺️ Roadmap

### Version 2.0 (Q1 2025)
- [ ] **Voice recognition** for hands-free commands
- [ ] **Linux & macOS support**
- [ ] **Command history** with search and replay
- [ ] **Alias customization** via config file

### Version 3.0 (Q2 2025)
- [ ] **AI-driven suggestions** (e.g., "You often search Java tutorials—open YouTube?")
- [ ] **Plugin system** for third-party integrations
- [ ] **Bash/PowerShell script execution**
- [ ] **Multi-language support** (Spanish, French, Hindi)

### Future Ideas
- Web dashboard for remote command execution
- Integration with smart home devices
- Natural language file content search
- Scheduled command execution

---

## 🤝 Contributing

We welcome contributions! Here's how you can help:

1. **Fork** the repository
2. Create a **feature branch** (`git checkout -b feature/AmazingFeature`)
3. **Commit** your changes (`git commit -m 'Add AmazingFeature'`)
4. **Push** to the branch (`git push origin feature/AmazingFeature`)
5. Open a **Pull Request**

### Development Setup

```bash
# Clone your fork
git clone https://github.com/yourusername/jmcp.git

# Create a branch
git checkout -b feature/new-command

# Make changes and test
mvn test

# Submit PR
git push origin feature/new-command
```

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for detailed guidelines.

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- Inspired by natural language interfaces like Siri and Alexa
- Built with ❤️ by developers who hate memorizing commands
- Special thanks to the Spring Boot and Java communities

---

## 📧 Contact


- GitHub: [@spee-dev](https://github.com/yourusername)


---

<div align="center">

