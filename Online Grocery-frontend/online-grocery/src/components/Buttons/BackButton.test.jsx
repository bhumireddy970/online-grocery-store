import { render, screen } from "@testing-library/react";
import { describe, it, expect } from "vitest";
import BackButton from "./BackButton";

describe("BackButton", () => {
  it("should render Back button", () => {
    render(<BackButton />);

    expect(
      screen.getByRole("button", { name: /back/i })
    ).toBeInTheDocument();
  });
});