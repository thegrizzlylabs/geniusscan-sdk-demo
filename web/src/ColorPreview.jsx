import React from "react";

const ColorPreview = ({ config }) => (
  <div className="p-4 rounded shadow-md" style={{ backgroundColor: config.backgroundColor }}>
    <div className="flex items-center justify-between mb-4">
      <span style={{ color: config.foregroundColor }}>Preview Text</span>
      <div className="flex space-x-2">
        <div className="p-2 rounded" style={{ backgroundColor: config.highlightColor }}></div>
      </div>
    </div>
    <div className="border border-dashed p-4 rounded" style={{ borderColor: config.foregroundColor }}>
      <div className="text-center" style={{ color: config.foregroundColor }}>
        Document Scan Area
      </div>
    </div>
  </div>
);

export default ColorPreview;
