# Context

Users can already view their portfolio holdings, but there is no AI-assisted analysis workflow. The request is to add a user-facing report generator that can analyze portfolio industry exposure, risk, and holding structure while letting each user choose an OpenAI-compatible LLM provider and supply their own API key at request time.

# Goals

- Add a user-facing portfolio report workflow on the portfolio page.
- Let users choose a provider preset or custom OpenAI-compatible base URL.
- Let users choose a model from provider-aware dropdown options, while still allowing a custom model override when needed.
- Let users enter an API key without persisting the secret.
- Generate a structured report that covers industry exposure, concentration and risk observations, and holding structure.
- Keep the API user-scoped and compatible with the existing `ApiResponse` envelope.
- Persist the latest generated report in the database and show that latest saved report on the user page.

# Non-goals

- Persisting provider settings or API keys to the database.
- Keeping a full historical archive of every generated report.
- Introducing server-side scheduled AI analysis.
- Building a generic cross-app AI integration framework.
- Guaranteeing authoritative sector classification from a market-data vendor.

# Approach

- Extend the backend portfolio domain with a report-generation endpoint under `/api/portfolio`.
- Accept a request payload containing provider label, base URL, model, and API key.
- Persist the latest generated report per user in a dedicated table, updating the existing row when a new report is generated.
- Reuse current portfolio holdings plus calculated market values to build a prompt payload for the selected LLM.
- Call the provider through the OpenAI-compatible `/chat/completions` API using `RestTemplate`.
- Handle common OpenAI-compatible response variations, including `message.content` returned as either plain text or an array of text parts.
- Map provider reachability / malformed provider response failures to explicit API errors instead of surfacing them as generic 500s.
- Instruct the model to classify each holding into an industry based on available symbol/name context and produce a structured markdown report with:
  - industry exposure summary
  - risk analysis
  - portfolio structure analysis
  - key watch items / recommendations
- Return the generated report plus lightweight metadata to the frontend.
- Add a read endpoint for the latest saved report so the portfolio page can show it on load.
- Update the portfolio page with provider/model selectors, custom override inputs only when needed, loading/error states, and a report panel that shows the latest saved report.
- Render saved reports in a readable format, preferring proper Markdown rendering over plain preformatted text.

# Impact

- Backend: new DTOs, service logic, persistence model, controller endpoints, and a Flyway migration for saved reports.
- Frontend: new portfolio-page UI state, provider/model selector UX, request submission, and latest-report rendering.
- Frontend: markdown report presentation styling for the latest saved report.
- Credentials remain transient and are not persisted.

# Risks

- OpenAI-compatible providers vary in strictness around payload shape and URL format.
- Industry classification is inferred by the model because the current stock records do not include sector metadata.
- API keys are highly sensitive and must not be logged or stored.
- Report quality depends on the selected model.
- Provider/model preset lists can drift if vendors rename models.

# Test Plan

- Add backend unit tests for controller wiring and service request validation / response parsing.
- Add backend tests for provider-network failures and alternate OpenAI-compatible content shapes.
- Add backend tests for saving and loading the latest persisted report.
- Run backend tests with Maven.
- Run frontend lint and build to validate TypeScript and rendering changes.
