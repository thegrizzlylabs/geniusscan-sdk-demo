import React from "react";
import { CheckFat } from "@phosphor-icons/react";

const Checkbox = ({ label, name, checked, onChange, className = "" }) => {
  return (
    <label className={`cursor-pointer inline-flex items-center text-sm ${className}`}>
      <div className="relative w-4 h-4">
        <input
          type="checkbox"
          name={name}
          checked={checked}
          onChange={onChange}
          className="peer absolute opacity-0 w-full h-full cursor-pointer"
        />
        <div
          className={`flex items-center justify-center w-4 h-4 border border-gray-300 rounded transition-colors ${checked ? "bg-orange-400 border-orange-400" : "bg-white"}`}
        >
          {checked && <CheckFat className="w-4/5 h-4/5 text-white" weight="fill" />}
        </div>
      </div>
      {label && <span className="ml-2">{label}</span>}
    </label>
  );
};

export default Checkbox;
