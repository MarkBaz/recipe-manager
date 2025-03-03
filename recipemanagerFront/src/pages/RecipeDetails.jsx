import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import api from "../services/api";
import "../styles/RecipeDetails.css";

function RecipeDetails() {
  const { recipeId } = useParams();
  const [recipe, setRecipe] = useState(null);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState("");
  const [averageRating, setAverageRating] = useState(null);
  const [userRating, setUserRating] = useState(0);
  const [isFavorite, setIsFavorite] = useState(false);
  const navigate = useNavigate();

  const token = localStorage.getItem("token");
  const userId = localStorage.getItem("userId");
  const firstname = localStorage.getItem("firstname");
  const lastname = localStorage.getItem("lastname");
  const email = localStorage.getItem("email");

  useEffect(() => {
    fetchRecipeDetails();
    fetchComments();
    fetchAverageRating();
    fetchUserRating();
    checkIfFavorite();
  }, [recipeId]);

  const fetchRecipeDetails = async () => {
    try {
      const response = await api.get(`/recipes/${recipeId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setRecipe(response.data);
    } catch (error) {
      console.error("Failed to fetch recipe details", error);
    }
  };

  const fetchComments = async () => {
    try {
      const response = await api.get(`/comments/recipe/${recipeId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setComments(response.data);
    } catch (error) {
      console.error("Failed to fetch comments", error);
    }
  };

  const fetchUserRating = async () => {
    try {
        if (!token || !userId) {
            console.error("No auth token found, user might not be logged in.");
            return;
        }

        const response = await api.get(`/ratings/user/${userId}/recipe/${recipeId}`, {
            headers: { Authorization: `Bearer ${token}` },
        });

        if (response.data) {
            setUserRating(response.data.stars);
        }
    } catch (error) {
        console.error("Failed to fetch user rating", error);
    }
};

  const fetchAverageRating = async () => {
    try {     

        const response = await api.get(`/ratings/recipe/${recipeId}/average`, {
            headers: { Authorization: `Bearer ${token}` },
        });
        setAverageRating(response.data);
    } catch (error) {
        console.error("Failed to fetch average rating", error);
    }
  };

  const checkIfFavorite = async () => {
    try {
        const response = await api.get(`/favorites/user/${userId}`, {
            headers: { Authorization: `Bearer ${token}` },
        });
        const isFav = response.data.some(fav => fav.recipeId === parseInt(recipeId));
        setIsFavorite(isFav);
    } catch (error) {
        console.error("Failed to check favorite status", error);
    }
};

  const handleAddComment = async () => {
    try {
        if (!token || !userId) {
            console.error("User not logged in.");
            return;
        }

        const userDTO = { userId, firstname, lastname, email };

        await api.post("/comments", {
            content: newComment,
            recipeId,
            user: userDTO
        }, {
            headers: { Authorization: `Bearer ${token}` },
        });
        setNewComment("");
        fetchComments();
    } catch (error) {
        console.error("Failed to add comment:", error.response ? error.response.data : error.message);
    }
  };

  const handleDeleteComment = async (commentId) => {
    try {
      await api.delete(`/comments/${commentId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
  
      // remove deleted comment from state
      setComments((prevComments) =>
        prevComments.filter((comment) => comment.commentId !== commentId)
      );
    } catch (error) {
      console.error("Failed to delete comment:", error);
    }
  };
  

  const handleRateRecipe = async (stars) => {
    try {
      await api.post(
        "/ratings",
        { stars, userId, recipeId },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setUserRating(stars);
      fetchAverageRating();
    } catch (error) {
      console.error("Failed to rate recipe", error);
    }
  };

  const toggleFavorite = async () => {
    try {
      if (isFavorite) {
        await api.delete(`/favorites/user/${userId}/recipe/${recipeId}`, {
            headers: { Authorization: `Bearer ${token}` },
        });
        setIsFavorite(false);
    } else {
        await api.post("/favorites", { userId, recipeId }, {
            headers: { Authorization: `Bearer ${token}` },
        });
    }
        setIsFavorite(!isFavorite);
    } catch (error) {
        console.error("Failed to toggle favorite", error);
    }
};

  return (
    <div className="main-recipe-content">
      {recipe ? (
        <>
          {/* Recipe Title with Clickable Creator Name */}
          <div className="recipe-header">
            <h1>{recipe.title}</h1>
            {recipe.user && (
              <span className="recipe-creator">
                (by{" "}
                <span 
                  className="clickable-user"
                  onClick={() => navigate(`/users/${recipe.user.userId}`)}
                >
                  {recipe.user.firstname} {recipe.user.lastname}
                </span>)
              </span>
            )}
          </div>
          <p><strong>Description:</strong> {recipe.description}</p>
          <p><strong>Ingredients:</strong> </p>
            <ul>
              {recipe.ingredients.split("\n").map((ingredient, index) => (
                <li key={index}>{ingredient}</li>
              ))}
            </ul>
            <p><strong>Instructions:</strong></p>
              <ol className="instructions-list">
                {recipe.instructions.split("\n").map((step, index) => (
                  <li key={index}> {step}</li>
                ))}
              </ol>
          {token && (
          <div className="rating-favorite-container">
              <p>Rate this Recipe</p>

              <button className={`favorite-btn ${isFavorite ? "favorited" : ""}`} onClick={toggleFavorite}>
                  ❤️ {isFavorite ? "Favorite" : "Favorite"}
              </button>
          </div>
          )}

          <div className="rating-container">
            {token && (
              [1, 2, 3, 4, 5].map((star) => (
                  <button
                      key={star}
                      onClick={() => handleRateRecipe(star)}
                      className={star <= userRating ? "selected" : ""}
                  >
                      {star} ⭐
                  </button>
              ))
            )}
              <span className="average-rating-text">
                  Average Rating: {averageRating ? averageRating.toFixed(1) : "No ratings yet"}
              </span>
          </div>

          
          <p><strong>Comments</strong></p>
          <div className="comments-section">
              {comments.map((comment) => (
                  <div key={comment.commentId} className="comment-bubble">
                      <p><strong>{comment.user ? `${comment.user.firstname} ${comment.user.lastname}` : "Unknown User"}:</strong></p>
                      <p>{comment.content}</p>

                      {/* Show delete button only if the comment belongs to the logged-in user */}
                      {comment.user && comment.user.userId.toString() === userId && (
                        <button
                          className="delete-comment-btn"
                          onClick={() => handleDeleteComment(comment.commentId)}
                        >
                          🗑️ Delete
                        </button>
                      )}
                  </div>
              ))}
          </div>

          <div className="add-comment-form">
              <textarea
                  className="comment-input"
                  placeholder="Add a comment..."
                  value={newComment}
                  onChange={(e) => setNewComment(e.target.value)}
              />
              <button className="submit-btn" onClick={handleAddComment}>Submit Comment</button>
          </div>
        </>
      ) : (
        <p>Loading recipe details...</p>
      )}
    </div>
  );
}

export default RecipeDetails;