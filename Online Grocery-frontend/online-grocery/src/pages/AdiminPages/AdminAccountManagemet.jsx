import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import "./AdminAccountManagement.scss";
import { orderService } from "../../api/orderService";
import BackButton from "../../components/Buttons/BackButton";

const AdminAccountManagement = () => {
  const [admins, setAdmins] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const initialFormState = {
    id: "",
    name: "",
    phone: "",
    email: "",
    address: "",
    role: "admin",
  };

  const [formData, setFormData] = useState(initialFormState);
  const [isEditing, setIsEditing] = useState(false);

  useEffect(() => {
    fetchAdmins();
  }, []);

  const fetchAdmins = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await orderService.getAllCustomers();
      if (Array.isArray(response?.data)) {
        const adminUsers = response.data.filter(
          (user) => user.role === "admin",
        );
        setAdmins(adminUsers);
      }
    } catch (err) {
      setError(err?.response?.data?.message || err.message);
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
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.name || !formData.email) {
      alert("Name and Email are required fields.");
      return;
    }

    try {
      if (isEditing) {
        await orderService.updateCustomer(formData);
        setAdmins((prev) =>
          prev.map((admin) =>
            admin.id === formData.id ? { ...formData } : admin,
          ),
        );
      } else {
        const response = await orderService.addCustomer(formData);
        const newAdmin = response?.data;
        if (newAdmin && newAdmin.role === "admin") {
          setAdmins((prev) => [...prev, newAdmin]);
        }
      }
      handleClear();
    } catch (err) {
      alert(err?.response?.data?.message || err.message);
    }
  };

  const handleEditClick = (adminUser) => {
    setIsEditing(true);
    setFormData({
      id: adminUser.id || "",
      name: adminUser.name || "",
      phone: adminUser.phone || "",
      email: adminUser.email || "",
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
    } catch (err) {
      alert(err?.response?.data?.message || err.message);
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
            onChange={handleInputChange}
          />
          <input
            type="email"
            name="email"
            placeholder="Email Address"
            value={formData.email}
            onChange={handleInputChange}
          />
          <input
            type="text"
            name="phone"
            placeholder="Phone Number"
            value={formData.phone}
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
        ) : error ? (
          <p className="error-message">{error}</p>
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
      </div>
    </div>
  );
};

export default AdminAccountManagement;
