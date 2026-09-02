import { Link } from "react-router-dom";
import "./AdminDashBoard.scss";

const AdminDashBoard = () => {
  return (
    <div className="admin-container">
      <h1 className="admin-title">AdminDashBoard</h1>
      <div className="admin-cards">
        <Link to="/admin/productmanagement">
          <div className="admin-link">Product Management</div>
        </Link>
        <Link to="/admin/catogorymanagement">
          <div className="admin-link">Category Management</div>
        </Link>
      </div>
    </div>
  );
};

export default AdminDashBoard;
