import Root from "./Root";
import { createRoot } from "react-dom/client";
import { createElement } from "react";

import "./styles.css";

const root = createRoot(document.getElementById("root"));
root.render(createElement(Root));
