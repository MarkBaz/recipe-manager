import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import "../styles/Favorites.css";

function Favorites() {
  const [favoriteRecipes, setFavoriteRecipes] = useState([]);
  const navigate = useNavigate();
  const token = localStorage.getItem("token");
  const userId = localStorage.getItem("userId");

  useEffect(() => {
    fetchFavorites();
  }, []);

  const fetchFavorites = async () => {
    try {
      const response = await api.get(`/favorites/user/${userId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      // Fetch and set average rating for each recipe
      const favoritesWithRatings = await Promise.all(response.data.map(async (recipe) => {
        try {
            const ratingResponse = await api.get(`/ratings/recipe/${recipe.recipeId}/average`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            return { ...recipe, averageRating: ratingResponse.data }; // Store rating
        } catch (error) {
            console.error(`Failed to fetch rating for recipe ${recipe.recipeId}`, error);
            return { ...recipe, averageRating: 0.0 }; // Default to 0
        }
    }));

      setFavoriteRecipes(favoritesWithRatings);
    } catch (error) {
      console.error("Failed to fetch favorite recipes:", error);
    }
  };

  const removeFromFavorites = async (recipeId) => {
    try {
      // Remove from favorites
      await api.delete(`/favorites/user/${userId}/recipe/${recipeId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      // Update state to reflect removal
      setFavoriteRecipes((prevFavorites) => prevFavorites.filter((fav) => fav.recipeId !== recipeId));
    } catch (error) {
      console.error("Failed to remove favorite:", error);
    }
  };

  return (
    <div className="main-content">
      <h1>My Favorite Recipes</h1>

      {favoriteRecipes.length === 0 ? (
        <p>No favorite recipes yet.</p>
      ) : (
        <div className="favorites-list">
          {favoriteRecipes.map((recipe) => (
            <div key={recipe.recipeId} className="recipe-card" onClick={() => navigate(`/recipes/${recipe.recipeId}`)}>
              <div className="recipe-details">
                <h3>{recipe.title}</h3>
                <p>{recipe.description}</p>
                <p>By: {recipe.user ? (
                  <span 
                    className="clickable-user" 
                    onClick={(e) => {
                      e.stopPropagation();
                      navigate(`/users/${recipe.user.userId}`);
                    }}
                  >
                    {recipe.user.firstname} {recipe.user.lastname}
                  </span>
                ) : "Unknown User"}
              </p>

                <p>⭐ Average Rating: {typeof recipe.averageRating === "number" ? recipe.averageRating.toFixed(1) : "No ratings yet"}</p>
              </div>

              {/* Remove Favorite Button */}
              <button
                className="remove-favorite-btn"
                onClick={(e) => {
                  e.stopPropagation();
                  removeFromFavorites(recipe.recipeId);
                }}
              >
                ❌ Remove
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default Favorites;
