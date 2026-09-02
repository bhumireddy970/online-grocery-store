import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import "./ProductManagement.scss";
import { productService } from "../../api/productService";
import { inventoryService } from "../../api/inventoryService";
import BackButton from "../../components/Buttons/BackButton";

export default function ProductManagement() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const initialFormState = {
    id: "",
    sku: "",
    name: "",
    price: "",
    description: "",
    active: "true",
    categoryId: "",
    initialQuantity: "",
  };
  const [formData, setFormData] = useState(initialFormState);
  const [isEditing, setIsEditing] = useState(false);

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    setLoading(true);
    try {
      const response = await productService.getAllProducts();

      const productsData = response.data;

      for (let product of productsData) {
        try {
          const invRes = await inventoryService.getInventoryByProductId(
            product.id,
          );
          product.inventoryCount = invRes?.data?.availableQuantity;
          product.inventoryCountreserved = invRes?.data?.reservedQuantity;
        } catch (err) {
          product.inventoryCount = "N/A";
          console.log(err);
        }
      }

      setProducts(response.data);
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
    if (!formData.sku || !formData.name) {
      alert("SKU and Name are required fields.");
      return;
    }

    try {
      if (isEditing) {
        await productService.updateProduct(formData);
        setProducts((prev) =>
          prev.map((p) => (p.id === formData.id ? { ...formData } : p)),
        );
      } else {
        const response = await productService.addProduct(formData);
        const newProduct = response?.data;
        setProducts((prev) => [...prev, newProduct]);
      }
      handleClear();
    } catch (err) {
      alert(err.message);
    }
  };

  const handleEditClick = (product) => {
    setIsEditing(true);
    setFormData({
      id: product.id || "",
      sku: product.sku || "",
      name: product.name || "",
      price: product.price || "",
      description: product.description || "",
      active: String(product.active),
      categoryId: product.categoryId || "",
      initialQuantity: product.initialQuantity || "",
    });
  };

  const handleDeleteClick = async (id) => {
    if (!window.confirm("Are you sure you want to delete this product?"))
      return;

    try {
      await productService.deleteProduct(id);

      setProducts((prev) => prev.filter((p) => p.id !== id));
      if (isEditing && formData.id === id) {
        handleClear();
      }
    } catch (err) {
      alert(err?.response?.data?.message);
    }
  };

  return (
    <div className="product-manager">
      <Link to="/admin">
        <BackButton />
      </Link>
      <div className="form-container">
        <h3>{isEditing ? " Update Product" : " Add New Product"}</h3>
        <form onSubmit={handleSubmit} className="product-form">
          <input
            type="text"
            name="sku"
            placeholder="SKU"
            value={formData.sku}
            onChange={handleInputChange}
          />
          <input
            type="text"
            name="name"
            placeholder="Product Name"
            value={formData.name}
            onChange={handleInputChange}
          />
          <input
            type="number"
            name="price"
            placeholder="Price"
            value={formData.price}
            onChange={handleInputChange}
          />
          <input
            type="text"
            name="description"
            placeholder="Description"
            value={formData.description}
            onChange={handleInputChange}
          />

          <select
            name="active"
            value={formData.active}
            onChange={handleInputChange}
          >
            <option value="true">Active</option>
            <option value="false">Inactive</option>
          </select>

          <input
            type="text"
            name="categoryId"
            placeholder="Category ID"
            value={formData.categoryId}
            onChange={handleInputChange}
          />
          {!isEditing ? (
            <input
              type="number"
              name="initialQuantity"
              placeholder="Initial Qty"
              value={formData.initialQuantity}
              onChange={handleInputChange}
            />
          ) : (
            ""
          )}

          <div className="form-actions">
            <button
              type="submit"
              className={`btn-submit ${isEditing ? "edit-mode" : "add-mode"}`}
            >
              {isEditing ? "Update Product" : "Add Product"}
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
          <table className="product-table">
            <thead>
              <tr>
                <th>SKU</th>
                <th>Name</th>
                <th>Price</th>
                <th>Description</th>
                <th>Active</th>
                <th>Category ID</th>
                <th>Available Quantity</th>
                <th>Reserved Quantity</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {products.length === 0 ? (
                <tr>
                  <td colSpan="8" className="empty-cell">
                    No products found.
                  </td>
                </tr>
              ) : (
                products.map((product) => (
                  <tr key={product.id || product.sku}>
                    <td>{product.sku}</td>
                    <td>{product.name}</td>
                    <td>{product.price}</td>
                    <td>{product.description}</td>
                    <td>{String(product.active)}</td>
                    <td>{product.categoryId}</td>
                    <td>{product.inventoryCount}</td>
                    <td>{product.inventoryCountreserved}</td>
                    <td className="actions-cell">
                      <button
                        onClick={() => handleEditClick(product)}
                        className="btn-edit"
                      >
                        {" "}
                        Edit
                      </button>
                      <button
                        onClick={() => handleDeleteClick(product.id)}
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
