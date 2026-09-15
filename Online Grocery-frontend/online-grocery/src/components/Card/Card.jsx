import { ShoppingCart } from "lucide-react";
import "./Card.scss";
import { useContext } from "react";
import { CartContext } from "../../context/CartContext";

const Card = ({ product }) => {
  const { cartItems, addToCart, removeFromCart } = useContext(CartContext);

  const currentCartItem = cartItems.find((item) => item.id === product.id);

  const handleAddToCartClick = () => {
    addToCart(product);
  };
  return (
    <div className="product-card">
      <div className="product-image">
        <img
          src={
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQcMNi6WAbyesx5sPfFA7HRcjDbTa9qjEaNh-rUsc_V-g&s=10"
          }
          alt={product.name}
        />
      </div>

      <div className="product-details">
        <h3 className="product-name">{product.name}</h3>

        <p className="product-price">₹{product.price}</p>

        {currentCartItem? (
          <div className="quantity-actions-wrapper">
            <button onClick={() => removeFromCart(product.id)}>-</button>

            <span>{currentCartItem.quantity}</span>

            <button onClick={() => addToCart(currentCartItem)}>+</button>
          </div>
        ) : (
          <button
            type="submit"
            className="add-cart-button"
            onClick={handleAddToCartClick}
          >
            <ShoppingCart size={18} />
            Add to Cart
          </button>
        )}
      </div>
    </div>
  );
};

export default Card;
