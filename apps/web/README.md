# Web App

React + Vite frontend for the stock system.

## Responsibilities

- authenticated UI flows
- portfolio, trade, and stock pages
- portfolio AI report generation with provider/model selection
- latest saved AI report display rendered as readable Markdown
- API integration through `VITE_API_BASE`

## Location

```text
apps/web/
├── src/
├── public/
├── package.json
├── vite.config.ts
└── README.md
```

## Local Development

Run through the monorepo compose stack:

```bash
make dev
```

Or run directly:

```bash
npm install
npm run dev
```

The local browser entrypoint is `http://localhost:3001`.

## Portfolio AI Report Flow

- Users open the portfolio page to generate an AI analysis report.
- The UI lets users choose a provider preset or custom OpenAI-compatible base URL.
- The UI lets users choose a provider-aware model preset or enter a custom model name.
- The API key is entered at request time and is not persisted in the frontend or backend.
- The latest saved report is fetched from the backend on page load and rendered as Markdown.

## Build-Time Configuration

Environment values for the web app live under `infra/env/`.

Examples:

- `infra/env/dev/web.env`
- `infra/env/test/web.env.example`
- `infra/env/prod/web.env.example`

## Build

```bash
npm run build
```
