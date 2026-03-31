# Web App

React + Vite frontend for the stock system.

## Responsibilities

- authenticated UI flows
- portfolio, trade, and stock pages
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
