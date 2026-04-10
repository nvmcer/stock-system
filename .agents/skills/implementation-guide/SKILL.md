---
name: implementation-guide
version: 1.0.0
description: Best practices for implementing features safely
parameters:
  language:
    type: string
    description: Programming language (java, typescript, python)
    required: true
  component_type:
    type: string
    description: Type of component (api, service, ui, database)
    required: false
  task_description:
    type: string
    description: Description of the task being implemented
    required: false
---

# Implementation Guide Skill

Provides execution guidance for moving from approved tasks to finished changes safely.

## When to Use

Use this skill when:

- starting implementation work from `tasks.md`
- deciding how to keep a change minimal and safe
- reviewing whether supporting docs and validation are staying aligned

## Execution Workflow

1. Read `spec.md`, `tasks.md`, and `handoff.md` if present.
2. Confirm the current task and its acceptance criteria.
3. Plan the smallest viable diff.
4. Implement the task without drifting from the documented scope.
5. Update `tasks.md` as soon as task status changes.
6. Run validation for the changed area before moving on.

## Quality Guardrails

- Follow `docs/STYLE_GUIDE.md` for code standards.
- Prefer the smallest correct change.
- Keep specs, tasks, code, tests, and docs aligned.
- Do not edit unrelated work already present in the worktree.
- Stop and document blockers instead of skipping required steps.

## Change-Type Reminders

- **Java / Spring Boot**: keep controllers thin, business logic in services, shared responses consistent with `ApiResponse`.
- **TypeScript / React**: keep API access in services, maintain type safety, and align UI validation/loading/error states with backend behavior.
- **Docs / Workflow**: update all referenced paths and related guidance together.

## Validation Reminder

- Code changes should meet the repository testing expectations for the affected area.
- Docs-only changes still require reference checks and `git diff --check`.

## Related Skills

- `testing-strategy`: Decide what validation to run
- `coupling-analysis`: Confirm required coupled updates
- `review-checklist`: Self-review before handoff or PR
