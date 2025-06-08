# DiaryKeeper - Comprehensive Diary Management System

## Project Overview
DiaryKeeper is a full-featured diary application built with Java 21 and JavaFX that provides secure user authentication, mood-based diary entries, search functionality, and data export capabilities. The application follows a layered architecture with clear separation between GUI, service, DAO, and database layers.

## Features

- 🔐 **User Authentication**:
    - Secure registration and login with password hashing
    - Session management
    - Role-based access (User/Admin)

- 📝 **Diary Management**:
    - Create, read, update, and delete diary entries
    - Mood tracking (Happy, Sad, Neutral, Excited, Anxious)
    - Rich text editing
    - Automatic timestamping

- 🔍 **Advanced Search**:
    - Search by content keywords
    - Filter by mood
    - View recent entries

- 📤 **Data Export**:
    - Export entries to text files
    - Automatic organization in "memories" directory
    - Preserves metadata (title, date, mood)

- 💾 **Database Integration**:
    - MySQL database backend
    - Automatic schema initialization
    - CRUD operations with JDBC

## Technologies Used

- **Core**: Java 21
- **GUI**: JavaFX 21
- **Database**: MySQL 8.x
- **Build System**: Maven
- **Dependencies**:
    - `mysql-connector-java` (8.0.33)
    - `javafx-controls` (21)
    - `javafx-fxml` (21)

## Prerequisites

1. **Java Development Kit 21** ([Download](https://jdk.java.net/21/))
2. **Maven 3.9+** ([Installation Guide](https://maven.apache.org/install.html))
3. **MySQL Server 8.x** ([Download](https://dev.mysql.com/downloads/mysql/))
4. **JavaFX SDK 21** ([Download](https://gluonhq.com/products/javafx/))

## Setup and Installation

### 1. Database Setup
```sql
CREATE DATABASE diaryKeeper;
CREATE USER 'diary_user'@'localhost' IDENTIFIED BY 'SecurePass123!';
GRANT ALL PRIVILEGES ON diaryKeeper.* TO 'diary_user'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Configuration
Update database credentials in `DatabaseConnection.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/diaryKeeper";
private static final String USERNAME = "diary_user";
private static final String PASSWORD = "SecurePass123!";
```

### 3. Build with Maven
```bash
mvn clean install
```

### 4. Run the Application
```bash
mvn javafx:run
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── dev/
│   │       └── diary/
│   │           ├── model/          # Domain models
│   │           │   ├── BaseEntity.java
│   │           │   ├── DiaryEntry.java
│   │           │   ├── EntryMood.java
│   │           │   ├── User.java
│   │           │   └── UserRole.java
│   │           ├── db/             # Database connection
│   │           │   └── DatabaseConnection.java
│   │           ├── dao/            # Data Access Objects
│   │           │   ├── CrudDAO.java
│   │           │   ├── DiaryEntryDAO.java
│   │           │   └── UserDAO.java
│   │           ├── service/        # Business logic
│   │           │   ├── DiaryService.java
│   │           │   ├── PasswordUtils.java
│   │           │   └── UserService.java
│   │           ├── gui/            # JavaFX interfaces
│   │           │   ├── DiaryView.java
│   │           │   ├── LoginView.java
│   │           │   ├── RegisterView.java
│   │           │   └── View.java
│   │           └── DiaryKeeper.java # Main class
│   ├── resources/
│   │   └── styles.css              # CSS styles
│   └── module-info.java            # Module configuration
└── test/                           # Unit tests
pom.xml                             # Maven configuration
```

## Key Components Explained

### 1. Domain Models (`model` package)
- **BaseEntity.java**: Abstract base class with common fields (ID, timestamps)
- **DiaryEntry.java**: Represents a diary entry with title, content, mood, and encryption flag
- **User.java**: User model with credentials and role
- **Enums (EntryMood, UserRole)**: Define constrained values

### 2. Database Layer (`db` package)
- **DatabaseConnection.java**:
    - Singleton pattern for database connection
    - Automatic table creation on first connection
    - MySQL JDBC implementation

### 3. Data Access Layer (`dao` package)
- **CrudDAO.java**: Generic CRUD operations interface
- **UserDAO.java**:
    - Implements user CRUD operations
    - Additional methods: `findByUsername`, `existsByEmail`
- **DiaryEntryDAO.java**:
    - Implements diary entry CRUD
    - Specialized queries: `findByUserId`, `searchByContent`

### 4. Service Layer (`service` package)
- **PasswordUtils.java**:
    - SHA-256 password hashing
    - Secure password verification
- **DiaryService.java**:
    - Business logic for diary operations
    - Entry validation
    - Facade for DAO operations
- **UserService.java**:
    - User authentication
    - Registration logic
    - Password management

### 5. Presentation Layer (`gui` package)
- **View.java**: Common interface for JavaFX views
- **LoginView.java**: User authentication interface
- **RegisterView.java**: New user registration
- **DiaryView.java**: Main diary interface with:
    - Entry creation/editing
    - Mood selection
    - Entry list with search
    - Export functionality

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Diary Entries Table
```sql
CREATE TABLE diary_entries (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    user_id INT NOT NULL,
    mood VARCHAR(50) NOT NULL,
    is_encrypted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

## Usage Guide

### 1. Registration
1. Launch application
2. Click "Register"
3. Enter username, email, and password
4. System will hash password and create user account

### 2. Creating a Diary Entry
1. Select mood from dropdown
2. Enter title and content
3. Click "Save Entry"
4. Entry appears in left sidebar

### 3. Searching and Filtering
- Use search box to find entries by content
- Select mood to filter entries
- Click entry in sidebar to view details

### 4. Exporting Entries
1. Select an entry
2. Click "Export"
3. Find text file in `memories/` directory

### 5. Managing Entries
- **Update**: Select entry → Edit content → Click "Save"
- **Delete**: Select entry → Click "Delete" → Confirm

## Best Practices Implemented

1. **Security**:
    - Password hashing with SHA-256
    - Prepared statements to prevent SQL injection
    - Separation of concerns

2. **Code Quality**:
    - SOLID principles
    - Layered architecture
    - Java 21 features (records, pattern matching)
    - Exception handling

3. **Performance**:
    - Database connection pooling
    - Efficient SQL queries
    - Lazy loading of entries

4. **Maintainability**:
    - Clear separation of concerns
    - Comprehensive JavaDoc
    - Modular design




