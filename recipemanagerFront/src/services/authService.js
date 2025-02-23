import api from "./api";

export const login = async (email, password) => {
  try {
    const response = await api.post("/auth/login", { email, password });

    if (response.data.token && response.data.userId) {
      localStorage.setItem("token", response.data.token);
      localStorage.setItem("userId", response.data.userId);
    } else {
      console.error("Login response missing token or userId:", response.data);
    }
    return response.data;
  } catch (error) {
    throw error.response?.data?.message || "Login failed";
  }
};

export const logout = () => {
  localStorage.removeItem("token");
};

export const isAuthenticated = () => {
  return !!localStorage.getItem("token");
};
