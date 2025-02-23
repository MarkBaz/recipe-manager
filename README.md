# Recipe Manager

This is a full-stack application for managing recipes. It includes a Spring Boot backend and a React frontend.

## 📌 Installation Steps

### 1️⃣  Install Dependencies

#### **Backend (Spring Boot)**
- Ensure you have **Java 17+** and **Maven** installed.
- Navigate to `recipemanager/` and run:
  mvn clean install

#### **Frontend (React)**
- Ensure Node.js (v18+) and npm are installed.
- Navigate to recipemanagerFront/ and run:
  npm install

### Set Up the Database
- psql -U username -d recipe_db -f recipe_db_dump.sql
- Replace username with your actual PostgreSQL username.

## Run the Project
- Start Backend
 -- cd recipemanager
 -- mvn spring-boot:run
- Start Frontend
 -- cd recipemanagerFront
 -- npm start

Your application will be accessible at http://localhost:5173

# Features
- User authentication (login/signup)
- Add recipes
- Recipe search & filtering
- Ratings & comments
- Favorite recipes



