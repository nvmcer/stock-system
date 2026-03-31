# Terraform Layout

This directory is structured for long-term growth, but currently focuses only on `prod`.

## Goals

- keep provider-specific logic in reusable modules
- keep environment-specific wiring in `live/`
- split state by responsibility
- support future migration from Hetzner to other VM or cloud providers with minimal repo churn

## Structure

```text
infra/terraform/
├── modules/
│   ├── cloudflare_dns/
│   ├── cloudflare_pages/
│   ├── hetzner_firewall/
│   └── hetzner_server/
└── live/
    └── prod/
        ├── global/
        └── app/
```

## State Strategy

### `live/prod/global`

Use for resources that are global or edge-facing:

- Cloudflare DNS
- Cloudflare Pages project
- other future global edge resources

### `live/prod/app`

Use for application runtime resources:

- Hetzner server
- Hetzner firewall
- volume, network, floating IP, or similar runtime resources later

This split keeps DNS and Pages changes separate from server lifecycle changes.

## Backend Strategy

Each stack under `live/` should have its own backend configuration and therefore its own state.

Recommended pattern:

- `live/prod/global/backend.hcl`
- `live/prod/app/backend.hcl`

Example init:

```bash
terraform -chdir=infra/terraform/live/prod/global init -backend-config=backend.hcl
terraform -chdir=infra/terraform/live/prod/app init -backend-config=backend.hcl
```

## Variables

Do not commit live secrets.

Recommended pattern:

- commit `terraform.tfvars.example`
- keep real values in untracked `terraform.tfvars`
- use CI secret injection for automation later

## Current Scope

This layout intentionally prepares:

- Cloudflare Pages
- Cloudflare DNS
- Hetzner server
- Hetzner firewall

It does not yet attempt to model dev or test infrastructure in Terraform.
