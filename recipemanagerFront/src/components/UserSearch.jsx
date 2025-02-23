import React, { useState, useEffect, useRef } from "react";
import api from "../services/api";
import { useNavigate } from "react-router-dom";
import "../styles/Search.css"

function UserSearch() {
  const [searchQuery, setSearchQuery] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const searchRef = useRef(null);
  const navigate = useNavigate();

  const token = localStorage.getItem("token");


  // Hide results when clicking outside
  useEffect(() => {
    function handleClickOutside(event) {
      if (searchRef.current && !searchRef.current.contains(event.target)) {
        setSearchResults([]);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  useEffect(() => {
    if (!searchQuery.trim()) {
      setSearchResults([]);
      return;
    }

    const fetchSearchResults = async () => {
      try {
        const [userRes, recipeRes] = await Promise.all([
          api.get(`/users/search?keyword=${searchQuery}`, {
            headers: { Authorization: `Bearer ${token}` },
          }),
          api.get(`/recipes/search?keyword=${searchQuery}`, {
            headers: { Authorization: `Bearer ${token}` },
          }),
        ]);

        // Format results with labels
        const formattedResults = [
            ...userRes.data.map((user) => ({
              id: user.userId,
              name: `${user.firstname} ${user.lastname} (user)`,
              type: "user",
            })),
            ...recipeRes.data.map((recipe) => ({
              id: recipe.recipeId,
              name: `${recipe.title} (recipe by ${recipe.user.firstname} ${recipe.user.lastname})`,
              type: "recipe",
            })),
          ];
  
          setSearchResults(formattedResults);
      } catch (error) {
        console.error("Error searching users and recipes:", error);
      }
    };

    fetchSearchResults();
  }, [searchQuery]);

  const handleResultClick = (result) => {
    if (result.type === "user") {
      navigate(`/users/${result.id}`);
    } else {
      navigate(`/recipes/${result.id}`);
    }
    setSearchQuery(""); // Clear search bar after navigation
    setSearchResults([]); // Hide dropdown
  };

  // Handle search button click (navigate to Searched.jsx)
  const handleSearch = () => {
    if (!searchQuery.trim()) return;
    navigate(`/search-results?query=${searchQuery}`);
    setSearchQuery("");
  };

  return (
    <div className="search-container" ref={searchRef}>
      <input
        type="text"
        placeholder="Search users or recipes..."
        value={searchQuery}
        onChange={(e) => setSearchQuery(e.target.value)}
      />
      <button onClick={handleSearch}>🔍 Search</button>

      {/* Search Results Dropdown */}
      {searchResults.length > 0 && (
        <ul className="search-results active">
          {searchResults.map((result) => (
            <li key={result.id} onClick={() => handleResultClick(result)}>
              {result.name}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default UserSearch;
