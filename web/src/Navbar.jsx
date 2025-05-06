import React, { useState, useEffect, useRef } from "react";
import { List, X } from "@phosphor-icons/react";
import Button from "./components/Button";
import Logo from "./Logo";

const Navbar = ({ links }) => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const menuRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setIsMenuOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  return (
    <header className="bg-gray-50 border-b border-gray-200">
      <div className="max-w-6xl mx-auto px-4 py-4">
        <div className="flex justify-between items-center">
          <div className="flex items-center">
            <Logo className="w-8 h-8" />
            <span className="ml-3 text-xl text-[#EA580C]">Genius Scan SDK</span>
          </div>

          <div>
            {/* Desktop navigation */}
            <nav className="hidden md:flex md:items-center">
              {links.map(({ href, label, isButton }) =>
                isButton ? (
                  <Button key={href} href={href} target="_blank" className="ml-4">
                    {label}
                  </Button>
                ) : (
                  <a key={href} href={href} target="_blank" className="text-gray-500 hover:text-gray-900 mx-4">
                    {label}
                  </a>
                ),
              )}
            </nav>

            {/* Mobile menu button */}
            <div className="md:hidden">
              <button
                onClick={() => setIsMenuOpen(!isMenuOpen)}
                className="p-2 rounded-md text-gray-500 hover:text-gray-900 hover:bg-gray-100"
              >
                {isMenuOpen ? <X className="h-6 w-6" /> : <List className="h-6 w-6" />}
              </button>
            </div>
          </div>
        </div>
      </div>
      {/* Mobile navigation dropdown */}
      {isMenuOpen && (
        <nav ref={menuRef} className="absolute w-screen bg-white shadow-lg border-t border-gray-200 py-2 z-50">
          {links.map(({ href, label, isButton }) =>
            isButton ? (
              <div key={href} className="px-4 py-2">
                <Button href={href} target="_blank" className="w-full">
                  {label}
                </Button>
              </div>
            ) : (
              <a
                key={href}
                href={href}
                target="_blank"
                className="block px-4 py-2 text-gray-500 hover:text-gray-900 hover:bg-gray-50"
              >
                {label}
              </a>
            ),
          )}
        </nav>
      )}
    </header>
  );
};

export default Navbar;
