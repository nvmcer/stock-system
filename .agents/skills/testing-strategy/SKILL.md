---
name: testing-strategy
version: 1.0.0
description: Comprehensive testing strategy and best practices
parameters:
  test_type:
    type: string
    description: Type of testing needed (unit, integration, e2e, performance)
    required: false
  component:
    type: string
    description: Component being tested (api, service, ui, database)
    required: false
  language:
    type: string
    description: Programming language (java, typescript)
    required: true
---

# Testing Strategy Skill

Provides guidance for choosing validation that matches the type and risk of the change.

## When to Use

Use this skill when:

- writing the Test Plan in `spec.md`
- deciding what validation a task requires
- checking whether docs-only changes have been validated appropriately

## Validation by Change Type

### Docs or Workflow Changes

- Verify referenced files and paths exist.
- Review cross-document consistency.
- Run `git diff --check`.
- Run docs-specific linting only if the repository provides it.

### Code Changes

- Run unit tests for changed logic.
- Target `>80%` coverage for changed code paths.
- Add integration tests for API, database, auth, or external integration changes.
- Run lint and build where applicable.
- Add E2E or manual flow validation for user-facing behavior changes.

## Planning Checklist

1. What changed: code, docs, workflow, config, schema, UI, or API?
2. What could realistically break?
3. Which automated checks exist in this repository for that area?
4. What manual verification is still needed?
5. What evidence should be recorded in `tasks.md`, `handoff.md`, or the PR summary?

## Common Requirements

- Include error and edge cases for changed code.
- Match validation to coupling risk, not just the primary file changed.
- Do not claim coverage targets for docs-only changes.
- Rerun validation after fixing a failure.

## Related Skills

- `spec-writing`: Record the test plan in the spec
- `implementation-guide`: Run validation during task execution
- `review-checklist`: Confirm validation is complete before finish
