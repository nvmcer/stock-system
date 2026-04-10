# Skills Index

This directory contains optional workflow guidance for AI agents working in this repository.

`docs/AI_WORKFLOW.md` remains the canonical process. These skill files provide deeper, step-specific guidance when the host environment supports skill loading. If the host does not support skills, follow the equivalent workflow manually.

## Available Skills

- `spec-writing`
  - Use when creating or revising `docs/<feature-name>/spec.md`.
- `task-breakdown`
  - Use when converting a spec into `docs/<feature-name>/tasks.md`.
- `coupling-analysis`
  - Use when a change may affect shared contracts, configs, auth, schemas, or workflow/process docs.
- `implementation-guide`
  - Use when starting implementation and deciding how to execute tasks safely.
- `testing-strategy`
  - Use when defining or reviewing the validation plan for code or docs changes.
- `review-checklist`
  - Use before handoff, PR preparation, or final self-review.

## Usage Notes

- Skills support the workflow; they do not replace `AGENTS.md` or `docs/AI_WORKFLOW.md`.
- If a skill conflicts with the workflow docs, follow `docs/AI_WORKFLOW.md` for the canonical sequence and `AGENTS.md` for repository-specific constraints.
- Keep skill docs aligned with the workflow when updating this directory.
