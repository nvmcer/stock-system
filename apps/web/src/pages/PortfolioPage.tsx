import { useEffect, useState } from "react";
import type { ChangeEvent, FormEvent } from "react";
import ReactMarkdown from "react-markdown";
import api from "../services/api";
import type { ApiResponse, PortfolioAiReport, PortfolioAiReportRequest, PortfolioItem } from "../services/types";

const CUSTOM_PROVIDER = "Custom";
const CUSTOM_MODEL = "__custom_model__";

const PROVIDER_OPTIONS = [
  {
    label: "OpenAI",
    baseUrl: "https://api.openai.com/v1",
    models: [
      { value: "gpt-4.1-mini", label: "GPT-4.1 Mini" },
      { value: "gpt-4.1", label: "GPT-4.1" },
      { value: "gpt-4o-mini", label: "GPT-4o Mini" },
      { value: CUSTOM_MODEL, label: "Custom model" },
    ],
  },
  {
    label: "DeepSeek",
    baseUrl: "https://api.deepseek.com/v1",
    models: [
      { value: "deepseek-chat", label: "DeepSeek Chat" },
      { value: "deepseek-reasoner", label: "DeepSeek Reasoner" },
      { value: CUSTOM_MODEL, label: "Custom model" },
    ],
  },
  {
    label: "OpenRouter",
    baseUrl: "https://openrouter.ai/api/v1",
    models: [
      { value: "openai/gpt-4.1-mini", label: "GPT-4.1 Mini" },
      { value: "anthropic/claude-3.7-sonnet", label: "Claude 3.7 Sonnet" },
      { value: "google/gemini-2.5-pro-preview", label: "Gemini 2.5 Pro" },
      { value: CUSTOM_MODEL, label: "Custom model" },
    ],
  },
  {
    label: "Moonshot",
    baseUrl: "https://api.moonshot.cn/v1",
    models: [
      { value: "moonshot-v1-8k", label: "Moonshot 8K" },
      { value: "moonshot-v1-32k", label: "Moonshot 32K" },
      { value: CUSTOM_MODEL, label: "Custom model" },
    ],
  },
  {
    label: "SiliconFlow",
    baseUrl: "https://api.siliconflow.cn/v1",
    models: [
      { value: "deepseek-ai/DeepSeek-V3", label: "DeepSeek V3" },
      { value: "Qwen/Qwen2.5-72B-Instruct", label: "Qwen 2.5 72B" },
      { value: CUSTOM_MODEL, label: "Custom model" },
    ],
  },
  {
    label: CUSTOM_PROVIDER,
    baseUrl: "",
    models: [{ value: CUSTOM_MODEL, label: "Custom model" }],
  },
] as const;

function getErrorMessage(err: unknown) {
  const error = err as { response?: { data?: { message?: string } }, message?: string };
  return error.response?.data?.message || error.message || "Unknown error";
}

function formatCurrency(value: number) {
  return `$${value.toFixed(2)}`;
}

