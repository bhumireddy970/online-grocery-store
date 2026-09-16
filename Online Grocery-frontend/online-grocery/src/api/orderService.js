import { orderClient } from "./axiosClient.js";

export const orderService = {
    loginCustomer: (loginData) => orderClient.post('/customers/login',loginData) ,
    updateCustomerProfile: (FormData) =>  orderClient.put(`/customers/${FormData.id}`,FormData),
    createCustomer:(userDetails)=>orderClient.post('/customers',userDetails),
    getAllCustomers:()=> orderClient.get('/customers'),
    deleteCustomer:(id)=> orderClient.delete(`/customers/${id}`)
};