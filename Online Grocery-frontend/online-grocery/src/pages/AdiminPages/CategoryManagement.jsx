import  { useState, useEffect } from "react";
import "./ProductManagement.scss";
import BackButton from "../../components/Buttons/BackButton";
import { Link } from "react-router-dom";
import { productService } from "../../api/productService";

export default function CategoryManager() {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const initialFormState = {
    id: "",
    name: "",
    description: "",
  };
  const [formData, setFormData] = useState(initialFormState);
  const [isEditing, setIsEditing] = useState(false);

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    setLoading(true);
    try {
      const response = await productService.getALlCategories();
      setCategories(response.data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleClear = () => {
    setFormData(initialFormState);
    setIsEditing(false);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.name) {
      alert("Name is a required field.");
      return;
    }

    try {
      if (isEditing) {
        await productService.updateCategory(formData);
        setCategories((prev) =>
          prev.map((c) => (c.id === formData.id ? { ...formData } : c)),
        );
      } else {
        const response = await fetch(API_URL, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(formData),
        });
        if (!response.ok) throw new Error("Failed to add category");
        const newCategory = await response.json();
        setCategories((prev) => [...prev, newCategory]);
      }
      handleClear();
    } catch (err) {
      alert(err.message);
    }
  };

  const handleEditClick = (category) => {
    setIsEditing(true);
    setFormData({
      id: category.id || "",
      name: category.name || "",
      description: category.description || "",
    });
  };

  const handleDeleteClick = async (id) => {
    console.log(id);
    if (!window.confirm("Are you sure you want to delete this category?"))
      return;

    try {
       await productService.deleteCategories(id);

      setCategories((prev) => prev.filter((c) => c.id !== id));
      if (isEditing && formData.id === id) {
        handleClear();
      }
    } catch (err) {
      console.log(err?.response?.data?.message);
      alert(err?.response?.data?.message);
    }
  };

  return (
    <div className="category-manager">
      <Link to="/admin">
        <BackButton />
      </Link>
      <div className="form-container">
        <h3>{isEditing ? " Update Category" : " Add New Category"}</h3>
        <form onSubmit={handleSubmit} className="category-form">
          <input
            type="text"
            name="name"
            placeholder="Category Name"
            value={formData.name}
            onChange={handleInputChange}
          />
          <input
            type="text"
            name="description"
            placeholder="Description"
            value={formData.description}
            onChange={handleInputChange}
          />

          <div className="form-actions">
            <button
              type="submit"
              className={`btn-submit ${isEditing ? "edit-mode" : "add-mode"}`}
            >
              {isEditing ? "Update Category" : "Add Category"}
            </button>
            <button type="button" onClick={handleClear} className="btn-clear">
              Clear
            </button>
          </div>
        </form>
      </div>

      {loading && <p className="status-message">Loading table data...</p>}
      {error && <p className="status-message error">Error: {error}</p>}

      {!loading && !error && (
        <div className="table-responsive">
          <table className="category-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Description</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {categories.length === 0 ? (
                <tr>
                  <td colSpan="3" className="empty-cell">
                    No categories found.
                  </td>
                </tr>
              ) : (
                categories.map((category) => (
                  <tr key={category.id}>
                    <td>{category.name}</td>
                    <td>{category.description}</td>
                    <td className="actions-cell">
                      <button
                        onClick={() => handleEditClick(category)}
                        className="btn-edit"
                      >
                        {" "}
                        Edit
                      </button>
                      <button
                        onClick={() => handleDeleteClick(category.id)}
                        className="btn-delete"
                      >
                        {" "}
                        Delete
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
