import "./CategoryCard.scss";

const CategoryCard = ({ Category }) => {
  return (
    <div className="category-card">
      <div className="category-image">
        <img src={`../../public/${Category.name}.jpg`} alt={Category.name} />
      </div>

      <div className="category-details">
        <h3 className="category-name">{Category.name}</h3>
      </div>
    </div>
  );
};

export default CategoryCard;
