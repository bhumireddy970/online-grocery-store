import axios from "axios";

export const BASE_URL = axios.create({
  baseURL: "http://localhost:8080/api",
  timeout: 5000,
});

BASE_URL.interceptors.request.use(
  (config) => {
    const savedUser = localStorage.getItem("store_user");

    if (savedUser) {
      try {
        const user = JSON.parse(savedUser);

        if (user?.token) {
          config.headers.Authorization = `Bearer ${user.token}`;
        }
      } catch (error) {
        console.error("Unable to read saved user:", error);
      }
    }

    return config;
  },
  (error) => Promise.reject(error)
);
