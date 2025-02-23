import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { registerUser } from "../services/userService";
import "../styles/Signup.css"

function Signup() {
  const [firstname, setFirstname] = useState("");
  const [lastname, setLastname] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await registerUser(firstname, lastname, email, password);
      navigate("/login"); // Redirect to login after successful signup
    } catch (err) {
      setError(err);
    }
  };

  return (
    <div className="main-content">
      <h1 className="signup-header">
        Sign Up{" "}
        <span className="yes-account">
          (Already have an account?{" "}
          <span 
            className="clickable-login"
            onClick={() => navigate(`/login`)}
          >
            Login!
          </span>)
        </span>
      </h1>
      {error && <p className="error-text">{error}</p>}
      <form onSubmit={handleSubmit} className="auth-form">
        <input
          type="text"
          className="auth-input"
          placeholder="First Name"
          value={firstname}
          onChange={(e) => setFirstname(e.target.value)}
          required
        />
        <input
          type="text"
          className="auth-input"
          placeholder="Last Name"
          value={lastname}
          onChange={(e) => setLastname(e.target.value)}
          required
        />
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
        <button type="submit" className="auth-button">Sign Up</button>
      </form>
    </div>
);
}

export default Signup;
