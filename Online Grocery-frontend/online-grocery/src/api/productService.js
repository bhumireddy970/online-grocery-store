import { BASE_URL } from './axiosClient.js';

export const productService = {
  getAllProducts: () => BASE_URL.get('/products'),
  addProduct:(formData)=>BASE_URL.post('/products',formData), 
  updateProduct:(formData)=>BASE_URL.put(`/products/${formData.id}`,formData), 
  deleteProduct:(id)=>BASE_URL.delete(`/products/${id}`), 

  getALlCategories:()=>BASE_URL.get('/categories'),
  updateCategory:(formData)=>BASE_URL.put(`/categories/${formData.id}`,formData),
  deleteCategories:(id)=>BASE_URL.delete(`/categories/${id}`),
  addCateogory:(formData)=>BASE_URL.post("/categories",formData)
};