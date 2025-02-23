import React from "react";
import { NavLink, useNavigate } from "react-router-dom";
import "../styles/Navbar.css";
import UserSearch from "./UserSearch";

function Navbar() {
  const token = localStorage.getItem("token");
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.clear();
    navigate("/login");
  };

  return (
    <nav className="navbar">
      <NavLink  to="/" className="logo">🍽️ Recipe Manager</NavLink >

      <UserSearch />
      
      <div className="nav-links">
        {token ? (
          <>
            <NavLink to="/recipes" className={({ isActive }) => (isActive ? "active" : "")}>
              Recipes
            </NavLink>
            <NavLink to="/profile" className={({ isActive }) => (isActive ? "active" : "")}>
              My Profile
            </NavLink>
            <button onClick={handleLogout}>Logout</button>
          </>
        ) : (
          <>
            <NavLink to="/login" className={({ isActive }) => (isActive ? "active" : "")}>
              Login
            </NavLink>
            <NavLink to="/signup" className={({ isActive }) => (isActive ? "active" : "")}>
              Sign Up
            </NavLink>
          </>
        )}
      </div>
    </nav>
  );
}

export default Navbar;
