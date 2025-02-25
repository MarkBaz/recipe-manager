import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import api from "../services/api";
import "../styles/SearchResults.css";

function Searched() {
  const [userResults, setUserResults] = useState([]);
  const [recipeResults, setRecipeResults] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  const navigate = useNavigate();
  const location = useLocation();
  const token = localStorage.getItem("token");

  // Extract search query from URL
  const queryParams = new URLSearchParams(location.search);
  const searchQuery = queryParams.get("query");

  useEffect(() => {
    if (!searchQuery) return;

    const fetchResults = async () => {
      try {
        const [userRes, recipeRes] = await Promise.all([
          api.get(`/users/search?keyword=${searchQuery}`, {
            headers: { Authorization: `Bearer ${token}` },
          }),
          api.get(`/recipes/search?keyword=${searchQuery}`, {
            headers: { Authorization: `Bearer ${token}` },
          }),
        ]);

        // Fetch number of recipes for each user
        const usersWithRecipeCounts = await Promise.all(
          userRes.data.map(async (user) => {
            try {
              const userRecipes = await api.get(`/recipes/user/${user.userId}`, {
                headers: { Authorization: `Bearer ${token}` },
              });
              return { ...user, recipeCount: userRecipes.data.length };
            } catch (error) {
              console.error(`Failed to fetch recipes for user ${user.userId}`, error);
              return { ...user, recipeCount: 0 };
            }
          })
        );

        // Fetch average rating for each recipe
        const recipesWithRatings = await Promise.all(
          recipeRes.data.map(async (recipe) => {
            try {
              const ratingRes = await api.get(`/ratings/recipe/${recipe.recipeId}/average`, {
                headers: { Authorization: `Bearer ${token}` },
              });
              return { ...recipe, averageRating: ratingRes.data ?? 0.0 };
            } catch (error) {
              console.error(`Failed to fetch rating for recipe ${recipe.recipeId}`, error);
              return { ...recipe, averageRating: 0.0 };
            }
          })
        );

        setUserResults(usersWithRecipeCounts);
        setRecipeResults(recipesWithRatings);
      } catch (error) {
        console.error("Error fetching search results:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchResults();
  }, [searchQuery]);

  return (
    <div className="main-content">
      <h1>Search Results for "{searchQuery}"</h1>
  
      {isLoading ? (
        <p>Loading results...</p>
      ) : (
        <>
          {/* Results Container */}
          <div className="search-results-container">
            {/* Users Section */}
            <div className="users-section">
              <h2>Users</h2>
              {userResults.length > 0 ? (
                <div className="search-user-list">
                  {userResults.map((user) => (
                    <div key={user.userId} className="search-user-card" onClick={() => navigate(`/users/${user.userId}`)}>
                      <h3>{user.firstname} {user.lastname}</h3>
                      <p>Email: {user.email}</p>
                      <p>Recipes Created: {user.recipeCount}</p>
                    </div>
                  ))}
                </div>
              ) : (
                <p>No users found.</p>
              )}
            </div>
  
            {/* Recipes Section */}
            <div className="recipes-section">
              <h2>Recipes</h2>
              {recipeResults.length > 0 ? (
                <div className="recipes-list-search">
                  {recipeResults.map((recipe) => (
                    <div key={recipe.recipeId} className="recipe-card-search" onClick={() => navigate(`/recipes/${recipe.recipeId}`)}>
                      <div className="recipe-details">
                        <h3>{recipe.title}</h3>
                        <p>{recipe.description}</p>
                        <p>
                          By:{" "}
                          <span
                            className="clickable-user"
                            onClick={(e) => {
                              e.stopPropagation();
                              navigate(`/users/${recipe.user.userId}`);
                            }}
                          >
                            {recipe.user.firstname} {recipe.user.lastname}
                          </span>
                        </p>
                        <p>⭐ Average Rating: {typeof recipe.averageRating === "number" ? recipe.averageRating.toFixed(1) : "No ratings yet"}</p>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <p>No recipes found.</p>
              )}
            </div>
          </div>
  
          {/* No results found */}
          {userResults.length === 0 && recipeResults.length === 0 && (
            <p>No users or recipes found.</p>
          )}
        </>
      )}
    </div>
  );
}

export default Searched;
