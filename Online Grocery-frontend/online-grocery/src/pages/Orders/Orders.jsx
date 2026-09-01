import React from 'react'
import { Link } from 'react-router-dom'
import BackButton from '../components/BackButton/BackButton'

const Orders = () => {
  return (
    <>
    <Link to='/'><BackButton /></Link>
   
    <div>Order</div>
    </>
  )
}

export default Orders