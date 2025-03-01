import React from "react";
import { Routes, Route } from "react-router-dom";
import Navbar from "./components/Navbar";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Profile from "./pages/Profile";
import PrivateRoute from "./components/PrivateRoute";
import Signup from "./pages/Signup";
import Recipes from "./pages/Recipes";
import MyRecipes from "./pages/MyRecipes";
import RecipeDetails from "./pages/RecipeDetails";
import Favorites from "./pages/Favorites";
import OtherUser from "./pages/OtherUser";
import SearchResults from "./pages/SearchResults";
import EditAccount from "./pages/EditAccount";
import "./styles/App.css";

function App() {
  return (
    <div className="app-container"> {/* Wrap everything inside a div */}
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/recipes" element={<Recipes />} />
        <Route
          path="/my-recipes"
          element={
            <PrivateRoute>
              <MyRecipes />
            </PrivateRoute>
          }
        />
        <Route
          path="/profile"
          element={
            <PrivateRoute>
              <Profile />
            </PrivateRoute>
          }
        />
        <Route path="/recipes/:recipeId" element={<RecipeDetails />} />
        <Route path="/favorites" element={<Favorites />} />
        <Route path="/users/:userId" element={<OtherUser />} />
        <Route path="/search-results" element={<SearchResults />} />
        <Route path="/edit-account" element={<EditAccount />} />
      </Routes>
    </div>
  );
}

export default App;
