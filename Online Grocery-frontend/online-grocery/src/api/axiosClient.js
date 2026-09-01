import axios from "axios";

export const productsClient = axios.create({
  baseURL: "http://localhost:8081/api",
  timeout: 5000,
  headers: { "Content-Type":' application / json' }
});

export const inventoryClient = axios.create({
  baseURL: "http://localhost:8082/api",
  timeout: 5000,
  headers: { "Content-Type":' application / json' }
});

export const orderClient = axios.create({
  baseURL: "http://localhost:8083/api",
  timeout: 5000
});