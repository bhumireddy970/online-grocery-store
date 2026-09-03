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
   
      <div className="category-container">
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
   
  );
};

export default Category;
