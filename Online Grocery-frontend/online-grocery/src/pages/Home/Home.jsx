import { Link } from "react-router-dom";
import "./Home.scss";

const Home = () => {
  return (
    <div className="home-page">
      <section className="hero-section">
        <div className="hero-content">
          <h1>Fresh Groceries, Delivered to Your Door</h1>

          <p>
            Shop fresh fruits, vegetables, dairy products, beverages and more at
            the best prices.
          </p>

          <Link to="/products">
            <button className="shop-now-btn">Shop Now</button>
          </Link>
        </div>
      </section>

      <section className="home-section">
        <h2>Shop by Category</h2>

        <div className="category-container">
          <div className="category-card">
            <div className="category-icon">🍎</div>
            <h3>Fruits</h3>
            <p>Fresh and healthy fruits</p>
          </div>

          <div className="category-card">
            <div className="category-icon">🥦</div>
            <h3>Vegetables</h3>
            <p>Fresh vegetables every day</p>
          </div>

          <div className="category-card">
            <div className="category-icon">🥛</div>
            <h3>Dairy</h3>
            <p>Milk, cheese and more</p>
          </div>

          <div className="category-card">
            <div className="category-icon">🥤</div>
            <h3>Beverages</h3>
            <p>Refreshing drinks</p>
          </div>
        </div>
      </section>

      <section className="why-section">
        <h2>Why Choose Us?</h2>

        <div className="features-container">
          <div className="feature-card">
            <div className="feature-icon">🚚</div>
            <h3>Fast Delivery</h3>
            <p>Get your groceries delivered quickly to your doorstep.</p>
          </div>

          <div className="feature-card">
            <div className="feature-icon">🌱</div>
            <h3>Fresh Products</h3>
            <p>We provide fresh and quality products for your family.</p>
          </div>

          <div className="feature-card">
            <div className="feature-icon">💰</div>
            <h3>Best Prices</h3>
            <p>Enjoy great products at affordable prices.</p>
          </div>
        </div>
      </section>

      <section className="cta-section">
        <h2>Ready to Start Shopping?</h2>

        <p>Find everything you need in one place.</p>

        <Link to="/products">
          <button className="shop-now-btn">Explore Products</button>
        </Link>
      </section>
    </div>
  );
};

export default Home;
