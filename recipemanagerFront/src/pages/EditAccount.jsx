import React, { useEffect, useState } from "react";
import api from "../services/api";
import { useNavigate } from "react-router-dom";
import "../styles/EditAccount.css";

function EditAccount() {
    const [user, setUser] = useState({
        firstname: "",
        lastname: "",
        password: "",
      });
    const navigate = useNavigate();
    const token = localStorage.getItem("token");
    const userId = localStorage.getItem("userId");

    useEffect(() => {
        const fetchUser = async () => {
          try {
            const response = await api.get(`/users/${userId}`, {
              headers: { Authorization: `Bearer ${token}` },
            });
    
            setUser({
              firstname: response.data.firstname,
              lastname: response.data.lastname,
              password: "",
            });
          } catch (error) {
            console.error("Failed to fetch user details:", error);
          }
        };
    
        fetchUser();
      }, [userId, token]);

      const handleChange = (e) => {
        setUser({ ...user, [e.target.name]: e.target.value });
      };
    
      const handleSubmit = async (e) => {
        e.preventDefault();
    
        try {
          await api.put(`/users/${userId}`,
            {
              firstname: user.firstname,
              lastname: user.lastname,
              password: user.password || undefined,
            },
            { headers: { Authorization: `Bearer ${token}` } }
          );
          navigate("/profile");
        } catch (error) {
          console.error("Failed to update account:", error);
          alert("Error updating account. Please try again.");
        }
      };


    return (
        <div className="main-content">
            <h1>Edit Account Details</h1>
            <div className="edit-account-container">
                <form onSubmit={handleSubmit}>
                <label>First Name:</label>
                    <input
                    type="text"
                    name="firstname"
                    value={user.firstname}
                    onChange={handleChange}
                    required
                    />

                    <label>Last Name:</label>
                    <input
                    type="text"
                    name="lastname"
                    value={user.lastname}
                    onChange={handleChange}
                    required
                    />

                    <label>New Password (optional):</label>
                    <input
                    type="password"
                    name="password"
                    value={user.password}
                    onChange={handleChange}
                    placeholder="Leave blank to keep the same password"
                    />

                    <button className="edit-save-button" type="submit">Save Changes</button>
                    <button className="edit-cancel-button" onClick={() => navigate("/profile")}>
                        Cancel
                    </button>
                </form>
                
            </div>    
        </div>
      );
}

export default EditAccount;