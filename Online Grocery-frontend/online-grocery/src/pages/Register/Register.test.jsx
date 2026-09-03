import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import userEvent from "@testing-library/user-event";
import { describe, expect } from "vitest";
import { AuthContext } from "../../context/AuthContext";
import Register from "./Register";
import { orderService } from "../../api/orderService";

vi.mock("../../api/orderService", () => ({
  orderService: {
    createCustomer: vi.fn(),
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

describe("Register Component", () => {
  const login = vi.fn();
  const renderRegister = () => {
    render(
      <MemoryRouter>
        <AuthContext.Provider value={{ login }}>
          <Register />
        </AuthContext.Provider>
      </MemoryRouter>,
    );
  };

  beforeEach(() => {
    vi.clearAllMocks();
    renderRegister();
  });

  it("should render the Register page", () => {
    expect(screen.getByText("Create your MyShop account")).toBeInTheDocument();
    expect(screen.getByLabelText("Name")).toBeInTheDocument();
    expect(screen.getByLabelText("Phone Number")).toBeInTheDocument();
    expect(screen.getByLabelText("Address")).toBeInTheDocument();
    expect(screen.getByLabelText("Email")).toBeInTheDocument();
    expect(screen.getByLabelText("Password")).toBeInTheDocument();
    expect(screen.getByLabelText("Confirm Password")).toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: /register/i }),
    ).toBeInTheDocument();
    expect(screen.getByRole("link", { name: /login/i })).toBeInTheDocument();
  });

  it("should user able to enter name,phone number,address,email,password,confirm password", async () => {
    const user = userEvent.setup();

    const nameInput = screen.getByLabelText("Name");
    const phoneNumberInput = screen.getByLabelText("Phone Number");
    const addressInput = screen.getByLabelText("Address");
    const emailInput = screen.getByLabelText("Email");
    const passwordInput = screen.getByLabelText("Password");
    const confirmpasswordInput = screen.getByLabelText("Confirm Password");

    await user.type(nameInput, "John Doe");
    await user.type(phoneNumberInput, "9876543210");
    await user.type(addressInput, "Hyderabad, Telangana");
    await user.type(emailInput, "test@gmail.com");
    await user.type(passwordInput, "password123");
    await user.type(confirmpasswordInput, "password123");

    expect(nameInput).toHaveValue("John Doe");
    expect(phoneNumberInput).toHaveValue("9876543210");
    expect(addressInput).toHaveValue("Hyderabad, Telangana");
    expect(emailInput).toHaveValue("test@gmail.com");
    expect(passwordInput).toHaveValue("password123");
    expect(confirmpasswordInput).toHaveValue("password123");
  });

  it("should register button able to work when clicked", async () => {
    const user = userEvent.setup();

    const nameInput = screen.getByLabelText("Name");
    const phoneNumberInput = screen.getByLabelText("Phone Number");
    const addressInput = screen.getByLabelText("Address");
    const emailInput = screen.getByLabelText("Email");
    const passwordInput = screen.getByLabelText("Password");
    const confirmPasswordInput = screen.getByLabelText("Confirm Password");

    const registerButton = screen.getByRole("button", { name: /register/i });

    orderService.createCustomer.mockResolvedValue({
      data: {
        id: "550e8400-e29b-41d4-a716-446655440000",
        name: "Ashok Kumar",
        email: "ashok@gmail.com",
        phone: "9876543210",
        address: "Visakhapatnam, Andhra Pradesh",
      },
    });

    await user.type(nameInput, "Ashok Kumar");
    await user.type(phoneNumberInput, "9876543210");
    await user.type(addressInput, "Visakhapatnam, Andhra Pradesh");
    await user.type(emailInput, "ashok@gmail.com");
    await user.type(passwordInput, "Password@123");
    await user.type(confirmPasswordInput, "Password@123");

    await user.click(registerButton);

    expect(orderService.createCustomer).toHaveBeenCalledTimes(1);

    expect(orderService.createCustomer).toHaveBeenCalledWith({
      name: "Ashok Kumar",
      email: "ashok@gmail.com",
      phone: "9876543210",
      address: "Visakhapatnam, Andhra Pradesh",
      password: "Password@123",
    });
  });

  it("should navigate to profile after successful register", async () => {
    const user = userEvent.setup();

    orderService.createCustomer.mockResolvedValue({
      data: {
        id: "550e8400-e29b-41d4-a716-446655440000",
        name: "Ashok Kumar",
        email: "ashok@gmail.com",
        phone: "9876543210",
        address: "Visakhapatnam, Andhra Pradesh",
      },
    });

    await user.type(screen.getByLabelText("Name"), "Ashok Kumar");
    await user.type(screen.getByLabelText("Phone Number"), "9876543210");
    await user.type(
      screen.getByLabelText("Address"),
      "Visakhapatnam, Andhra Pradesh",
    );
    await user.type(screen.getByLabelText("Email"), "ashok@gmail.com");
    await user.type(screen.getByLabelText("Password"), "Password@123");
    await user.type(screen.getByLabelText("Confirm Password"), "Password@123");

    await user.click(screen.getByRole("button", { name: /register/i }));

    expect(navigate).toHaveBeenCalledWith("/profile");
  });

  it("should show an alert when passwords does not match", async () => {
    const user = userEvent.setup();

    const alertMock = vi.spyOn(window, "alert").mockImplementation(() => {});

    await user.type(screen.getByLabelText("Name"), "Ashok Kumar");
    await user.type(screen.getByLabelText("Phone Number"), "9876543210");
    await user.type(
      screen.getByLabelText("Address"),
      "Visakhapatnam, Andhra Pradesh",
    );
    await user.type(screen.getByLabelText("Email"), "ashok@gmail.com");
    await user.type(screen.getByLabelText("Password"), "Password@123");
    await user.type(screen.getByLabelText("Confirm Password"), "Password@2345");

    await user.click(screen.getByRole("button", { name: /register/i }));

    expect(alertMock).toHaveBeenCalledWith("Passwords do not match");

    expect(orderService.createCustomer).not.toHaveBeenCalled();

    alertMock.mockRestore();
  });

  it("should show an error alert when registration API fails", async () => {
    const user = userEvent.setup();

    const alertMock = vi.spyOn(window, "alert").mockImplementation(() => {});

    orderService.createCustomer.mockRejectedValue({
      response: {
        data: {
          message: "Email already exists",
        },
      },
    });

    await user.type(screen.getByLabelText("Name"), "Ashok Kumar");
    await user.type(screen.getByLabelText("Phone Number"), "9876543210");
    await user.type(
      screen.getByLabelText("Address"),
      "Visakhapatnam, Andhra Pradesh",
    );
    await user.type(screen.getByLabelText("Email"), "ashok@gmail.com");
    await user.type(screen.getByLabelText("Password"), "Password@123");
    await user.type(screen.getByLabelText("Confirm Password"), "Password@123");

    await user.click(screen.getByRole("button", { name: /register/i }));

    expect(alertMock).toHaveBeenCalledWith("Email already exists");

    alertMock.mockRestore();
  });
});
