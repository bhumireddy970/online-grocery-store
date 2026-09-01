import "./CategoryCard.scss";

const CategoryCard = ({ Category }) => {
  return (
    
      <div className="category-card">
          <div className="category-image">
            <img
              src={
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQcMNi6WAbyesx5sPfFA7HRcjDbTa9qjEaNh-rUsc_V-g&s=10"
              }
              alt={Category.name}
            />
          </div>

          <div className="category-details">
            <h3 className="category-name">{Category.name}</h3>
          </div>
        
      </div>
    
  );
};

export default CategoryCard;
