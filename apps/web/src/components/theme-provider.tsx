import type { ReactNode } from "react";
import { ThemeContext, useThemeState } from "../hooks/use-theme";

interface ThemeProviderProps {
    children: ReactNode;
}

export function ThemeProvider({ children }: ThemeProviderProps) {
    const themeState = useThemeState();

    return <ThemeContext.Provider value={themeState}>{children}</ThemeContext.Provider>;
}
