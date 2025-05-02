import React from "react";

const Button = ({ children, target, href, onClick, className = "" }) => {
  const btnClasses = `cursor-pointer bg-orange-400 text-white px-4 py-2 rounded-md hover:bg-orange-500 transition-colors ${className}`;

  if (href) {
    return (
      <a href={href} target={target} className={btnClasses}>
        {children}
      </a>
    );
  } else {
    return (
      <button onClick={onClick} className={btnClasses}>
        {children}
      </button>
    );
  }
};

export default Button;
