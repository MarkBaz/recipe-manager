import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import "../styles/MyRecipes.css";

function MyRecipes() {
  const [recipes, setRecipes] = useState([]);
  const [categories, setCategories] = useState([]);
  const [newRecipe, setNewRecipe] = useState({
    title: "",
    description: "",
    ingredients: "",
    instructions: "",
    categoryId: "",
  });
  const [editingRecipe, setEditingRecipe] = useState(null);

  const userId = localStorage.getItem("userId");
  const token = localStorage.getItem("token");
  const navigate = useNavigate();

  useEffect(() => {
    if (userId) {
      fetchUserRecipes();
      fetchCategories();
    }
  }, [userId]);

  const fetchUserRecipes = async () => {
    try {
      const response = await api.get(`/recipes/user/${userId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      const recipesWithRatings = await Promise.all(response.data.map(async (recipe) => {
        try {
            const ratingResponse = await api.get(`/ratings/recipe/${recipe.recipeId}/average`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            return { ...recipe, averageRating: ratingResponse.data };
        } catch (error) {
            console.error(`Failed to fetch rating for recipe ${recipe.recipeId}`, error);
            return { ...recipe, averageRating: 0.0 }; // Default to 0
        }
    }));

      setRecipes(recipesWithRatings);
    } catch (error) {
      console.error("Failed to fetch recipes", error);
    }
  };

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

  const handleAddRecipe = async () => {
    try {
      if (!newRecipe.title || !newRecipe.description || !newRecipe.ingredients.trim() || !newRecipe.instructions.trim() || !newRecipe.categoryId) {
        alert("Please fill in all fields before adding a recipe.");
        return;
      }

      const user = {
        userId: localStorage.getItem("userId"),
        firstname: localStorage.getItem("firstname"),
        lastname: localStorage.getItem("lastname"),
        email: localStorage.getItem("email")
      };

      const recipePayload = {
        ...newRecipe,
        user,
        ingredients: newRecipe.ingredients
        .split("\n")
        .map(line => `${line.trim()}`)
        .filter(line => line !== "•")
        .join("\n"),
      };

      console.log("Recipe Payload:", recipePayload);

      await api.post("/recipes", recipePayload, {
        headers: { Authorization: `Bearer ${token}` },
      });
      fetchUserRecipes();
      setNewRecipe({ title: "", description: "", ingredients: "", instructions: "", categoryId: "" });
    } catch (error) {
      console.error("Failed to add recipe", error);
    }
  };

  const handleEditRecipe = async () => {
    try {
      await api.put(`/recipes/${editingRecipe.recipeId}`, editingRecipe, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setRecipes((prevRecipes) =>
        prevRecipes.map((recipe) =>
          recipe.recipeId === editingRecipe.recipeId ? editingRecipe : recipe

        )
      );
      setEditingRecipe(null);
    } catch (error) {
      console.error("Failed to update recipe", error);
    }
  };

  const handleDeleteRecipe = async (recipeId) => {
    try {
      await api.delete(`/recipes/${recipeId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      fetchUserRecipes();
    } catch (error) {
      console.error("Failed to delete recipe", error);
    }
  };

  return (
    <div className="main-content">
      <h1>My Recipes</h1>
      <ul className="recipe-list">
        {recipes.map((recipe) => (
          <li key={recipe.recipeId} className="recipe-card">
            
            {editingRecipe && editingRecipe.recipeId === recipe.recipeId ? (
              <div className="edit-section">
                <h3 className="edit-title">{editingRecipe.title}</h3>
                <input 
                  type="text" 
                  placeholder="Title"
                  value={editingRecipe.title} 
                  onChange={(e) => setEditingRecipe({ ...editingRecipe, title: e.target.value })} 
                />
                <textarea 
                  name="description"
                  placeholder="Description"
                  value={editingRecipe.description} 
                  onChange={(e) => setEditingRecipe({ ...editingRecipe, description: e.target.value })}
                />
                <textarea 
                  name="ingredients"
                  placeholder="Ingredients (one per line)"
                  value={editingRecipe.ingredients} 
                  onChange={(e) => setEditingRecipe({ ...editingRecipe, ingredients: e.target.value })}
                />
                <textarea 
                  name="instructions"
                  placeholder="Instructions (one per line)"
                  value={editingRecipe.instructions} 
                  onChange={(e) => setEditingRecipe({ ...editingRecipe, instructions: e.target.value })}
                />
                <button className="save-btn" onClick={handleEditRecipe}>Save</button>
                <button className="cancel-btn" onClick={() => setEditingRecipe(null)}>Cancel</button>
              </div>
            ) : (
              <div className="recipe-info" onClick={() => navigate(`/recipes/${recipe.recipeId}`)}>
                <h3>{recipe.title}</h3>
                <p>{recipe.description}</p>
                <p>⭐ Average Rating: {typeof recipe.averageRating === "number" ? recipe.averageRating.toFixed(1) : "No ratings yet"}</p>
              </div>
            )}

            {!editingRecipe || editingRecipe.recipeId !== recipe.recipeId ? (
              <div className="action-buttons">
                <button className="edit-btn" onClick={(e) => { e.stopPropagation(); setEditingRecipe(recipe); }}>Edit</button>
                <button className="delete-btn" onClick={(e) => { e.stopPropagation(); handleDeleteRecipe(recipe.recipeId); }}>Delete</button>
              </div>
            ) : null}

          </li>
        ))}
      </ul>

      <h2>Add a Recipe</h2>
      <div className="add-recipe-form">
        <input type="text" 
          placeholder="Title" 
          value={newRecipe.title} 
          onChange={(e) => setNewRecipe({ ...newRecipe, title: e.target.value })} 
        />
        <textarea
          name="description"
          placeholder="Description"
          value={newRecipe.description}
          onChange={(e) => setNewRecipe({ ...newRecipe, description: e.target.value })}
        />
        <textarea 
          name="ingredients" 
          placeholder="Ingredients (one per line)" 
          value={newRecipe.ingredients} 
          onChange={(e) => setNewRecipe({ ...newRecipe, ingredients: e.target.value })} 
        />
        <textarea
          name="instructions"
          placeholder="Instructions (one per line)"
          value={newRecipe.instructions}
          onChange={(e) => setNewRecipe({ ...newRecipe, instructions: e.target.value })}
        />
        <div className="category-container">
          <select
            className="category-dropdown"
            value={newRecipe.categoryId}
            onChange={(e) => setNewRecipe({ ...newRecipe, categoryId: e.target.value })}
          >
            <option value="">Select Category</option>
            {categories.map((cat) => (
              <option key={cat.categoryId} value={cat.categoryId}>
                {cat.name}
              </option>
            ))}
          </select>
        </div>

        <button className="add-btn" onClick={handleAddRecipe}>Add Recipe</button>
      </div>
    </div>
  );
}

export default MyRecipes;
