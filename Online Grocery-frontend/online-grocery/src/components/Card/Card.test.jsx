import { render, screen } from "@testing-library/react";
import { CartContext } from "../../context/CartContext";
import userEvent from "@testing-library/user-event";
import { describe, it, expect, vi, beforeEach } from "vitest";
import Card from "./Card";

describe("Card component", () => {
  let product;
  let addToCart;
  beforeEach(() => {
    product = {
      name: "Apple",
      price: 100,
    };

    addToCart = vi.fn();

    render(
      <CartContext.Provider value={{ addToCart }}>
        <Card product={product} />
      </CartContext.Provider>,
    );
  });

  it("should render add to cart button", () => {
    expect(
      screen.getByRole("button", { name: /Add to Cart/i }),
    ).toBeInTheDocument();
  });

  it("should call addToCart when Add to Cart button is clicked", async () => {
    const cartButton = screen.getByRole("button", {
      name: /Add to Cart/i,
    });

    const user = userEvent.setup();

    await user.click(cartButton);

    expect(addToCart).toHaveBeenCalledTimes(1);
  });

  it("should display product name", () => {
    expect(screen.getByText("Apple")).toBeInTheDocument();
  });

  it("should display product price", () => {
    expect(screen.getByText("₹100")).toBeInTheDocument();
  });
});
