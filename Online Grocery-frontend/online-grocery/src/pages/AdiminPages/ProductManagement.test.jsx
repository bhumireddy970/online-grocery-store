import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { describe, expect } from "vitest";
import ProductManagement from "./ProductManagement";
import { productService } from "../../api/productService";
import { inventoryService } from "../../api/inventoryService";

vi.mock("../../api/productService", () => ({
  productService: {
    getAllProducts: vi.fn(),
    addProduct: vi.fn(),
    updateProduct: vi.fn(),
    deleteProduct: vi.fn(),
  },
}));

vi.mock("../../api/inventoryService", () => ({
  inventoryService: {
    getInventoryByProductId: vi.fn(),
  },
}));

describe("ProductManagement Component", () => {
  const products = [
    {
      id: "1",
      sku: "SKU001",
      name: "Apple",
      price: 100,
      description: "Fresh Apple",
      active: true,
      categoryId: "cat1",
    },
    {
      id: "2",
      sku: "SKU002",
      name: "Milk",
      price: 50,
      description: "Fresh Milk",
      active: true,
      categoryId: "cat2",
    },
  ];

  const renderProductManagement = () => {
    render(
      <MemoryRouter>
        <ProductManagement />
      </MemoryRouter>,
    );
  };

  beforeEach(() => {
    vi.clearAllMocks();

    productService.getAllProducts.mockResolvedValue({ data: [] });

    inventoryService.getInventoryByProductId.mockResolvedValue({
      data: {
        availableQuantity: 10,
        reservedQuantity: 2,
      },
    });
  });

  it("should render Product Management page", async () => {
    renderProductManagement();

    expect(screen.getByText("Add New Product")).toBeInTheDocument();
    expect(screen.getByPlaceholderText("SKU")).toBeInTheDocument();
    expect(screen.getByPlaceholderText("Product Name")).toBeInTheDocument();
    expect(screen.getByPlaceholderText("Price")).toBeInTheDocument();
    expect(screen.getByPlaceholderText("Description")).toBeInTheDocument();
    expect(screen.getByPlaceholderText("Category ID")).toBeInTheDocument();
    expect(screen.getByPlaceholderText("Initial Qty")).toBeInTheDocument();

    expect(
      screen.getByRole("button", { name: /add product/i }),
    ).toBeInTheDocument();

    expect(screen.getByRole("button", { name: /clear/i })).toBeInTheDocument();
  });
});
