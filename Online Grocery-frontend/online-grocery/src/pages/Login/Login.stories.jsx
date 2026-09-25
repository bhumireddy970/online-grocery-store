import { MemoryRouter } from "react-router-dom";
import { AuthContext } from "../../context/AuthContext";
import Login from "./Login";

export default {
  title: "Pages/Login",
  component: Login,
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
