import type { Preview } from "@storybook/react";
import "../src/theme.css";

const preview: Preview = {
    parameters: {
        backgrounds: {
            default: "light",
            values: [
                { name: "light", value: "#ffffff" },
                { name: "dark", value: "#0a0a0a" },
            ],
        },
    },
};

export default preview;
