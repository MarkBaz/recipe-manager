# Recipe Manager

This is a full-stack application for managing recipes. It includes a Spring Boot backend and a React frontend.

## 📌 Installation Steps

<<<<<<< HEAD
###   Install Dependencies
=======
### Install Dependencies
>>>>>>> ef2f395 (Implemented delete user, updated read me file)

#### **Backend (Spring Boot)**
- Ensure you have **Java 17+** and **Maven** installed.
- If using IntelliJ, go to Preferences → Plugins → Search for "Lombok" → Install
- If using Eclipse, go to Help → Eclipse Marketplace → Search for "Lombok" → Install
- Make sure `Enable annotation processing` is turned ON in your IDE settings.
- Navigate to `recipemanager/` and run:
  mvn clean install

#### **Frontend (React)**
- Ensure Node.js (v18+) and npm are installed.
- Navigate to recipemanagerFront/ and run:
  npm install

### Set Up the Database
- Ensure PostgreSQL is installed.
- psql -U username -d recipe_db -f recipe_db_dump.sql
- Replace username with your actual PostgreSQL username.

## Run the Project
- Start Backend
 -> cd recipemanager
 -> mvn spring-boot:run
- Start Frontend
 -> cd recipemanagerFront
 -> npm start

Your application will be accessible at http://localhost:5173

# Features
- User authentication (login/signup)
- Add, edit, and delete recipes
- Recipe search & filtering
- Ratings & comments
- Favorite recipes



