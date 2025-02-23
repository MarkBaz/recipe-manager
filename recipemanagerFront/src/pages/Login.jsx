import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../services/authService";
import "../styles/Login.css"

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await login(email, password);

      // Store token and user details
      localStorage.setItem("token", response.token);
      localStorage.setItem("userId", response.userId);
      localStorage.setItem("firstname", response.firstname);
      localStorage.setItem("lastname", response.lastname);
      localStorage.setItem("email", response.email);

      navigate("/"); // Redirect to Home after login
    } catch (err) {
      setError(err);
    }
  };

  return (
    <div className="main-content">
      <h1 className="login-header">
        Login{" "}
        <span className="no-account">
          (Don't have an account?{" "}
          <span 
            className="clickable-signup"
            onClick={() => navigate(`/signup`)}
          >
            Sign up!
          </span>)
        </span>
      </h1>
      {error && <p className="error-text">{error}</p>}
      <form onSubmit={handleSubmit} className="auth-form">
        <input
          type="email" 
          className="auth-input" 
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password" 
          className="auth-input" 
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit" className="auth-button">Login</button>
      </form>
    </div>
);
}

export default Login;

