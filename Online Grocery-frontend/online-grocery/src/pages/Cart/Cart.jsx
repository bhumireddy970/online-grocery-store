import { useContext } from "react";
import { useNavigate } from "react-router-dom";
import { CartContext } from "../../context/CartContext";
import "./Cart.scss";

const Cart = () => {
  const { cartItems, addToCart, removeFromCart } = useContext(CartContext);

  const navigate = useNavigate();

  const totalAmount = cartItems.reduce(
    (acc, item) => acc + item.price * item.quantity,
    0,
  );

  if (cartItems.length === 0) {
    return <div className="empty-cart">Your cart is empty.</div>;
  }

  return (
    <div style={{ padding: "20px" }}>
      <h2>Your Shopping Cart</h2>

      <div className="cart-items-list">
        {cartItems.map((item) => {
          const productId = item.productId || item.id;

          return (
            <div key={productId} className="cart-item-row">
              <img
                src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQcMNi6WAbyesx5sPfFA7HRcjDbTa9qjEaNh-rUsc_V-g&s=10"
                alt={item.name}
                width="50"
              />

              <div>
                <h4>{item.name}</h4>

                <p>Price: ₹{item.price}</p>

                <p>Quantity: {item.quantity}</p>

                <p>Subtotal: ₹{item.price * item.quantity}</p>
              </div>

              <div className="quantity-actions-wrapper">
                <button onClick={() => removeFromCart(productId)}>-</button>

                <span>{item.quantity}</span>

                <button onClick={() => addToCart(item)}>+</button>
              </div>
            </div>
          );
        })}
      </div>

      <div className="cart-summary-section">
        <h3>Total Order Amount: ₹{totalAmount}</h3>

        <button
          onClick={() => navigate("/checkout")}
          className="checkout-action-btn"
        >
          Proceed to Checkout
        </button>
      </div>
    </div>
  );
};

export default Cart;
