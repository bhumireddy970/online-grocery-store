import { render, screen } from "@testing-library/react";
import { describe, it, expect } from "vitest";
import LogoutButton from "./LogoutButton";
import { AuthContext } from "../../context/AuthContext";
import userEvent from "@testing-library/user-event";

describe("LogoutButton", () => {
  it("should render Logout button", () => {
    const logout = () => {};

    render(
      <AuthContext.Provider value={{ logout }}>
        <LogoutButton />
      </AuthContext.Provider>,
    );

    expect(screen.getByRole("button", { name: /logout/i })).toBeInTheDocument();
  });

   it("should call logout when button is clicked", async () => {
    const logout = vi.fn();

    const user = userEvent.setup();

    render(
      <AuthContext.Provider value={{ logout }}>
        <LogoutButton />
      </AuthContext.Provider>
    );

    const logoutButton = screen.getByRole("button", {
      name: /logout/i
    });

    await user.click(logoutButton);

    expect(logout).toHaveBeenCalledTimes(1);
  });

});
