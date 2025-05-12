import React from "react";

const Card = ({ children, bg = "bg-white" }) => {
  return <div className={`${bg} rounded-md border border-gray-200 px-5 py-4`}>{children}</div>;
};

export default Card;
