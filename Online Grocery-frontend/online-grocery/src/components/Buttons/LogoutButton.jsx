import "./BackButton.scss";
import { useContext } from "react";
import { AuthContext } from "../../context/AuthContext";

const LogoutButton = () => {
  const { logout } = useContext(AuthContext);
  return (
    <button type="button" className="logout-button" onClick={logout}>
      Logout
    </button>
  );
};

export default LogoutButton;
