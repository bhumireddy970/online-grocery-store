import { MemoryRouter } from "react-router-dom";
import Navbar from "./Navbar";
import { AuthContext } from "../../context/AuthContext";
import { CartContext } from "../../context/CartContext";
import ProductsData from "../../constants/ProductsData";

const User = {
  id: 1,
  name: "Saradhi",
  phone: "9876543210",
  address: "Kadapa, Andhra Pradesh",
};

const cartItems = [
  { id: 1, name: "Apple" },
  { id: 2, name: "Milk" },
  { id: 3, name: "Bread" },
];

export default {
  title: "components/Navbar",
  component: Navbar,
  tags: ["autodocs"],
};

export const LoggedIn = {
  decorators: [
    (Story) => (
      <MemoryRouter>
        <AuthContext.Provider value={{ user: User }}>
          <CartContext.Provider value={{ cartItems: ProductsData }}>
            <Story />
          </CartContext.Provider>
        </AuthContext.Provider>
      </MemoryRouter>
    ),
  ],
};

export const NotLoggedIn = {
  decorators: [
    (Story) => (
      <MemoryRouter>
        <AuthContext.Provider value={{ user: null }}>
          <CartContext.Provider value={{ cartItems: null }}>
            <Story />
          </CartContext.Provider>
        </AuthContext.Provider>
      </MemoryRouter>
    ),
  ],
};
