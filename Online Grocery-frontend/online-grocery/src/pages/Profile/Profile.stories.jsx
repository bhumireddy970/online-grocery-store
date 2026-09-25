import { AuthContext } from "../../context/AuthContext";
import Profile from "./Profile";

const User = {
  id: 1,
  name: "Saradhi",
  phone: "9876543210",
  address: "Kadapa, Andhra Pradesh",
};

export default {
  title: "Pages/Profile",
  component: Profile,
  tags: ["autodocs"],
};

export const LoggedIn = {
  decorators: [
    (Story) => (
      <AuthContext.Provider value={{ user: User, login: () => {} }}>
        <Story />
      </AuthContext.Provider>
    ),
  ],
};

export const NotLoggedIn = {
  decorators: [
    (Story) => (
      <AuthContext.Provider value={{ user: null, login: () => {} }}>
        <Story />
      </AuthContext.Provider>
    ),
  ],
};
