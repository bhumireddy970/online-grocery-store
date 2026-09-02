import { inventoryClient } from "./axiosClient";

export const inventoryService={
    getInventoryByProductId:(id)=>inventoryClient.get(`/inventory/product/${id}`)
}