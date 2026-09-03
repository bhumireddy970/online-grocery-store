import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { expect } from "vitest";
import { AuthContext } from "../../context/AuthContext";
import { CartContext } from "../../context/CartContext";
import Navbar from "./Navbar";

describe("Navbar Component", () => {
  const renderNavbar = (user, cartItems = []) => {
    render(
      <MemoryRouter>
        <AuthContext.Provider value={{ user }}>
          <CartContext.Provider value={{ cartItems }}>
            <Navbar />
          </CartContext.Provider>
        </AuthContext.Provider>
      </MemoryRouter>,
    );
  };

  it("should display Login when user is not logged in", () => {
    renderNavbar(null);
    expect(screen.getByRole("link", { name: /login/i })).toBeInTheDocument();
    expect(screen.getByRole("link", { name: /Home/i })).toBeInTheDocument();
    expect(screen.getByRole("link", { name: /Products/i })).toBeInTheDocument();
  });

  it("should display Products for normal user", () => {
    const user = {
      role: "user",
    };
    renderNavbar(user);
    expect(screen.getByRole("link", { name: /profile/i })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /logout/i })).toBeInTheDocument();
    expect(screen.getByRole("link", { name: /Home/i })).toBeInTheDocument();
    expect(screen.getByRole("link", { name: /Products/i })).toBeInTheDocument();
    expect(
      screen.queryByRole("link", { name: /Admin DashBoard/i }),
    ).not.toBeInTheDocument();
  });

  it("should display Admin DashBoard for Admin user", () => {
    const user = {
      role: "admin",
    };
    renderNavbar(user);
    expect(screen.getByRole("link", { name: /profile/i })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /logout/i })).toBeInTheDocument();
    expect(screen.getByRole("link", { name: /Home/i })).toBeInTheDocument();
    expect(
      screen.getByRole("link", { name: /Admin DashBoard/i }),
    ).toBeInTheDocument();
    expect(
      screen.queryByRole("link", { name: /Products/i }),
    ).not.toBeInTheDocument();
  });

  it("should display cart item count", () => {
    const user = {
      role: "user",
    };
    const cartItems = [
      { id: 1, name: "Apple" },
      { id: 2, name: "Milk" },
      { id: 3, name: "Bread" },
    ];

    renderNavbar(user, cartItems);

    expect(screen.getByText("3")).toBeInTheDocument();
  });
});
