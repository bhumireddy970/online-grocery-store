import { CartContext } from "../../context/CartContext";
import Card from "./Card";
import ProductsData from "../../constants/ProductsData";

const product = {
  id: 10,
  name: "Mango",
  price: 499,
};

export default {
  title: "components/Card",
  component: Card,
  tags: ["autodocs"],
   decorators: [
    (Story) => (
      <CartContext.Provider
        value={{
          cartItems: ProductsData,
          addToCart: () => {},
          removeFromCart: () => {},
        }}
      >
        <Story />
      </CartContext.Provider>
    ),
  ],
};


export const Product = {
  args: {
    product,
  },
};
