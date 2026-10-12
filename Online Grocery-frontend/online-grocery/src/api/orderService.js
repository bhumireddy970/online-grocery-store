import { BASE_URL } from "./axiosClient.js";

export const orderService = {
    loginCustomer: (loginData) => BASE_URL.post('/customers/login',loginData) ,
    updateCustomerProfile: (FormData) =>  BASE_URL.put(`/customers/${FormData.id}`,FormData),
    createCustomer:(userDetails)=>BASE_URL.post('/customers',userDetails),
    getAllCustomers:()=> BASE_URL.get('/customers'),
    deleteCustomer:(id)=> BASE_URL.delete(`/customers/${id}`),
    addOrders:(data)=>BASE_URL.post('/orders',data),
    confirmOrder: (orderId) =>BASE_URL.put(`/orders/${orderId}/confirm`)
};