import { use, useContext, useState } from "react";
import { User, Phone, MapPin, Edit, Save, X } from "lucide-react";
import { orderService } from "../../api/orderService";
import { AuthContext } from "../../context/AuthContext";
import "./Profile.scss";
import Alert from "../../components/Alert/Alert";

const Profile = () => {
  const { user, login } = useContext(AuthContext);
  const [isEditing, setIsEditing] = useState(false);
  const [alert, setAlert] = useState({ isOpen: false, message: "" });
  const [formData, setFormData] = useState({
    name: "",
    phone: "",
    address: "",
  });
  const [errors, setErrors] = useState({});

  const showAlert = (message, type) => {
    setAlert({ isOpen: true, message, type });
  };

  const handleCloseAlert = () => {
    setAlert({ isOpen: false, message: "" });
  };

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

    setErrors(newErrors);
  };

  const handleEdit = () => {
    setFormData({ ...user });
    setIsEditing(true);
  };

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleCancel = () => {
    setIsEditing(false);
  };

  const handleUpdate = async () => {
    try {
      const response = await orderService.updateCustomerProfile(user.id,formData);

      login(response.data);
      setIsEditing(false);


      showAlert("Profile updated successfully","Success");
    } catch (error) {
      console.error("Update failed:", error?.response?.data);
      showAlert(error?.response?.data,"Error");
    }
  };

  if (!user) {
    return (
      <div className="profile-page">
        <div className="profile-card">
          <h2>Please login to view your profile</h2>
        </div>
      </div>
    );
  }

  return (
    <div className="profile-page">
      <div className="profile-card">
        <div className="profile-header">
          <div className="profile-avatar">
            <User size={45} />
          </div>

          <div>
            <h1>My Profile</h1>
            <p>Manage your personal information</p>
          </div>
        </div>

        <div className="profile-field" key="name">
          <div className="field-icon">
            <User size={20} />
          </div>

          <div className="field-content">
            <label htmlFor="name">Name</label>

            {isEditing ? (
              <>
                <input
                  id="name"
                  type="text"
                  name="name"
                  value={formData["name"]}
                  onChange={handleChange}
                  onBlur={() => validateField("name")}
                />
                {errors.name ? <p>{errors.name}</p> : ""}
              </>
            ) : (
              <span>{user["name"] || "Not provided"}</span>
            )}
          </div>
        </div>

        <div className="profile-field" key="phone">
          <div className="field-icon">
            <Phone size={20} />
          </div>

          <div className="field-content">
            <label htmlFor="phone">Phone</label>

            {isEditing ? (
              <>
                {" "}
                <input
                  id="phone"
                  type="tel"
                  name="phone"
                  value={formData["phone"]}
                  onChange={handleChange}
                  onBlur={() => validateField("phone")}
                />
                {errors.phone ? <p>{errors.phone}</p> : ""}
              </>
            ) : (
              <span>{user["phone"] || "Not provided"}</span>
            )}
          </div>
        </div>

        <div className="profile-field" key="address">
          <div className="field-icon">
            <MapPin size={20} />
          </div>

          <div className="field-content">
            <label htmlFor="address">Address</label>

            {isEditing ? (
              <textarea
                id="address"
                name="address"
                value={formData["address"]}
                onChange={handleChange}
                rows={3}
              />
            ) : (
              <span>{user["address"] || "Not provided"}</span>
            )}
          </div>
        </div>

        <div className="profile-actions">
          {!isEditing ? (
            <button type="button" className="edit-button" onClick={handleEdit}>
              <Edit size={18} />
              Edit Profile
            </button>
          ) : (
            <>
              <button
                type="button"
                className="save-button"
                onClick={handleUpdate}
                disabled={Object.keys(errors).length>0}
              >
                <Save size={18} />
                Save Changes
              </button>

              <button
                type="button"
                className="cancel-button"
                onClick={handleCancel}
              >
                <X size={18} />
                Cancel
              </button>
            </>
          )}
        </div>
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

export default Profile;
