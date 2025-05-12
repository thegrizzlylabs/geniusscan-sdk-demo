import React from "react";

const ColorInput = ({ attribute, config, onChange }) => {
  return (
    <div className="flex items-center space-x-2">
      <div className="w-10 h-10 border border-gray-200 rounded flex-shrink-0 overflow-hidden">
        <input
          type="color"
          value={config[attribute]}
          onChange={(e) => onChange({ target: { name: attribute, value: e.target.value } })}
          className="cursor-pointer -mx-2 -my-2 w-14 h-14"
        />
      </div>
      <input
        type="text"
        name={attribute}
        value={config[attribute].toUpperCase()}
        onChange={onChange}
        size={7}
        className="flex-1 border border-gray-200 rounded px-3 py-2 text-sm font-mono focus:outline-orange-400 focus:border-orange-400 focus:ring-orange-400"
        placeholder="#000000"
      />
    </div>
  );
};

export default ColorInput;
