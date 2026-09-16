import { useContext, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "./Register.scss";
import Alert from "../../components/Alert/Alert";

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

    if (field === "name") {
      if (name.trim() === "") {
        newErrors.name = "Name is required";
      } else if (!/^[a-zA-Z ]{3,}$/.test(name)) {
        newErrors.name = "Enter a valid name";
      } else {
        delete newErrors.name;
      }
    }
    if (field === "phone") {
      if (phone.trim() === "") {
        newErrors.phone = "Phone Number is required";
      } else if (!/^[6-9]\d{9}$/.test(phone)) {
        newErrors.phone = "Enter a valid Phone Number";
      } else {
        delete newErrors.phone;
      }
    }
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
      } else if (
        !/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()+{}])[A-Za-z0-9!@#$%^&*()+{}]{8,}$/.test(
          password,
        )
      ) {
        newErrors.password = "Enter a strong password";
      } else {
        delete newErrors.password;
      }
    }
    if (field === "confirmPassword") {
      if (password != confirmPassword) {
        newErrors.confirmPassword = "Password does not match";
      } else {
        delete newErrors.confirmPassword;
      }
    }

    setErrors(newErrors);
  };
  const userDetails = {
    name: name,
    email: email,
    phone: phone,
    address: address,
    password: password,
    role:"customer"
  };

  const handleRegister = async (e) => {
    e.preventDefault();

    try {
      const response = await orderService.createCustomer(userDetails);

      login(response.data);
      showAlert("Registration successfull","Success");
      setTimeout((prev)=>{
        setAlert((prev) => ({ ...prev, isOpen: false })); 
        navigate("/profile"); 
      },2000)
    } catch (error) {
      console.log(error.response?.data?.message);
      showAlert(error.response?.data?.message,"Error");
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
              onBlur={() => validateField("name")}
              required
            />
            {errors.name ? <p>{errors.name}</p> : ""}
          </div>

          <div className="form-group">
            <label htmlFor="phone">Phone Number</label>

            <input
              id="phone"
              type="tel"
              maxLength="10"
              placeholder="Enter your Phone Number"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              onBlur={() => {
                validateField("phone");
              }}
              required
            />
            {errors.phone ? <p>{errors.phone}</p> : ""}
          </div>

          <div className="form-group">
            <label htmlFor="address">Address</label>

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
              onBlur={() => {
                validateField("email");
              }}
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
              onBlur={() => {
                validateField("password");
              }}
              required
            />
            {errors.password ? <p>{errors.password}</p> : ""}
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword">Confirm Password</label>

            <input
              id="confirmPassword"
              type="password"
              placeholder="Confirm your password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              onBlur={() => {
                validateField("confirmPassword");
              }}
              required
            />
            {errors.confirmPassword ? <p>{errors.confirmPassword}</p> : ""}
          </div>

          <button
            type="submit"
            className="register-button"
            disabled={
              Object.keys(errors).length > 0 ||
              name.trim() === "" ||
              phone.trim() === "" ||
              address.trim() === "" ||
              email.trim() === "" ||
              password.trim() === "" ||
              confirmPassword.trim() === ""
            }
          >
            Register
          </button>
          <Alert
            isOpen={alert.isOpen}
            message={alert.message}
            onClose={handleCloseAlert}
            type={alert.type}
          />
        </form>

        <p className="login-text">
          Already have an account? <Link to="/login">Login</Link>
        </p>
      </div>
    </div>
  );
};

export default Register;
