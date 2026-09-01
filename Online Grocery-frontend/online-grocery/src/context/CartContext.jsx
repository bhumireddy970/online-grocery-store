import { createContext, useState, useEffect, useContext, useMemo } from "react";
import { AuthContext } from "./AuthContext";
import axios from "axios";

export const CartContext = createContext();

export const CartProvider = ({ children }) => {
  const { user } = useContext(AuthContext);

  const [cartItems, setCartItems] = useState([]);
  const [orderId, setOrderId] = useState(null);

  const customerId = user?.id;

  useEffect(() => {
    if (!customerId) {
      setCartItems([]);
      setOrderId(null);
      return;
    }

    const storedCart = localStorage.getItem(`cart_${customerId}`);

    if (storedCart) {
      try {
        setCartItems(JSON.parse(storedCart));
      } catch (error) {
        console.error("Error parsing cart from local storage:", error);
        setCartItems([]);
      }
    } else {
      setCartItems([]);
    }
  }, [customerId]);

  useEffect(() => {
    if (!customerId) {
      return;
    }

    localStorage.setItem(`cart_${customerId}`, JSON.stringify(cartItems));
  }, [cartItems, customerId]);

  const addToCart = (product) => {
    if (!user) {
      alert("Please log in to add items to your cart!");
      return false;
    }

    const targetId = product.productId || product.id;

    const existingItem = cartItems.find(
      (item) => item.productId === targetId || item.id === targetId,
    );

    let updatedItems;

    if (existingItem) {
      updatedItems = cartItems.map((item) =>
        item.productId === targetId || item.id === targetId
          ? {
              ...item,
              quantity: item.quantity + 1,
            }
          : item,
      );
    } else {
      updatedItems = [
        ...cartItems,
        {
          ...product,
          productId: targetId,
          quantity: 1,
        },
      ];
    }

    setCartItems(updatedItems);
    console.log(cartItems);

    return true;
  };

  const removeFromCart = (productId) => {
    const updatedItems = cartItems
      .map((item) =>
        item.productId === productId || item.id === productId
          ? {
              ...item,
              quantity: item.quantity - 1,
            }
          : item,
      )
      .filter((item) => item.quantity > 0);

    setCartItems(updatedItems);
    console.log(cartItems)
  };

  const clearCart = () => {
    setCartItems([]);
    setOrderId(null);

    if (customerId) {
      localStorage.removeItem(`cart_${customerId}`);
    }
  };

  const proceedToCheckout = async () => {
    if (!user) {
      alert("Please log in first!");
      return null;
    }

    if (cartItems.length === 0) {
      alert("Your cart is empty!");
      return null;
    }

    const requestBody = {
      customerId: customerId,
      items: cartItems.map((item) => ({
        productId: item.productId || item.id,
        quantity: item.quantity,
      })),
    };

    try {
      const response = await axios.post(
        "http://localhost:8083/api/orders",
        requestBody,
      );

      console.log(response.data);

      if (response.data?.orderId) {
        setOrderId(response.data.orderId);
        return response.data.orderId;
      }

      return null;
    } catch (error) {
      console.error(
        "Error creating bulk order:",
        error.response?.data || error.message,
      );

      alert("Failed to proceed to checkout.");

      return null;
    }
  };

  const processBooking = async () => {
    if (!orderId) {
      alert("No order found!");
      return false;
    }

    try {
      await axios.put(`http://localhost:8083/api/orders/${orderId}/confirm`);

      alert("Booking processed successfully!");

      clearCart();

      return true;
    } catch (error) {
      console.error(
        "Booking confirmation error:",
        error.response?.data || error.message,
      );

      alert("Failed to confirm your booking.");

      return false;
    }
  };

  const value = useMemo(
    () => ({
      cartItems,
      orderId,
      addToCart,
      removeFromCart,
      proceedToCheckout,
      processBooking,
    }),
    [cartItems, orderId, customerId],
  );

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
};
