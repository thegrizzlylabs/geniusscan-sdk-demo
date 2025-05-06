import React from "react";
import { CaretDown } from "@phosphor-icons/react";
const Select = ({ value, onChange, options, name }) => {
  return (
    <div className="relative">
      <select
        name={name}
        value={value}
        onChange={onChange}
        className="w-full border border-gray-200 rounded px-3 py-2 pr-8 appearance-none focus:outline-orange-400 focus:border-orange-400 focus:ring-orange-400 text-sm"
      >
        {options.map(({ value, label }) => (
          <option key={value} value={value}>
            {label}
          </option>
        ))}
      </select>
      <div className="pointer-events-none absolute right-2 top-1/2 -translate-y-1/2">
        <CaretDown className="text-gray-400" />
      </div>
    </div>
  );
};

export default Select;
