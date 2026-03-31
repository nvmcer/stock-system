# Infra

This directory is the only place for runtime, deployment, observability, and infrastructure code.

## Layout

```text
infra/
├── caddy/
├── compose/
├── env/
├── observability/
├── terraform/
│   ├── modules/
│   └── live/prod/
└── README.md
```

## Compose Layers

- `compose/base.yml`: shared API runtime used in every environment
- `compose/dev.yml`: local-only services and hot reload wiring
- `compose/observability.yml`: optional local observability stack
- `compose/edge.yml`: Caddy layer for `test` and `prod`

## Environment Contract

```text
infra/env/
├── dev/
│   ├── compose.env
│   ├── api.env
│   └── web.env
├── test/
│   ├── compose.env.example
│   ├── api.env.example
│   └── web.env.example
└── prod/
    ├── compose.env.example
    ├── api.env.example
    └── web.env.example
```

Rules:

- `dev` files can be tracked because they contain only local defaults
- `test` and `prod` real env files must stay outside git
- application secrets belong in `api.env`
- Docker and routing values belong in `compose.env`
- frontend build-time values belong in `web.env`

## Terraform Layout

Terraform is structured for long-term growth, but only `prod` is modeled right now.

- `terraform/modules/`: reusable provider-specific modules
- `terraform/live/prod/global/`: Cloudflare Pages and DNS
- `terraform/live/prod/app/`: Hetzner server and firewall

State is intentionally split between `global` and `app` so DNS and Pages changes do not share the same state as server lifecycle changes.
