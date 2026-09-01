import "./AdminDashBoard.scss"
const AdminDashBoard = () => {
  return (
    <div className='table-container'>
      <h1 className='heading'>AdminDashBoard</h1>
      <h2 className='heading'>Products Table</h2>
      <table  className='custom-table'>
        <thead className='dashboard-heading'>
          <tr>
            <th>sku</th>
            <th>name</th>
            <th>price</th>
            <th>description</th>
            <th>active</th>
            <th>categoryId</th>
            <th>initialQuantity</th>
            <th></th>
          </tr>
        </thead>
        <tbody className='dashboard-body'>
          <tr>
            <td>APL001</td>
            <td>APL001</td>
            <td>APL001</td>
            <td>APL001</td>
            <td>APL001</td>
            <td>APL001</td>
            <td>APL001</td>
          </tr>
          <tr>
            <td>APL001</td>
            <td>APL001</td>
            <td>APL001</td>
            <td>APL001</td>
            <td>APL001</td>
            <td>APL001</td>
            <td>APL001</td>
          </tr>
          <tr>
            <td>APL001</td>
            <td>APL001</td>
            <td>APL001</td>
            <td>APL001</td>
            <td>APL001</td>
            <td>APL001</td>
            <td>APL001</td>
          </tr>
          <tr>
            <td>APL001</td>
            <td>APL001</td>
            <td>APL001</td>
            <td>APL001</td>
            <td>APL001</td>
            <td>APL001</td>
            <td>APL001</td>
          </tr>
        </tbody>
      </table>
    </div>
  )
}

export default AdminDashBoard