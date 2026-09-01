import { useContext } from "react"
import { AuthContext } from "./context/AuthContext"
import { Navigate, Outlet } from "react-router-dom";

const AdminProtectedRoutes = ({allowedRoles}) => {
    const{user}=useContext(AuthContext);
    if(!user)
        return <Navigate to="/login" replace />;
    if (!allowedRoles.includes(user?.role)) 
    {
        alert("Unauthorised user")
        return <Navigate to="/login" replace />
    }
    
  return <Outlet />
}

export default AdminProtectedRoutes