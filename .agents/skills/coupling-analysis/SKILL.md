---
name: coupling-analysis
version: 1.0.0
description: Analyze coupling points and cross-component impacts
parameters:
  change_type:
    type: string
    description: Type of change (dto, api, schema, config, auth, workflow)
    required: true
  affected_components:
    type: array
    description: List of components that might be affected
    required: false
  feature_name:
    type: string
    description: The kebab-case feature name
    required: true
---

# Coupling Analysis Skill

Provides guidance for identifying required coupled changes before implementation or review.

## When to Use

Use this skill when:

- writing or reviewing `spec.md`
- a change touches shared contracts or multiple components
- a workflow or process document change must stay consistent across several files
- downstream impacts are not obvious

## Coupling Types (from AGENTS.md)

### DTO Changes
- **Backend DTO** → Update frontend types + consumers
- **API Response** → Update error handling + parsing
- **Request DTO** → Update validation + documentation

### Authentication Changes
- **JWT/Auth** → Update backend security + frontend login/requests
- **User context** → Update all user-scoped endpoints

### Schema Changes
- **Database schema** → Add Flyway migration
- **Entity changes** → Update related DTOs + services

### Configuration Changes
- **Backend env** → Align frontend env + deployment docs
- **Secrets** → Update all environments consistently

### Workflow and Process Changes
- **AI workflow docs** → Align `docs/AI_WORKFLOW.md`, `AGENTS.md`, templates, and skill docs
- **Templates** → Ensure referenced files exist at the documented paths

## Analysis Process

1. Identify the primary change type.
2. Check `AGENTS.md` for required coupled updates.
3. List the files, tests, and docs that must change together.
4. Decide whether the change can land atomically.
5. Record the results in the spec Impact section and reflect them in `tasks.md`.

## Output Checklist


- Primary change type recorded
- Required coupled updates listed
- Validation implications noted
- Coordination or rollout risks noted

## Risk Assessment

High-risk changes usually include:

- shared DTO changes
- API envelope changes
- auth changes
- schema changes
- workflow/process updates that modify canonical instructions

## Common Pitfalls

- **Forgotten couplings**: Primary file updated but dependent docs or consumers are not
- **Local-only fixes**: One document is corrected while the canonical workflow still disagrees elsewhere
- **Validation gaps**: Coupled areas change without matching test or review updates

## Related Skills

- `spec-writing`: Record impacts in the spec
- `implementation-guide`: Execute coupled changes safely
- `testing-strategy`: Match validation to the coupled scope
