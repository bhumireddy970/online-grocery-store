
import { Routes, Route } from "react-router-dom";
import Home from "../pages/Home/Home";
import Products from "../pages/Products/Products";
import Cart from "../pages/Cart/Cart";
import Login from "../pages/Login/Login";
import Profile from "../pages/Profile/Profile";
import Register from "../pages/Register/Register";
import Category from "../pages/Category/Category";
import AdminProtectedRoutes from "./AdminProtectedRoutes";
import AdminDashBoard from "../pages/AdiminPages/AdminDashBoard";
import Checkout from "../pages/Checkout/Checkout";
import ProductManagement from "../pages/AdiminPages/ProductManagement";
import CategoryManagement from "../pages/AdiminPages/CategoryManagement";

const Routing = () => {
  return (
     <Routes>

        <Route path="/" element={<Home />} />

        <Route
          path="/login"
          element={<Login />}
        />

        <Route
          path="/register"
          element={<Register />}
        />

        <Route
          path="/products"
          element={<Products />}
        />

        <Route
          path="/cart"
          element={<Cart />}
        />

        <Route
          path="/profile"
          element={<Profile />}
        />
        <Route
          path="/category"
          element={<Category />}
        />

        <Route
  path="/checkout"
  element={<Checkout />}
/>

        <Route element={<AdminProtectedRoutes allowedRoles={['admin']}/>}>
          <Route path="/admin" element={<AdminDashBoard/>}/>
          <Route path="/admin/productmanagement" element={<ProductManagement />} />
          <Route path="/admin/catogorymanagement" element={<CategoryManagement/>}/>
        </Route>

      </Routes>

  )
}

export default Routing;