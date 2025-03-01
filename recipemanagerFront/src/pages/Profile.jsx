import React, { useEffect, useState } from "react";
import api from "../services/api";
import { useNavigate } from "react-router-dom";
import "../styles/Profile.css";

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

  const handleDeleteAccount = async () => {
    const confirmDelete = window.confirm("Are you sure you want to delete your account? This action cannot be undone.");
    
    if (confirmDelete) {
      try {
        await api.delete(`/users/${userId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        localStorage.clear();
        alert("Your account has been deleted.");
        navigate("/signup");
      } catch (error) {
        console.error("Failed to delete account:", error);
        alert("Error deleting account. Please try again.");
      }
    }
  };


  return (
    <div className="main-content">
      <h1>My Profile</h1>
      {user ? (
        <div className="profile-info">
          <p><strong>Name:</strong> {user.firstname} {user.lastname}</p>
          <p><strong>Email:</strong> {user.email}</p>
          <p><strong>Recipes Created:</strong> {recipeCount}</p>
          <button className="edit-account-btn" onClick={() => navigate("/edit-account")}>
            Edit Account Details
          </button>

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

          <button className="delete-account-btn" onClick={handleDeleteAccount}>
            ❌ Delete My Account
          </button>

        </div>
      ) : (
        <p>Loading user data...</p>
      )}
    </div>
  );
}

export default Profile;
