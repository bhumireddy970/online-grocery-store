import { Link } from "react-router-dom";
import BackButton from "../../components/Buttons/BackButton";
import Card from "../../components/Card/Card";
import { useEffect, useState, useContext } from "react";
import { productService } from "../../api/productService";

import { AuthContext } from "../../context/AuthContext";

import "./Products.scss";
import Category from "../Category/Category";

const Products = () => {
  const [products, setProducts] = useState([]);
  const [search, setSearch] = useState("");
  const [ setCategory] = useState([]);
  const getCategory = async () => {
    try {
      const response = await productService.getALlCategories();
      setCategory(response.data);
    } catch (error) {
      console.log(error.message);
    }
  };
  useEffect(() => {
    getCategory();
  }, []);

  const { categoryId, setCategoryId } = useContext(AuthContext);

  useEffect(() => {
    productService
      .getAllProducts()
      .then((response) => {
        const allProducts = response?.data || [];

        if (categoryId) {
          const filteredProducts = allProducts.filter(
            (product) => product.categoryId === categoryId,
          );

          setProducts(filteredProducts);
        } else {
          setProducts(allProducts);
        }
      })
      .catch((error) => console.error(error?.message));
  }, [categoryId]);

  const filteredProducts = products.filter((product) =>
    product.name?.toLowerCase().includes(search.toLowerCase()),
  );

  return (
    <>
      <Link to="/">
        <BackButton />
      </Link>

      {categoryId && (
        <button
          type="button"
          onClick={() => setCategoryId(null)}
          className="show-all-btn"
        >
          ✕ Show All Products
        </button>
      )}

      <div className="section-title">Categories</div>

      <div>
        <Category />
      </div>

      <div className="section-title">Products</div>

      <div className="search-container">
        <input
          type="text"
          placeholder="Search products..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      <div className="products-grid">
        {filteredProducts.length > 0 ? (
          filteredProducts.map((product) => (
            <Card key={product.id} product={product} />
          ))
        ) : (
          <div className="no-products">No products found.</div>
        )}
      </div>
    </>
  );
};

export default Products;
