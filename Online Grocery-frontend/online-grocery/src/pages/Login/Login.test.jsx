import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter } from "react-router-dom";
import { AuthContext } from "../../context/AuthContext";
import Login from "./Login";
import { describe, it, expect, vi, beforeEach } from "vitest";
import { orderService } from "../../api/orderService";

vi.mock("../../api/orderService", () => ({
  orderService: {
    loginCustomer: vi.fn(),
  },
}));

const navigate = vi.fn();

vi.mock("react-router-dom", async () => {
  const actual = await vi.importActual("react-router-dom");

  return {
    ...actual,
    useNavigate: () => navigate,
  };
});

describe("Login Component", () => {
  const login = vi.fn();

  const renderLogin = () => {
    render(
      <MemoryRouter>
        <AuthContext.Provider value={{ login }}>
          <Login />
        </AuthContext.Provider>
      </MemoryRouter>,
    );
  };

  beforeEach(() => {
    vi.clearAllMocks();
    renderLogin();
  });

  it("should render the login page", () => {
    expect(screen.getByText("Welcome Back")).toBeInTheDocument();
    expect(screen.getByText("Login to your account")).toBeInTheDocument();
    expect(screen.getByLabelText("Email")).toBeInTheDocument();
    expect(screen.getByLabelText("Password")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /login/i })).toBeInTheDocument();
    expect(screen.getByText("Don't have an account?")).toBeInTheDocument();
    expect(screen.getByRole("link", { name: /register/i })).toBeInTheDocument();
  });

  it("should user able to enter email and password", async () => {
    const user = userEvent.setup();

    const emailInput = screen.getByLabelText("Email");
    const passwordInput = screen.getByLabelText("Password");

    await user.type(emailInput, "test@gmail.com");
    await user.type(passwordInput, "password123");

    expect(emailInput).toHaveValue("test@gmail.com");
    expect(passwordInput).toHaveValue("password123");
  });

  it("should login button able to work when clicked", async () => {
    const user = userEvent.setup();

    const emailInput = screen.getByLabelText("Email");

    const passwordInput = screen.getByLabelText("Password");

    const loginButton = screen.getByRole("button", { name: /login/i });

    orderService.loginCustomer.mockResolvedValue({
      data: {
        id: 1,
        email: "test@gmail.com",
        role: "user",
      },
    });

    await user.type(emailInput, "test@gmail.com");

    await user.type(passwordInput, "password123");

    await user.click(loginButton);

    expect(orderService.loginCustomer).toHaveBeenCalledTimes(1);

    expect(orderService.loginCustomer).toHaveBeenCalledWith({
      email: "test@gmail.com",
      password: "password123",
    });
  });

  it("should navigate to home after successful login", async () => {
    const user = userEvent.setup();
    
    await user.type(screen.getByLabelText("Email"), "test@gmail.com");

    await user.type(screen.getByLabelText("Password"), "password123");

    await user.click(screen.getByRole("button", { name: /login/i }));

    expect(navigate).toHaveBeenCalledWith("/");
  });
});
