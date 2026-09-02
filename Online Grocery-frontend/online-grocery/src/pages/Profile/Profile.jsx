import { useContext, useState } from "react";
import { User, Mail, Phone, MapPin, Edit, Save, X } from "lucide-react";
import { orderService } from "../../api/orderService";
import { AuthContext } from "../../context/AuthContext";
import "./Profile.scss";

const Profile = () => {
  const { user, login } = useContext(AuthContext);

  const [isEditing, setIsEditing] = useState(false);

  const [formData, setFormData] = useState({
    name: "",
    email: "",
    phone: "",
    address: "",
  });

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
      const response = await orderService.updateCustomerProfile(
        user.id,
        formData,
      );

      login(response.data);
      setIsEditing(false);

      alert("Profile updated successfully");
    } catch (error) {
      console.error("Update failed:", error);
      alert("Failed to update profile");
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
            <label htmlFor="name">"Name"</label>

            {isEditing ? (
              <input
                type="text"
                name="name"
                value={formData["name"]}
                onChange={handleChange}
              />
            ) : (
              <span>{user["name"] || "Not provided"}</span>
            )}
          </div>
        </div>

        <div className="profile-field" key="email">
          <div className="field-icon">
            <Mail size={20} />
          </div>

          <div className="field-content">
            <label htmlFor="email">"Email"</label>

            {isEditing ? (
              <input
                type="email"
                name="email"
                value={formData["email"]}
                onChange={handleChange}
              />
            ) : (
              <span>{user["email"] || "Not provided"}</span>
            )}
          </div>
        </div>

        <div className="profile-field" key="phone">
          <div className="field-icon">
            <Phone size={20} />
          </div>

          <div className="field-content">
            <label htmlFor="phone">"Phone"</label>

            {isEditing ? (
              <input
                type="tel"
                name="phone"
                value={formData["phone"]}
                onChange={handleChange}
                maxLength="10"
              />
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
            <label htmlFor="email">"Address"</label>

            {isEditing ? (
              <textarea
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
      </div>
    </div>
  );
};

export default Profile;
