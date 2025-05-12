import React from "react";
import { LANGUAGES, FILTERS, ACTIONS } from "@thegrizzlylabs/web-geniusscan-sdk";
import { ClipboardText } from "@phosphor-icons/react";

import ColorInput from "./ColorInput";
import Checkbox from "./components/Checkbox";
import Select from "./components/Select";
import Card from "./components/Card";
import { howToConfigUrl } from "./Root";

export const DEFAULT_CONFIG = {
  multiPage: true,
  showFps: false,
  defaultFilter: "automatic",
  availableFilters: FILTERS,
  jpegQuality: 60,
  postProcessingActions: ACTIONS,
  foregroundColor: "#ffffff",
  backgroundColor: "#000000",
  highlightColor: "#6495ED",
  language: "en",
};

const LANGUAGE_LABELS = {
  ar: "Arabic",
  da: "Danish",
  de: "German",
  en: "English",
  es: "Spanish",
  fr: "French",
  he: "Hebrew",
  hu: "Hungarian",
  id: "Indonesian",
  it: "Italian",
  ja: "Japanese",
  ko: "Korean",
  nl: "Dutch",
  pl: "Polish",
  pt: "Portuguese",
  ru: "Russian",
  sv: "Swedish",
  tr: "Turkish",
  vi: "Vietnamese",
  "zh-TW": "Traditional Chinese",
  "zh-CN": "Simplified Chinese",
};

const ACTION_LABELS = {
  rotate: "Rotate",
  editFilter: "Edit filter",
  correctDistortion: "Correct distortion",
};

const ConfigurationForm = ({ customConfig, config, setConfig }) => {
  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;

    if (type === "checkbox") {
      if (name.startsWith("filter-")) {
        const filterName = name.replace("filter-", "");
        const filtersSet = new Set(config.availableFilters);
        checked ? filtersSet.add(filterName) : filtersSet.delete(filterName);
        setConfig({ ...config, availableFilters: FILTERS.filter((filter) => filtersSet.has(filter)) });
      }
      if (name.startsWith("action-")) {
        const actionName = name.replace("action-", "");
        const actionsSet = new Set(config.postProcessingActions);

        checked ? actionsSet.add(actionName) : actionsSet.delete(actionName);
        setConfig({ ...config, postProcessingActions: ACTIONS.filter((action) => actionsSet.has(action)) });
      }
    } else {
      setConfig({ ...config, [name]: value });
    }
  };

  const languageOptions = LANGUAGES.map((lang) => ({
    value: lang,
    label: LANGUAGE_LABELS[lang] || lang,
  })).sort((a, b) => a.label.localeCompare(b.label));

  const filterOptions = config.availableFilters.map((filter) => ({
    value: filter,
    label: filter.replace(/_/g, " ").replace(/\b\w/g, (l) => l.toUpperCase()),
  }));

  const handleCopy = () => {
    navigator.clipboard.writeText(JSON.stringify(customConfig, null, 2));
  };

  return (
    <div className="space-y-4">
      {/* General Settings */}
      <Card>
        <h3 className="text-base font-semibold mb-4">General Settings</h3>
        <div className="space-y-4">
          <div className="flex items-center space-x-4">
            <Checkbox
              name="multiPage"
              checked={config.multiPage}
              onChange={(e) => setConfig({ ...config, multiPage: e.target.checked })}
              label="Allow multi-page scans"
            />
            <Checkbox
              name="showFps"
              checked={config.showFps}
              onChange={(e) => setConfig({ ...config, showFps: e.target.checked })}
              label="Display FPS"
            />
          </div>
          <div>
            <label className="block text-sm mb-1">Language</label>
            <Select name="language" value={config.language} onChange={handleChange} options={languageOptions} />
          </div>
        </div>
      </Card>

      {/* Post-Processing Settings */}
      <Card>
        <h3 className="text-base font-semibold mb-4">Post-Processing Settings</h3>
        <div className="space-y-6">
          <div>
            <label className="block mb-2 text-sm font-medium">Available filters</label>
            <div className="grid grid-cols-2 gap-2">
              {FILTERS.map((filter) => (
                <Checkbox
                  key={filter}
                  name={`filter-${filter}`}
                  checked={config.availableFilters.includes(filter)}
                  onChange={handleChange}
                  label={filter.replace(/_/g, " ").replace(/\b\w/g, (l) => l.toUpperCase())}
                />
              ))}
            </div>
          </div>
          <div>
            <label className="block mb-2 text-sm font-medium">Default filter</label>
            <Select name="defaultFilter" value={config.defaultFilter} onChange={handleChange} options={filterOptions} />
          </div>
          <div>
            <label className="block mb-2 text-sm font-medium">Post-processing actions</label>
            <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-2 xl:grid-cols-3 gap-4">
              {ACTIONS.map((action) => (
                <Checkbox
                  key={action}
                  name={`action-${action}`}
                  checked={config.postProcessingActions.includes(action)}
                  onChange={handleChange}
                  label={ACTION_LABELS[action] || action}
                />
              ))}
            </div>
          </div>
          <div>
            <label htmlFor="jpegQuality" className="block mb-2">
              JPEG Quality: {config.jpegQuality}
            </label>
            <div className="flex items-center space-x-2">
              <span className="text-xs">0</span>
              <input
                type="range"
                id="jpegQuality"
                name="jpegQuality"
                min="0"
                max="100"
                value={config.jpegQuality}
                onChange={({ target: { value } }) => setConfig({ ...config, jpegQuality: parseInt(value) })}
                className="cursor-pointer w-full accent-orange-400"
              />
              <span className="text-xs">100</span>
            </div>
          </div>
        </div>
      </Card>

      {/* Color Configuration */}
      <Card>
        <h3 className="text-base font-semibold mb-4">Color Configuration</h3>
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div>
            <label className="block mb-2 text-sm font-medium">Foreground</label>
            <ColorInput attribute="foregroundColor" config={config} onChange={handleChange} />
          </div>
          <div>
            <label className="block mb-2 text-sm font-medium">Background</label>
            <ColorInput attribute="backgroundColor" config={config} onChange={handleChange} />
          </div>
          <div>
            <label className="block mb-2 text-sm font-medium">Highlight</label>
            <ColorInput attribute="highlightColor" config={config} onChange={handleChange} />
          </div>
        </div>
      </Card>

      {/* Custom Configuration */}
      {customConfig && (
        <Card bg="bg-gray-50">
          <div className="flex justify-between items-center mb-4">
            <h3 className="text-base font-semibold">Custom Configuration</h3>
            <a href={howToConfigUrl} target="_blank" className="text-orange-400 text-sm hover:text-orange-500">
              How to use?
            </a>
          </div>
          <pre className="font-mono text-sm overflow-auto whitespace-pre-wrap">
            {JSON.stringify(customConfig, null, 2)}
          </pre>
          <div className="flex justify-end">
            <button
              onClick={handleCopy}
              className="cursor-pointer bottom-3 bg-white rounded-md px-4 py-2 border border-gray-200 right-3 flex items-center text-gray-500 hover:text-gray-900"
            >
              <ClipboardText className="w-4 h-4 mr-1" />
              Copy
            </button>
          </div>
        </Card>
      )}
    </div>
  );
};

export default ConfigurationForm;
