import { ShoppingCart } from "lucide-react";
import "./Card.scss";
import { useContext } from "react";
import {CartContext} from "../../context/CartContext";

const Card = ({ product }) => {
   const { addToCart } = useContext(CartContext);

  const handleAddToCartClick = () => {
    addToCart(product);
  };
  return (
    <div className="product-card">
      <div className="product-image">
        <img src={"https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQcMNi6WAbyesx5sPfFA7HRcjDbTa9qjEaNh-rUsc_V-g&s=10"} alt={product.name} />
      </div>

      <div className="product-details">
        <h3 className="product-name">{product.name}</h3>

        <p className="product-price">₹{product.price}</p>

        <button type="submit" className="add-cart-button" onClick={handleAddToCartClick}>
          <ShoppingCart size={18} />
          Add to Cart
        </button>
      </div>
    </div>
  );
};

export default Card;
