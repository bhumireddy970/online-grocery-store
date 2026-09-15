import React, { useContext, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { AuthContext } from "../../context/AuthContext";
import "./Login.scss";
import { orderService } from "../../api/orderService";
import Alert from "../../components/Alert/Alert";

const Login = () => {
  const { login } = useContext(AuthContext);

  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errors, setErrors] = useState({});
  const [alert, setAlert] = useState({ isOpen: false, message: "" });

  const showAlert = (message, type) => {
    setAlert({ isOpen: true, message, type });
  };

  const handleCloseAlert = () => {
    setAlert({ isOpen: false, message: "" });
  };

  const validateField = (field) => {
    const newErrors = { ...errors };

    if (field === "email") {
      if (email.trim() === "") {
        newErrors.email = "Email is required";
      } else if (!/^[a-z]{3,}\d*@gmail\.com$/.test(email)) {
        newErrors.email = "Enter a valid email id";
      } else {
        delete newErrors.email;
      }
    }

    if (field === "password") {
      if (password.trim() === "") {
        newErrors.password = "Password is required";
      } else {
        delete newErrors.password;
      }
    }

    setErrors(newErrors);
  };

  const handleLogin = async (e) => {
    e.preventDefault();

    const loginData = {
      email,
      password,
    };

    try {
      const response = await orderService.loginCustomer(loginData);

      login(response.data);
      console.log(response?.data?.role);
      showAlert("Login Successfull", "Success");
      setTimeout(() => {
        setAlert((prev) => ({ ...prev, isOpen: false })); 
        if(response?.data?.role === "admin")
          navigate("/admin");
        else 
          navigate("/"); 
      }, 1000);
    } catch (error) {
      console.error("Login failed:", error?.response?.data?.message);
      showAlert(error?.response?.data?.message, "Error");
    }
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <h1>Welcome Back</h1>

        <p className="login-subtitle">Login to your account</p>

        <form onSubmit={handleLogin}>
          <div className="form-group">
            <label htmlFor="email">Email</label>

            <input
              id="email"
              type="email"
              placeholder="Enter your email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              onBlur={() => validateField("email")}
              required
            />
            {errors.email ? <p>{errors.email}</p> : ""}
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>

            <input
              id="password"
              type="password"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              onBlur={() => validateField("password")}
              required
            />
            {errors.password ? <p>{errors.password}</p> : ""}
          </div>

          <button
            type="submit"
            className="login-submit"
            disabled={Object.keys(errors).length > 0}
          >
            Login
          </button>
          <Alert
            isOpen={alert.isOpen}
            message={alert.message}
            onClose={handleCloseAlert}
            type={alert.type}
          />
        </form>

        <p className="register-text">
          Don't have an account? <Link to="/register">Register</Link>
        </p>
      </div>
    </div>
  );
};

export default Login;
