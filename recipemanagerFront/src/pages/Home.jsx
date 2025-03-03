import React from "react";
import { useNavigate } from "react-router-dom";
import "../styles/Home.css";

function Home() {
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  return (
    <div className="home-container">
      <h1>Welcome to Recipe Manager</h1>

      {!token && (
        <div className="home-bubbles">
          <div className="bubble" onClick={() => navigate("/recipes")}>
            <h2>📖 Recipes</h2>
            <p>Browse through all recipes.</p>
          </div>
          
          <div className="bubble" onClick={() => navigate("/login")}>
            <h2>🔐 Log in / Sign up</h2>
            <p>Log in to manage your own recipes.</p>
          </div>

        </div>
      )}

      {token && (
        <div className="home-bubbles">
          <div className="bubble" onClick={() => navigate("/recipes")}>
            <h2>📖 Recipes</h2>
            <p>Browse through all recipes.</p>
          </div>

          <div className="bubble" onClick={() => navigate("/my-recipes")}>
            <h2>🍳 My Recipes</h2>
            <p>Add or modify your own recipes.</p>
          </div>

          <div className="bubble" onClick={() => navigate("/favorites")}>
            <h2>❤️ My Favorites</h2>
            <p>View and manage your favorite recipes.</p>
          </div>
        </div>
      )}
    </div>
  );
}

export default Home;

