import api from "./api";

export const registerUser = async (firstname, lastname, email, password) => {
  try {
    const response = await api.post("/auth/register", {
      firstname,
      lastname,
      email,
      password,
    });
    return response.data;
  } catch (error) {
    throw error.response?.data?.message || "Registration failed";
  }
};
