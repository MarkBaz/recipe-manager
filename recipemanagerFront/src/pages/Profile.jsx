import React, { useEffect, useState } from "react";
import api from "../services/api";
import { useNavigate } from "react-router-dom";
import "../styles/Profile.css"; // Import the CSS

function Profile() {
  const [user, setUser] = useState(null);
  const navigate = useNavigate();
  const [recipeCount, setRecipeCount] = useState(0);

  const token = localStorage.getItem("token");
  const userId = localStorage.getItem("userId");

  useEffect(() => {
    const fetchUser = async () => {
      try {
        if (!token || !userId) {
          console.error("No token found or User not logged in, redirecting to login...");
          navigate("/login");
          return;
        }

        const response = await api.get(`/users/${userId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });

        setUser(response.data);
      } catch (error) {
        console.error("Failed to fetch user:", error);

        if (error.response && error.response.status === 401) {
          console.error("Unauthorized request. Redirecting to signup...");
          localStorage.clear();
          navigate("/signup");
        }
      }
    };

    const fetchRecipeCount = async () => {
      try {
        const response = await api.get(`/recipes/user/${userId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setRecipeCount(response.data.length);
      } catch (error) {
        console.error("Failed to fetch recipe count:", error);
      }
    };

    fetchUser();
    fetchRecipeCount();
  }, [navigate]);

  return (
    <div className="main-content">
      <h1>My Profile</h1>
      {user ? (
        <div className="profile-info">
          <p><strong>Name:</strong> {user.firstname} {user.lastname}</p>
          <p><strong>Email:</strong> {user.email}</p>
          <p><strong>Recipes Created:</strong> {recipeCount}</p>

          {/* New Bubble Buttons */}
          <div className="profile-bubbles">
            <div className="bubble" onClick={() => navigate("/my-recipes")}>
              <h2>📖 My Recipes</h2>
              <p>View and manage your recipes.</p>
            </div>

            <div className="bubble" onClick={() => navigate("/favorites")}>
              <h2>❤️ Favorites</h2>
              <p>View your favorite recipes.</p>
            </div>
          </div>
        </div>
      ) : (
        <p>Loading user data...</p>
      )}
    </div>
  );
}

export default Profile;
