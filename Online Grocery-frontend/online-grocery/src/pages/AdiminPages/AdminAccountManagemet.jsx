import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import "./AdminAccountManagement.scss";
import { orderService } from "../../api/orderService";
import BackButton from "../../components/Buttons/BackButton";
import Alert from "../../components/Alert/Alert";

const AdminAccountManagement = () => {
  const [admins, setAdmins] = useState([]);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const [alert, setAlert] = useState({ isOpen: false, message: "" });

  const showAlert = (message, type) => {
    setAlert({ isOpen: true, message, type });
  };

  const handleCloseAlert = () => {
    setAlert({ isOpen: false, message: "" });
  };

  const initialFormState = {
    id: "",
    name: "",
    phone: "",
    email: "",
    password: "",
    address: "",
    role: "admin",
  };

  const [formData, setFormData] = useState(initialFormState);
  const [isEditing, setIsEditing] = useState(false);

  const validateField = (field) => {
    const newErrors = { ...errors };

    if (field === "name") {
      if (formData.name.trim() === "") {
        newErrors.name = "Name is required";
      } else if (!/^[a-zA-Z ]{3,}$/.test(formData.name)) {
        newErrors.name = "Enter a valid name";
      } else {
        delete newErrors.name;
      }
    }
    if (field === "phone") {
      if (formData.phone.trim() === "") {
        newErrors.phone = "Phone Number is required";
      } else if (!/^[6-9]\d{9}$/.test(formData.phone)) {
        newErrors.phone = "Enter a valid Phone Number";
      } else {
        delete newErrors.phone;
      }
    }
    if (field === "email") {
      if (formData.email.trim() === "") {
        newErrors.email = "Email is required";
      } else if (!/^[a-z]{3,}\d*@gmail\.com$/.test(formData.email)) {
        newErrors.email = "Enter a valid email id";
      } else {
        delete newErrors.email;
      }
    }

    if (field === "password") {
      if (formData.password.trim() === "") {
        newErrors.password = "Password is required";
      } else if (
        !/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()+{}])[A-Za-z0-9!@#$%^&*()+{}]{8,}$/.test(
          formData.password,
        )
      ) {
        newErrors.password = "Enter a strong password";
      } else {
        delete newErrors.password;
      }
    }

    setErrors(newErrors);
  };

  useEffect(() => {
    fetchAdmins();
  }, []);

  const fetchAdmins = async () => {
    setLoading(true);
    try {
      const response = await orderService.getAllCustomers();
      if (Array.isArray(response?.data)) {
        const adminUsers = response.data.filter(
          (user) => user.role === "admin",
        );
        setAdmins(adminUsers);
      }
    } catch (err) {
      showAlert(err.response?.data?.message, "Error");
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleClear = () => {
    setFormData(initialFormState);
    setIsEditing(false);
    setErrors({});
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (isEditing) {
        await orderService.updateCustomerProfile(formData);
        setAdmins((prev) =>
          prev.map((admin) =>
            admin.id === formData.id ? { ...formData } : admin,
          ),
        );
        showAlert("Admin updated Successfully", "Success");
      } else {
        const response = await orderService.createCustomer(formData);
        const newAdmin = response?.data;
        if (newAdmin && newAdmin.role === "admin") {
          setAdmins((prev) => [...prev, newAdmin]);
        }
        showAlert("Admin Added Successfully", "Success");
      }
      handleClear();
    } catch (err) {
      showAlert(err.response?.data?.message, "Error");
    }
  };

  const handleEditClick = (adminUser) => {
    setIsEditing(true);
    setFormData({
      id: adminUser.id || "",
      name: adminUser.name || "",
      phone: adminUser.phone || "",
      email: adminUser.email || "",
      password: adminUser.password || "",
      address: adminUser.address || "",
      role: adminUser.role || "admin",
    });
  };

  const handleDeleteClick = async (id) => {
    if (!window.confirm("Are you sure you want to delete this admin account?"))
      return;

    try {
      await orderService.deleteCustomer(id);
      setAdmins((prev) => prev.filter((admin) => admin.id !== id));

      if (isEditing && formData.id === id) {
        handleClear();
      }
      showAlert("Admin deleted Successfully", "Success");
    } catch (err) {
      showAlert(err.response?.data?.message, "Error");
    }
  };

  return (
    <div className="admin-manager">
      <Link to="/admin">
        <BackButton />
      </Link>

      <div className="form-container">
        <h3>{isEditing ? "Update Admin Account" : "Add New Admin"}</h3>
        <form onSubmit={handleSubmit} className="admin-form">
          <input
            type="text"
            name="name"
            placeholder="Full Name"
            value={formData.name}
            onBlur={()=>validateField("name")}
            onChange={handleInputChange}
          />
          <input
            type="email"
            name="email"
            placeholder="Email Address"
            value={formData.email}
            onBlur={()=>validateField("email")}
            onChange={handleInputChange}
          />
          <input
            type="text"
            name="phone"
            placeholder="Phone Number"
            value={formData.phone}
            onBlur={()=>validateField("phone")}
            onChange={handleInputChange}
          />
          <input
            type="password"
            name="password"
            placeholder="Password"
            value={formData.password}
            onBlur={()=>validateField("password")}
            onChange={handleInputChange}
          />
          <input
            type="text"
            name="address"
            placeholder="Address"
            value={formData.address}
            onChange={handleInputChange}
          />
          <select
            name="role"
            value={formData.role}
            onChange={handleInputChange}
          >
            <option value="admin">Admin</option>
          </select>
        {errors.name ? <p>{errors.name}</p> : ""}
        {errors.email ? <p>{errors.email}</p> : ""}
        {errors.password ? <p>{errors.password}</p> : ""}
        {errors.phone ? <p>{errors.phone}</p> : ""}
          <div className="form-actions">
            <button
              type="submit"
              className={`btn-submit ${isEditing ? "edit-mode" : "add-mode"}`}
            >
              {isEditing ? "Update Admin" : "Add Admin"}
            </button>
            <button type="button" className="btn-clear" onClick={handleClear}>
              Clear
            </button>
            
          </div>
          
        </form>
      </div>

      <div className="table-container">
        <h3>Admin Accounts</h3>
        {loading ? (
          <p className="loading">Loading admin accounts...</p>
        ) : (
          <table className="admin-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Address</th>
                <th>Role</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {admins.length === 0 ? (
                <tr>
                  <td colSpan="6" className="no-data">
                    No Admin accounts found.
                  </td>
                </tr>
              ) : (
                admins.map((adminUser) => (
                  <tr key={adminUser.id}>
                    <td>{adminUser.name}</td>
                    <td>{adminUser.email}</td>
                    <td>{adminUser.phone || "N/A"}</td>
                    <td>{adminUser.address || "N/A"}</td>
                    <td>{adminUser.role}</td>
                    <td>
                      <button
                        className="btn-edit"
                        onClick={() => handleEditClick(adminUser)}
                      >
                        Edit
                      </button>
                      <button
                        className="btn-delete"
                        onClick={() => handleDeleteClick(adminUser.id)}
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        )}
        <Alert
          isOpen={alert.isOpen}
          message={alert.message}
          onClose={handleCloseAlert}
          type={alert.type}
        />
      </div>
    </div>
  );
};

export default AdminAccountManagement;
