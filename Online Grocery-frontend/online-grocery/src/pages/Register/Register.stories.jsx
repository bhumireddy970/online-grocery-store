import { MemoryRouter } from "react-router-dom";
import { AuthContext } from "../../context/AuthContext";
import Register from "./Register";

export default {
  title: "Pages/Register",
  component: Register,
  tags: ["autodocs"],

  decorators: [
    (Story) => (
      <MemoryRouter>
        <AuthContext.Provider value={{ login: () => {} }}>
          <Story />
        </AuthContext.Provider>
      </MemoryRouter>
    ),
  ],
};

export const Default = {};
