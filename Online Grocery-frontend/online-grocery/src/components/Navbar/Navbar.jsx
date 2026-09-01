import { Link } from "react-router-dom";
import { ShoppingCart, User } from "lucide-react";
import "./Navbar.scss";
import { AuthContext } from "../../context/AuthContext";
import { useContext } from "react";
import LogoutButton from "../Buttons/LogoutButton";
import { CartContext } from "../../context/CartContext";

const Navbar = () => {
   const { user } = useContext(AuthContext);
   const {cartItems} = useContext(CartContext)
  return (
    <nav className="navbar">
      <div className="navbar-logo">
        <Link to="/">MyShop</Link>
      </div>
    {
      user?.role=="admin"?
      (<div className="navbar-links">
        <Link to="/">Home</Link>
        <Link to="/admin">Admin DashBoard</Link>
        <Link to="/admin/inventorymanagement">Inventory Management</Link>
      </div>)
      :(<div className="navbar-links">
        <Link to="/">Home</Link>
        <Link to="/products">Products</Link>
      </div>)
    }
      

      <div className="navbar-actions">
        <Link to="/cart" className="cart-link">
          <ShoppingCart size={22} />
          <span className="cart-items">{cartItems.length}</span>
        </Link>

        {user ? (
          <Link to="/profile" className="profile-link">
            <User size={22} />
            <LogoutButton />
          </Link>
        ) : (
          <Link to="/login" className="login-button">
            Login
          </Link>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
