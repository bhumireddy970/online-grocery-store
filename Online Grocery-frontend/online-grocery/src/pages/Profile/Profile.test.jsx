import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { AuthContext } from "../../context/AuthContext";
import { orderService } from "../../api/orderService";
import Profile from "./Profile";
import { describe, it, expect, vi, beforeEach } from "vitest";

vi.mock("../../api/orderService", () => ({
  orderService: {
    updateCustomerProfile: vi.fn(),
  },
}));

describe("Profile Component", () => {
  const user = {
    id: "550e8400-e29b-41d4-a716-446655440000",
    name: "Ashok Kumar",
    email: "ashok@gmail.com",
    phone: "9876543210",
    address: "Visakhapatnam, Andhra Pradesh",
  };

  const login = vi.fn();

  const renderProfile = (userData = user) => {
    render(
      <AuthContext.Provider value={{ user: userData, login }}>
        <Profile />
      </AuthContext.Provider>,
    );
  };

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should show login message when user is not logged in", () => {
    renderProfile(null);

    expect(
      screen.getByText("Please login to view your profile"),
    ).toBeInTheDocument();
  });

  it("should display user profile information", () => {
    renderProfile();

    expect(screen.getByText("My Profile")).toBeInTheDocument();

    expect(
      screen.getByText("Manage your personal information"),
    ).toBeInTheDocument();

    expect(screen.getByText("Ashok Kumar")).toBeInTheDocument();

    expect(screen.getByText("ashok@gmail.com")).toBeInTheDocument();

    expect(screen.getByText("9876543210")).toBeInTheDocument();

    expect(
      screen.getByText("Visakhapatnam, Andhra Pradesh"),
    ).toBeInTheDocument();

    expect(
      screen.getByRole("button", {
        name: /edit profile/i,
      }),
    ).toBeInTheDocument();
  });

  it("should show editable fields when Edit Profile is clicked", async () => {
    const userEventInstance = userEvent.setup();

    renderProfile();

    await userEventInstance.click(
      screen.getByRole("button", {
        name: /edit profile/i,
      }),
    );

    expect(screen.getByLabelText("Name")).toBeInTheDocument();

    expect(screen.getByLabelText("Email")).toBeInTheDocument();

    expect(screen.getByLabelText("Phone")).toBeInTheDocument();

    expect(screen.getByLabelText("Address")).toBeInTheDocument();

    expect(
      screen.getByRole("button", {
        name: /save changes/i,
      }),
    ).toBeInTheDocument();

    expect(
      screen.getByRole("button", {
        name: /cancel/i,
      }),
    ).toBeInTheDocument();
  });

  it("should allow user to edit profile information", async () => {
    const userEventInstance = userEvent.setup();

    renderProfile();

    await userEventInstance.click(
      screen.getByRole("button", {
        name: /edit profile/i,
      }),
    );

    const nameInput = screen.getByLabelText("Name");
    const emailInput = screen.getByLabelText("Email");
    const phoneInput = screen.getByLabelText("Phone");
    const addressInput = screen.getByLabelText("Address");

    await userEventInstance.clear(nameInput);
    await userEventInstance.type(nameInput, "John Doe");

    await userEventInstance.clear(emailInput);
    await userEventInstance.type(emailInput, "john@gmail.com");

    await userEventInstance.clear(phoneInput);
    await userEventInstance.type(phoneInput, "9999999999");

    await userEventInstance.clear(addressInput);
    await userEventInstance.type(addressInput, "Hyderabad, Telangana");

    expect(nameInput).toHaveValue("John Doe");

    expect(emailInput).toHaveValue("john@gmail.com");

    expect(phoneInput).toHaveValue("9999999999");

    expect(addressInput).toHaveValue("Hyderabad, Telangana");
  });

  it("should show success alert after profile update", async () => {
    const userEventInstance = userEvent.setup();

    const alertMock = vi.spyOn(window, "alert").mockImplementation(() => {});

    renderProfile();

    await userEventInstance.click(
      screen.getByRole("button", {
        name: /edit profile/i,
      }),
    );

    orderService.updateCustomerProfile.mockResolvedValue({ data: user });

    await userEventInstance.click(
      screen.getByRole("button", {
        name: /save changes/i,
      }),
    );

    expect(alertMock).toHaveBeenCalledWith("Profile updated successfully");

    alertMock.mockRestore();
  });

  it("should show failure alert when profile update fails", async () => {
    const userEventInstance = userEvent.setup();

    const alertMock = vi.spyOn(window, "alert").mockImplementation(() => {});

    orderService.updateCustomerProfile.mockRejectedValue(
      new Error("Update failed"),
    );

    renderProfile();

    await userEventInstance.click(
      screen.getByRole("button", { name: /edit profile/i }),
    );

    await userEventInstance.click(
      screen.getByRole("button", { name: /save changes/i }),
    );

    expect(alertMock).toHaveBeenCalled("Failed to update profile");

    alertMock.mockRestore();
  });
});
