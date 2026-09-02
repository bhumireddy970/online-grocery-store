import { productsClient } from './axiosClient.js';

export const productService = {
  getAllProducts: () => productsClient.get('/products'),
  addProduct:(formData)=>productsClient.post('/products',formData), 
  updateProduct:(formData)=>productsClient.put(`/products/${formData.id}`,formData), 
  deleteProduct:(id)=>productsClient.delete(`/products/${id}`), 

  getALlCategories:()=>productsClient.get('/categories'),
  updateCategory:(formData)=>productsClient.put(`/categories/${formData.id}`,formData),
  deleteCategories:(id)=>productsClient.delete(`/categories/${id}`),
  addCateogory:(formData)=>productsClient.post("/categories",formData)
};