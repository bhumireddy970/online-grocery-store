import React, { useContext, useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "./Register.scss";
import validator from "validator";

import { AuthContext } from "../../context/AuthContext";
import { orderService } from "../../api/orderService";

const Register = () => {
  const { login } = useContext(AuthContext);

  const navigate = useNavigate();

  const [name, setName] = useState("");
  const [phone, setPhone] = useState("");
  const [address, setAddress] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [isStrong, setIsStrong] = useState(false);

  useEffect(() => {
    setIsStrong(validator.isStrongPassword(password));
  }, [password]);

  const userDetails = {
    name: name,
    email: email,
    phone: phone,
    address: address,
    password: password,
  };

  const handleRegister = async (e) => {
    e.preventDefault();

    if (password !== confirmPassword) {
      alert("Passwords do not match");
      return;
    }

    try {
      const response = await orderService.createCustomer(userDetails);

      login(response.data);
      navigate("/profile");
    } catch (error) {
      console.log(error.response?.data?.message);
      alert(error.response?.data?.message);
    }
  };

  return (
    <div className="register-page">
      <div className="register-card">
        <h1>Create Account</h1>

        <p className="register-subtitle">Create your MyShop account</p>

        <form onSubmit={handleRegister}>
          <div className="form-group">
            <label htmlFor="name">Name</label>

            <input
              id="name"
              type="text"
              placeholder="Enter your name"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="name">Phone Number</label>

            <input
              id="phone"
              type="tel"
              maxLength="10"
              placeholder="Enter your Phone Number"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="name">Address</label>

            <textarea
              id="address"
              placeholder="Enter your Address"
              value={address}
              onChange={(e) => setAddress(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="email">Email</label>

            <input
              id="email"
              type="email"
              placeholder="Enter your email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>

            <input
              id="password"
              type="password"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword">Confirm Password</label>

            <input
              id="confirmPassword"
              type="password"
              placeholder="Confirm your password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              required
            />
          </div>
          <p>
            {" "}
            {password != ""
              ? isStrong
                ? "Password Strength:  Strong"
                : "Password Strength:  Too Weak"
              : ""}
          </p>

          <button type="submit" className="register-button">
            Register
          </button>
        </form>

        <p className="login-text">
          Already have an account? <Link to="/login">Login</Link>
        </p>
      </div>
    </div>
  );
};

export default Register;
