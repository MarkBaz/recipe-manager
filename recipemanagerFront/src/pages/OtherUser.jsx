import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../services/api";
import "../styles/OtherUser.css"; 

function OtherUser() {
  const { userId } = useParams();
  const [user, setUser] = useState(null);
  const [recipes, setRecipes] = useState([]);
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  useEffect(() => {
    fetchUserProfile();
    fetchUserRecipes();
  }, [userId]);

  const fetchUserProfile = async () => {
    try {
      const response = await api.get(`/users/${userId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setUser(response.data);
    } catch (error) {
      console.error("Failed to fetch user profile:", error);
    }
  };

  const fetchUserRecipes = async () => {
    try {
      const response = await api.get(`/recipes/user/${userId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      const recipesWithRatings = await Promise.all(
        response.data.map(async (recipe) => {
          try {
            const ratingResponse = await api.get(`/ratings/recipe/${recipe.recipeId}/average`, {
              headers: { Authorization: `Bearer ${token}` },
            });
            return { ...recipe, averageRating: ratingResponse.data };
          } catch (error) {
            console.error(`Failed to fetch rating for recipe ${recipe.recipeId}`, error);
            return { ...recipe, averageRating: 0.0 };
          }
        })
      );

      setRecipes(recipesWithRatings);
    } catch (error) {
      console.error("Failed to fetch user recipes:", error);
    }
  };

  return (
    <div className="main-content">
      {user ? (
        <>
          <h1>{user.firstname} {user.lastname}</h1>
          <p><strong>Email:</strong> {user.email}</p>
          <p><strong>Recipes Created:</strong> {recipes.length}</p>

          <h2>{user.firstname}'s Recipes</h2>
          <div className="recipes-list">
            {recipes.map((recipe) => (
              <div 
                key={recipe.recipeId} 
                className="recipe-card" 
                onClick={() => navigate(`/recipes/${recipe.recipeId}`)}
              >
                <div className="recipe-details">
                  <h3>{recipe.title}</h3>
                  <p>{recipe.description}</p>
                  <p>⭐ Average Rating: {typeof recipe.averageRating === "number" ? recipe.averageRating.toFixed(1) : "No ratings yet"}</p>
                </div>
              </div>
            ))}
          </div>
        </>
      ) : (
        <p>Loading user profile...</p>
      )}
    </div>
  );
}

export default OtherUser;
