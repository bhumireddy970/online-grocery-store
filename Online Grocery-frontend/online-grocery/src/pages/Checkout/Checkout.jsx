import { useContext, useState } from "react";
import { useNavigate } from "react-router-dom";
import { CartContext } from "../../context/CartContext";
import "./Checkout.scss";

const Checkout = () => {
  const { cartItems, orderId, proceedToCheckout, processBooking } =
    useContext(CartContext);

  const navigate = useNavigate();

  const [loading, setLoading] = useState(false);

  const totalAmount = cartItems.reduce(
    (total, item) => total + (item.price || 0) * item.quantity,
    0,
  );

  const handleProceedToCheckout = async () => {
    setLoading(true);

    const newOrderId = await proceedToCheckout();

    setLoading(false);

    if (newOrderId) {
      alert("Order created successfully!");
    }
  };

  //

  const handleBooking = async () => {
    setLoading(true);

    const success = await processBooking();

    setLoading(false);

    if (success) {
      navigate("/");
    }
  };

  if (cartItems.length === 0) {
    return (
      <div className="checkout-container">
        <h2>Your cart is empty</h2>

        <button onClick={() => navigate("/products")}>Continue Shopping</button>
      </div>
    );
  }

  return (
    <div className="checkout-container">
      <h1>Checkout</h1>

      <div className="checkout-items">
        {cartItems.map((item) => {
          const productId = item.productId || item.id;

          const itemTotal = (item.price || 0) * item.quantity;

          return (
            <div className="checkout-item" key={productId}>
              <div className="checkout-product">
                {item.image && <img src={item.image} alt={item.name} />}

                <div>
                  <h3>{item.name}</h3>

                  <p>Price: ₹{item.price}</p>

                  <p>Quantity: {item.quantity}</p>
                </div>
              </div>

              <div className="item-total">₹{itemTotal.toFixed(2)}</div>
            </div>
          );
        })}
      </div>

      <div className="order-summary">
        <h2>Order Summary</h2>

        <div className="summary-row">
          <span>Items</span>
          <span>{cartItems.length}</span>
        </div>

        <div className="summary-row">
          <span>Total Quantity</span>

          <span>
            {cartItems.reduce((total, item) => total + item.quantity, 0)}
          </span>
        </div>

        <div className="summary-row total">
          <span>Total Amount</span>

          <span>₹{totalAmount.toFixed(2)}</span>
        </div>
      </div>

      {!orderId ? (
        <button
          className="checkout-button"
          onClick={handleProceedToCheckout}
          disabled={loading}
        >
          {loading ? "Creating Order..." : "Proceed to Checkout"}
        </button>
      ) : (
        <button
          className="checkout-button"
          onClick={handleBooking}
          disabled={loading}
        >
          {loading ? "Processing..." : "Confirm & Book"}
        </button>
      )}
    </div>
  );
};

export default Checkout;
