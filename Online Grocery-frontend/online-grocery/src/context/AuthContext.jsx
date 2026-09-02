import { createContext, useState, useMemo } from "react";

export const AuthContext = createContext();

const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    const savedData = localStorage.getItem("store_user");
    return savedData ? JSON.parse(savedData) : null;
  });
  const [categoryId, setCategoryId] = useState(() => {
    const savedData = localStorage.getItem("store_categoryId");
    return savedData ? JSON.parse(savedData) : null;
  });

  const login = (userDetails) => {
    setUser(userDetails);
    localStorage.setItem("store_user", JSON.stringify(userDetails));
  };

  const logout = () => {
    if (user?.id) {
      localStorage.removeItem(`cart_${user.id}`);
    }
    setUser(null);
    localStorage.removeItem("store_user");
  };

  const value = useMemo(
    () => ({
      user,
      login,
      logout,
      categoryId,
      setCategoryId,
    }),
    [user, categoryId],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export default AuthProvider;
