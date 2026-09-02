import "./Category.scss";
import CategoryCard from "../../components/Card/CategoryCard";
import { useState, useEffect, useContext } from "react";
import { productService } from "../../api/productService";
import { AuthContext } from "../../context/AuthContext";

const Category = () => {
  const [category, setCategory] = useState([]);
  const { setCategoryId } = useContext(AuthContext);
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

  return (
    <div>
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(10, 1fr)",
          margin: "20px",
          alignItems: "center",
          justifyContent: "space-between",
        }}
      >
        {category.map((category) => (
          <div
            key={category.id}
            onClick={() => setCategoryId(category.id)}
            tabIndex="0"
            role="button"
          >
            <CategoryCard Category={category} />
          </div>
        ))}
      </div>
    </div>
  );
};

export default Category;