function PortfolioPage() {
  const defaultProvider = PROVIDER_OPTIONS[0];
  const [portfolio, setPortfolio] = useState<PortfolioItem[]>([]);
  const [isLoadingPortfolio, setIsLoadingPortfolio] = useState(true);
  const [isLoadingSavedReport, setIsLoadingSavedReport] = useState(true);
  const [portfolioError, setPortfolioError] = useState<string | null>(null);
  const [reportError, setReportError] = useState<string | null>(null);
  const [isGeneratingReport, setIsGeneratingReport] = useState(false);
  const [report, setReport] = useState<PortfolioAiReport | null>(null);
  const [form, setForm] = useState<Omit<PortfolioAiReportRequest, "baseUrl" | "model">>({
    provider: defaultProvider.label,
    apiKey: "",
  });
  const [selectedModel, setSelectedModel] = useState<string>(defaultProvider.models[0].value);
  const [customModel, setCustomModel] = useState("");
  const [customBaseUrl, setCustomBaseUrl] = useState("");

  const totalProfit = portfolio.reduce((sum, item) => sum + Number(item.totalProfit), 0);
  const totalMarketValue = portfolio.reduce((sum, item) => sum + Number(item.currentPrice) * item.quantity, 0);
  const activeProvider = PROVIDER_OPTIONS.find((option) => option.label === form.provider) ?? PROVIDER_OPTIONS[PROVIDER_OPTIONS.length - 1];
  const effectiveBaseUrl = form.provider === CUSTOM_PROVIDER ? customBaseUrl.trim() : activeProvider.baseUrl;
  const effectiveModel = selectedModel === CUSTOM_MODEL ? customModel.trim() : selectedModel;

  useEffect(() => {
    const fetchPortfolio = async () => {
      setIsLoadingPortfolio(true);
      setPortfolioError(null);

      try {
        const res = await api.get<ApiResponse<PortfolioItem[]>>("/api/portfolio");
        if (res.data.success) {
          setPortfolio(res.data.data || []);
        } else {
          setPortfolioError("Failed to get portfolio: " + res.data.message);
        }
      } catch (err) {
        setPortfolioError("Failed to get portfolio: " + getErrorMessage(err));
      } finally {
        setIsLoadingPortfolio(false);
      }
    };

    const fetchLatestReport = async () => {
      setIsLoadingSavedReport(true);

      try {
        const res = await api.get<ApiResponse<PortfolioAiReport | null>>("/api/portfolio/analysis-report/latest");
        if (res.data.success) {
          setReport(res.data.data ?? null);
        } else {
          setReportError("Failed to load latest saved report: " + res.data.message);
        }
      } catch (err) {
        setReportError("Failed to load latest saved report: " + getErrorMessage(err));
      } finally {
        setIsLoadingSavedReport(false);
      }
    };

    void fetchPortfolio();
    void fetchLatestReport();
  }, []);

  const handleProviderChange = (event: ChangeEvent<HTMLSelectElement>) => {
    const nextProvider = event.target.value;
    const preset = PROVIDER_OPTIONS.find((option) => option.label === nextProvider);

    setForm((current) => ({
      ...current,
      provider: nextProvider,
    }));
    setSelectedModel(preset?.models[0]?.value ?? CUSTOM_MODEL);
  };

  const handleCustomBaseUrlChange = (event: ChangeEvent<HTMLInputElement>) => {
    setCustomBaseUrl(event.target.value);
  };

  const handleModelSelectChange = (event: ChangeEvent<HTMLSelectElement>) => {
    setSelectedModel(event.target.value);
  };

  const handleCustomModelChange = (event: ChangeEvent<HTMLInputElement>) => {
    setCustomModel(event.target.value);
  };

  const handleApiKeyChange = (event: ChangeEvent<HTMLInputElement>) => {
    setForm((current) => ({ ...current, apiKey: event.target.value }));
  };

  const handleGenerateReport = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setReportError(null);

    if (!effectiveBaseUrl) {
      setReportError("Please provide a Base URL for the selected provider.");
      return;
    }
    if (!effectiveModel) {
      setReportError("Please choose a model or enter a custom model name.");
      return;
    }

    setIsGeneratingReport(true);

    try {
      const payload: PortfolioAiReportRequest = {
        provider: form.provider,
        baseUrl: effectiveBaseUrl,
        model: effectiveModel,
        apiKey: form.apiKey,
      };

      const res = await api.post<ApiResponse<PortfolioAiReport>>("/api/portfolio/analysis-report", payload);
      if (res.data.success) {
        setReport(res.data.data);
      } else {
        setReportError("Failed to generate report: " + res.data.message);
      }
    } catch (err) {
      setReportError("Failed to generate report: " + getErrorMessage(err));
    } finally {
      setIsGeneratingReport(false);
    }
  };

  return (
    <div>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "20px" }}>
        <h2 style={{ margin: 0 }}>My Stock Portfolio</h2>
        <div style={{ display: "flex", gap: "12px", flexWrap: "wrap", justifyContent: "flex-end" }}>
          <div style={{ padding: "12px 16px", background: "rgba(99,102,241,0.1)", borderRadius: "8px", color: "#c7d2fe" }}>
            <strong>Holdings Value: {formatCurrency(totalMarketValue)}</strong>
          </div>
          <div style={{ padding: "12px 16px", background: totalProfit >= 0 ? "rgba(34,197,94,0.1)" : "rgba(239,68,68,0.1)", borderRadius: "8px", color: totalProfit >= 0 ? "#22c55e" : "#ef4444" }}>
            <strong>Total P/L: {formatCurrency(totalProfit)}</strong>
          </div>
        </div>
      </div>

      <div className="card" style={{ marginBottom: "20px", textAlign: "left" }}>
        <div style={{ display: "flex", justifyContent: "space-between", gap: "16px", alignItems: "flex-start", flexWrap: "wrap", marginBottom: "12px" }}>
          <div>
            <h3 style={{ margin: "0 0 8px 0" }}>AI Portfolio Report</h3>
            <p style={{ margin: 0, color: "#94a3b8" }}>
              Use any OpenAI-compatible provider to generate an analysis covering industry exposure, risk, and holding structure.
            </p>
          </div>
          <div style={{ padding: "10px 12px", borderRadius: "10px", background: "rgba(15,23,42,0.55)", border: "1px solid rgba(148,163,184,0.18)", color: "#cbd5e1", fontSize: "0.9rem", maxWidth: "420px" }}>
            API keys are sent only for the current request. The backend uses them transiently and does not store them.
          </div>
        </div>

        <form onSubmit={handleGenerateReport}>
          <div className="report-form-grid">
            <label className="report-field">
              <span>Provider</span>
              <select className="report-select" value={form.provider} onChange={handleProviderChange}>
                {PROVIDER_OPTIONS.map((option) => (
                  <option key={option.label} value={option.label}>{option.label}</option>
                ))}
              </select>
            </label>

            <label className="report-field">
              <span>Model</span>
              <select className="report-select" value={selectedModel} onChange={handleModelSelectChange}>
                {activeProvider.models.map((modelOption) => (
                  <option key={modelOption.value} value={modelOption.value}>{modelOption.label}</option>
                ))}
              </select>
            </label>
          </div>

          {selectedModel === CUSTOM_MODEL ? (
            <label className="report-field">
              <span>Custom Model</span>
              <input
                className="report-input"
                type="text"
                value={customModel}
                onChange={handleCustomModelChange}
                placeholder="your-model-name"
                required
              />
            </label>
          ) : null}

          {form.provider === CUSTOM_PROVIDER ? (
            <label className="report-field">
              <span>OpenAI-compatible Base URL</span>
              <input
                className="report-input"
                type="text"
                value={customBaseUrl}
                onChange={handleCustomBaseUrlChange}
                placeholder="https://your-provider.example/v1"
                required
              />
            </label>
          ) : (
            <div style={{ marginBottom: "12px", color: "#94a3b8", fontSize: "0.92rem" }}>
              Base URL: <code>{activeProvider.baseUrl}</code>
            </div>
          )}

          <label className="report-field">
            <span>API Key</span>
            <input
              className="report-input"
              type="password"
              value={form.apiKey}
              onChange={handleApiKeyChange}
              placeholder="sk-..."
              required
            />
          </label>

          <div style={{ display: "flex", justifyContent: "space-between", gap: "16px", flexWrap: "wrap", alignItems: "center", marginTop: "8px" }}>
            <p style={{ margin: 0, color: "#94a3b8", fontSize: "0.92rem" }}>
              Industry exposure is inferred from stock names and symbols because the current portfolio records do not include sector metadata.
            </p>
            <button
              type="submit"
              className="primary"
              disabled={isGeneratingReport || isLoadingPortfolio || portfolio.length === 0 || Boolean(portfolioError)}
              style={{ opacity: isGeneratingReport || isLoadingPortfolio || portfolio.length === 0 || Boolean(portfolioError) ? 0.7 : 1 }}
            >
              {isGeneratingReport ? "Generating..." : "Generate AI Report"}
            </button>
          </div>
        </form>

        {reportError ? (
          <div style={{ marginTop: "16px", padding: "12px 14px", borderRadius: "10px", background: "rgba(239,68,68,0.12)", color: "#fca5a5", border: "1px solid rgba(248,113,113,0.25)" }}>
            {reportError}
          </div>
        ) : null}
      </div>

      <div className="card">
        <div style={{ display: "flex", justifyContent: "space-between", gap: "16px", alignItems: "center", marginBottom: "16px", flexWrap: "wrap" }}>
          <h3 style={{ margin: 0 }}>Holdings</h3>
          <span style={{ color: "#94a3b8" }}>{portfolio.length} active positions</span>
        </div>

        {portfolioError ? (
          <div style={{ padding: "12px 14px", borderRadius: "10px", background: "rgba(239,68,68,0.12)", color: "#fca5a5", border: "1px solid rgba(248,113,113,0.25)" }}>
            {portfolioError}
          </div>
        ) : null}

        {isLoadingPortfolio ? (
          <div style={{ color: "#94a3b8", textAlign: "left" }}>Loading portfolio...</div>
        ) : null}

        {!isLoadingPortfolio && !portfolioError && portfolio.length === 0 ? (
          <div style={{ color: "#94a3b8", textAlign: "left" }}>No active holdings to analyze yet.</div>
        ) : null}

        {!isLoadingPortfolio && portfolio.length > 0 ? (
          <div style={{ overflowX: "auto" }}>
            <table style={{ width: "100%", borderCollapse: "collapse", minWidth: "860px" }}>
              <thead>
                <tr style={{ borderBottom: "1px solid rgba(255,255,255,0.1)" }}>
                  <th style={{ padding: "12px", textAlign: "left" }}>Code</th>
                  <th style={{ padding: "12px", textAlign: "left" }}>Name</th>
                  <th style={{ padding: "12px", textAlign: "right" }}>Qty</th>
                  <th style={{ padding: "12px", textAlign: "right" }}>Avg Cost</th>
                  <th style={{ padding: "12px", textAlign: "right" }}>Price</th>
                  <th style={{ padding: "12px", textAlign: "right" }}>Realized P/L</th>
                  <th style={{ padding: "12px", textAlign: "right" }}>Unrealized P/L</th>
                  <th style={{ padding: "12px", textAlign: "right" }}>Total P/L</th>
                </tr>
              </thead>
              <tbody>
                {portfolio.map((item) => (
                  <tr key={item.symbol} style={{ borderBottom: "1px solid rgba(255,255,255,0.05)" }}>
                    <td style={{ padding: "12px" }}><strong>{item.symbol}</strong></td>
                    <td style={{ padding: "12px" }}>{item.name}</td>
                    <td style={{ padding: "12px", textAlign: "right" }}>{item.quantity}</td>
                    <td style={{ padding: "12px", textAlign: "right" }}>{formatCurrency(item.avgCost)}</td>
                    <td style={{ padding: "12px", textAlign: "right" }}>{formatCurrency(item.currentPrice)}</td>
                    <td style={{ padding: "12px", textAlign: "right", color: item.realizedProfit >= 0 ? "#22c55e" : "#ef4444" }}>{formatCurrency(item.realizedProfit)}</td>
                    <td style={{ padding: "12px", textAlign: "right", color: item.unrealizedProfit >= 0 ? "#22c55e" : "#ef4444" }}>{formatCurrency(item.unrealizedProfit)}</td>
                    <td style={{ padding: "12px", textAlign: "right", color: item.totalProfit >= 0 ? "#22c55e" : "#ef4444" }}><strong>{formatCurrency(item.totalProfit)}</strong></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : null}
      </div>

      <div className="card" style={{ marginTop: "20px", textAlign: "left" }}>
        <div style={{ display: "flex", justifyContent: "space-between", gap: "16px", alignItems: "flex-start", flexWrap: "wrap", marginBottom: "16px" }}>
          <div>
            <h3 style={{ margin: "0 0 8px 0" }}>Latest Saved Report</h3>
            <p style={{ margin: 0, color: "#94a3b8" }}>
              Only the newest generated report is displayed here and persisted in the database for this user.
            </p>
          </div>

          {report ? (
            <div style={{ display: "flex", gap: "10px", flexWrap: "wrap", justifyContent: "flex-end" }}>
              <span style={{ padding: "8px 10px", borderRadius: "999px", background: "rgba(99,102,241,0.12)", color: "#c7d2fe", fontSize: "0.88rem" }}>{report.provider}</span>
              <span style={{ padding: "8px 10px", borderRadius: "999px", background: "rgba(15,23,42,0.65)", color: "#cbd5e1", fontSize: "0.88rem" }}>{report.model}</span>
              <span style={{ padding: "8px 10px", borderRadius: "999px", background: "rgba(15,23,42,0.65)", color: "#cbd5e1", fontSize: "0.88rem" }}>{report.holdingsAnalyzed} holdings</span>
            </div>
          ) : null}
        </div>

        {isLoadingSavedReport && !report && !isGeneratingReport ? (
          <div style={{ padding: "18px", borderRadius: "12px", background: "rgba(15,23,42,0.45)", border: "1px dashed rgba(148,163,184,0.24)", color: "#94a3b8" }}>
            Loading latest saved report...
          </div>
        ) : report ? (
          <>
            <div style={{ marginBottom: "16px", color: "#94a3b8", fontSize: "0.92rem" }}>
              Generated at {new Date(report.generatedAt).toLocaleString()} · Estimated analyzed market value {formatCurrency(report.totalMarketValue)}
            </div>
            <div className="report-markdown">
              <ReactMarkdown>{report.reportMarkdown}</ReactMarkdown>
            </div>
          </>
        ) : (
          <div style={{ padding: "18px", borderRadius: "12px", background: "rgba(15,23,42,0.45)", border: "1px dashed rgba(148,163,184,0.24)", color: "#94a3b8" }}>
            {isGeneratingReport
              ? "Waiting for the selected LLM to return your portfolio report..."
              : "No saved report yet. Configure a provider above and generate one to persist the latest analysis here."}
          </div>
        )}
      </div>
    </div>
  );
}

export default PortfolioPage;
