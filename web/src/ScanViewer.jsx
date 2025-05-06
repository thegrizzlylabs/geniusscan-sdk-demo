import React from "react";
import { MagnifyingGlassPlus } from "@phosphor-icons/react";

const ScanViewer = ({ jpegs }) => {
  return (
    <div className="grid grid-cols-3 gap-4">
      {jpegs.map((jpeg, index) => (
        <a
          href={URL.createObjectURL(new Blob([jpeg], { type: "image/jpeg" }))}
          target="_blank"
          key={index}
          className="relative group"
        >
          <img
            src={URL.createObjectURL(new Blob([jpeg], { type: "image/jpeg" }))}
            alt={`Scanned document ${index + 1}`}
            className="w-full h-auto rounded border border-gray-200"
            data-testid="scanned-image"
          />
          <MagnifyingGlassPlus size="1.5rem" className="absolute top-2 right-2 p-1 bg-white rounded shadow-sm" />
        </a>
      ))}
    </div>
  );
};

export default ScanViewer;
