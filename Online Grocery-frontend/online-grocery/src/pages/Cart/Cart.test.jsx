import { expect, vi } from "vitest";
import Cart from "./Cart";
import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { CartContext } from "../../context/CartContext";
import userEvent from "@testing-library/user-event";

const navigate = vi.fn();
const addToCart = vi.fn();
const removeFromCart = vi.fn();

vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual("react-router-dom");

  return {
    ...actual,
    useNavigate: () => navigate,
  };
});

describe("Cart Component", () => {
  const mockCartItems = [
    {
      name: "Oppo",
      price: 18000,
      quantity: 2,
    },
  ];

  const renderCart = (item=mockCartItems) => {
    render(
      <MemoryRouter>
        <CartContext.Provider
          value={{
            cartItems : item,
            addToCart,
            removeFromCart,
          }}
        >
          <Cart />
        </CartContext.Provider>
      </MemoryRouter>,
    );
  };

  it("Cart should show items if present", () => {
    renderCart();

    expect(screen.getByText(/Your Shopping Cart/)).toBeInTheDocument();
    expect(screen.getByRole("img",{name:"Oppo"})).toBeInTheDocument();
    expect(screen.getByText(/Oppo/)).toBeInTheDocument();
    expect(screen.getByText(/Price: ₹18000/)).toBeInTheDocument();
    expect(screen.getByText(/Quantity: 2/)).toBeInTheDocument();
    expect(screen.getByText(/Subtotal: ₹36000/)).toBeInTheDocument();
    expect(screen.getByText(/Total Order Amount: ₹36000/)).toBeInTheDocument();
    expect(
      screen.getByText(/Total Order Amount:\s*₹\s*36000/),
    ).toBeInTheDocument();
  });

  it("Cart should show message if items not present", () => {
    renderCart([]);

    expect(screen.getByText("Your cart is empty.")).toBeInTheDocument();
   
  });

  it("Should call addTocart when plus button is clicked",async()=>{
    const user=userEvent.setup();
    renderCart();

    const addButton=screen.getByRole("button",{name:"+"});
    
    await user.click(addButton);

    expect(addToCart).toHaveBeenCalled();
  })

  it("Should call removeFromCart when minus button is clicked",async()=>{
    const user=userEvent.setup();
    renderCart();

    const removeButton=screen.getByRole("button",{name:"-"});
    
    await user.click(removeButton);

    expect(addToCart).toHaveBeenCalled();
  })

  it("should navigate to checkout when checkout button is clicked", async () => {
  const user = userEvent.setup();
  renderCart();

  const checkoutButton = screen.getByRole("button", { name: /proceed to checkout/i,});

  await user.click(checkoutButton);

  expect(navigate).toHaveBeenCalledWith("/checkout");
});
});
