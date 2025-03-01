import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import "../styles/Recipes.css";


function Recipes() {
  const [recipes, setRecipes] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState("");
  const [favoriteRecipes, setFavoriteRecipes] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  const navigate = useNavigate();
  const token = localStorage.getItem("token");
  const userId = localStorage.getItem("userId");

  useEffect(() => {
    fetchCategories();
    fetchRecipes();
    fetchUserFavorites();
  }, []);

  const fetchCategories = async () => {
    try {
      const response = await api.get("/categories", {
        headers: { Authorization: `Bearer ${token}` },
      });
      setCategories(response.data);
    } catch (error) {
      console.error("Failed to fetch categories", error);
    }
  };

  const fetchRecipes = async () => {
    setIsLoading(true);
    try {
      const response = await api.get("/recipes", {
        headers: { Authorization: `Bearer ${token}` },
      });

      // fetch and set average rating for each recipe
      const recipesWithRatings = await Promise.all(response.data.map(async (recipe) => {
        try {
            const ratingResponse = await api.get(`/ratings/recipe/${recipe.recipeId}/average`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            return { ...recipe, averageRating: ratingResponse.data };
        } catch (error) {
            console.error(`Failed to fetch rating for recipe ${recipe.recipeId}`, error);
            return { ...recipe, averageRating: 0.0 };
        }
    }))
      setRecipes(recipesWithRatings);
    } catch (error) {
      console.error("Failed to fetch recipes:", error);
    }
    setIsLoading(false);
  };

  const fetchUserFavorites = async () => {
    try {
      const response = await api.get(`/favorites/user/${userId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setFavoriteRecipes(response.data);
    } catch (error) {
      console.error("Failed to fetch user favorites:", error);
    }
  };

  const handleCategoryChange = async (e) => {
    const categoryId = e.target.value;
    setSelectedCategory(categoryId);

    if (categoryId === "") {
      fetchRecipes();
    } else {
      try {
        const response = await api.get(`/recipes/category/${categoryId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        // fetch and attach average ratings just like in fetchRecipes
        const recipesWithRatings = await Promise.all(
          response.data.map(async (recipe) => {
            try {
              const ratingResponse = await api.get(`/ratings/recipe/${recipe.recipeId}/average`, {
                headers: { Authorization: `Bearer ${token}` },
              });
              return { ...recipe, averageRating: ratingResponse.data ?? 0.0 };
            } catch (error) {
              console.error(`Failed to fetch rating for recipe ${recipe.recipeId}`, error);
              return { ...recipe, averageRating: 0.0 };
            }
          })
        );
        setRecipes(recipesWithRatings);
      } catch (error) {
        console.error("Failed to fetch recipes by category", error);
      }
    }
  };

  const toggleFavorite = async (recipe) => {
    try {
      const isFavorite = favoriteRecipes.some((fav) => fav.recipeId === recipe.recipeId);

        if (isFavorite) {
            // remove from favorites
            await api.delete(`/favorites/user/${userId}/recipe/${recipe.recipeId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            // update state to reflect removal
            setFavoriteRecipes((prevFavorites) =>
              prevFavorites.filter((fav) => fav.recipeId !== recipe.recipeId)
          );
        } else {
            // else add to favorites
            await api.post(
                "/favorites",
                { userId, recipeId: recipe.recipeId },
                { headers: { Authorization: `Bearer ${token}` } }
            );
            // update state to reflect addition
            setFavoriteRecipes((prevFavorites) => [...prevFavorites, recipe]);
        }
    } catch (error) {
      console.error("Failed to toggle favorite:", error);
    }
  };

  return (
    <div className="main-content">
      <h1>All Recipes</h1>

      <div className="category-container">
        <select 
          value={selectedCategory} 
          onChange={handleCategoryChange} 
          className="category-dropdown"
        >
          <option value="">All Categories</option>
          {categories.map((cat) => (
            <option key={cat.categoryId} value={cat.categoryId}>
              {cat.name}
            </option>
          ))}
        </select>
      </div>

      {isLoading ? <p>Loading recipes...</p> : (
        <div className="recipes-list">
          {recipes.map((recipe) => (
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
              
              <button
                onClick={(e) => {
                  e.stopPropagation();
                  toggleFavorite(recipe);
                }}
                className={`favorite-btn ${favoriteRecipes.some((fav) => fav.recipeId === recipe.recipeId) ? "favorited" : ""}`}
              >
                ❤️ Favorite
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default Recipes;
