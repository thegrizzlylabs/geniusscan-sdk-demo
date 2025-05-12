import React, { useState, useEffect, useMemo } from "react";
import { scanDocument, MissingLicenseKeyError } from "@thegrizzlylabs/web-geniusscan-sdk";
import { Warning } from "@phosphor-icons/react";

import ConfigurationForm, { DEFAULT_CONFIG } from "./ConfigurationForm";
import ScanViewer from "./ScanViewer";
import Navbar from "./Navbar";
import Button from "./components/Button";

const initialState = localStorage.getItem("config") ? JSON.parse(localStorage.getItem("config")) : DEFAULT_CONFIG;

const quoteRequestUrl =
  "https://sdk.geniusscan.com/quote_request?utm_source=sdk_website&utm_campaign=demo&utm_medium=website&utm_term=Request%20a%20quote&utm_content=quote_request";
const startFreeTrialUrl =
  "https://sdk.geniusscan.com/users/sign_up?utm_source=sdk_website&utm_campaign=demo&utm_medium=website&utm_term=Start%20your%20free%20trial&utm_content=account_creation";
const documentationUrl = "https://www.npmjs.com/package/@thegrizzlylabs/web-geniusscan-sdk";
export const howToConfigUrl =
  "https://www.npmjs.com/package/@thegrizzlylabs/web-geniusscan-sdk#user-content-scan-configuration";

const navigationLinks = [
  { href: documentationUrl, label: "Documentation" },
  { href: quoteRequestUrl, label: "Request a quote" },
  { href: startFreeTrialUrl, label: "Start your free trial", isButton: true },
];

const Root = () => {
  // Default configuration as per documentation
  const [config, setConfig] = useState(initialState);
  // Get shortened config (only differences from default)
  const customConfig = useMemo(() => {
    let short = null;

    // Only include config keys that are different from DEFAULT_CONFIG
    for (const key in config) {
      if (JSON.stringify(config[key]) !== JSON.stringify(DEFAULT_CONFIG[key])) {
        if (!short) {
          short = {};
        }
        short[key] = config[key];
      }
    }
    return short;
  });

  const [error, setError] = useState(null);
  const [jpegs, setJpegs] = useState([]);

  useEffect(() => {
    localStorage.setItem("config", JSON.stringify(config));
  }, [config]);

  // Handle form submission
  const handleSubmit = async (e) => {
    setError(null);
    e.preventDefault();
    try {
      const scannedJpegs = await scanDocument(config);
      setJpegs(scannedJpegs);
    } catch (error) {
      setError(error);
    }
  };

  const resetConfig = () => {
    setConfig(DEFAULT_CONFIG);
  };

  return (
    <div className="min-h-screen bg-white">
      <Navbar links={navigationLinks} />

      {/* Main Content */}
      <main className="max-w-6xl mx-auto px-4 py-8">
        <div className="text-center mb-8">
          <h1 className="mb-2 flex items-center justify-center">
            <span className="text-2xl font-medium">Genius Scan Web SDK</span>
            <div className="ml-2 bg-gray-100 border border-gray-300 rounded px-2 py-1 text-sm text-gray-500 uppercase">
              DEMO
            </div>
          </h1>
          <p className="text-gray-500">
            Test the Web document scanner and generate your config code to add the SDK into your mobile apps.
          </p>
        </div>

        {/* Demo Warning */}
        {error && (
          <div className="max-w-3xl m-auto mb-4 p-4 border border-red-500 rounded  flex items-start">
            <Warning className="w-5 h-5 mr-2 mt-0.5 flex-shrink-0 text-red-500" />
            <p>
              {error instanceof MissingLicenseKeyError
                ? "On a demo mode and without a license key, the scanning session is limited to 1 minute. Please reload the page to start a new scanning session."
                : error.message}
            </p>
          </div>
        )}

        <div className="text-center mb-8">
          {error ? (
            <Button onClick={() => window.location.reload()}>Reload page to start scanning</Button>
          ) : (
            <Button onClick={handleSubmit}>Scan document</Button>
          )}
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Scanned Documents - First on mobile but right-side on desktop */}
          <div className="lg:order-2">
            <h2 className="text-xl font-medium mb-4">Scanned Documents</h2>
            <div className="bg-white rounded-md border border-gray-200 p-6">
              {jpegs.length > 0 ? (
                <ScanViewer jpegs={jpegs} />
              ) : (
                <div className="min-h-[200px] flex items-center justify-center">
                  <p className="text-sm text-gray-500">No document scanned yet!</p>
                </div>
              )}
            </div>
          </div>

          {/* SDK Configuration - Second on mobile */}
          <div className="lg:order-1">
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-medium">SDK Configuration</h2>
              <button
                onClick={resetConfig}
                className="cursor-pointer text-orange-400 hover:text-orange-500 text-sm font-medium"
              >
                Reset
              </button>
            </div>

            <ConfigurationForm
              customConfig={customConfig}
              config={config}
              setConfig={setConfig}
              onSubmit={handleSubmit}
            />
          </div>
        </div>
      </main>

      {/* Footer */}
      <footer className="bg-gray-50 border-t border-gray-200">
        <div className="max-w-6xl mx-auto px-4 py-4">
          <div className="flex justify-between items-center text-sm text-gray-500">
            <div>Genius Scan SDK by The Grizzly Labs</div>
            <div className="space-x-8">
              <a href="https://geniusscansdk.com/license/terms" target="_blank" className="hover:text-gray-900">
                Terms
              </a>
              <a href="mailto:Genius Scan SDK <sdk@geniusscan.com>" target="_blank" className="hover:text-gray-900">
                Contact
              </a>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default Root;
