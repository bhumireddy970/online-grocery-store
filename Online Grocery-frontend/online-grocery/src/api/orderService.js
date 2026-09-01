import { orderClient } from "./axiosClient.js";

export const orderService = {
    loginCustomer: (loginData) => orderClient.post('/customers/login',loginData) ,
    updateCustomerProfile: (id,FormData) =>  orderClient.put(`/customers/${id}`,FormData),
    createCustomer:(userDetails)=>orderClient.post('/customers',userDetails)
};