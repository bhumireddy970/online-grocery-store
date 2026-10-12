import { BASE_URL } from "./axiosClient";

export const inventoryService={
    getInventoryByProductId:(id)=>BASE_URL.get(`/inventory/product/${id}`)
}