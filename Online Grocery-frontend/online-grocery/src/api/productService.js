import { productsClient } from './axiosClient.js';

export const productService = {
  getAllProducts: () => productsClient.get('/products'),
  getALlCategories:()=>productsClient.get('/categories') 
};