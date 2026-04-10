---
name: spec-writing
version: 1.0.0
description: Best practices for writing effective spec.md files
parameters:
  feature_name:
    type: string
    description: The kebab-case feature name (e.g., user-auth-refactor)
    required: true
  context:
    type: string
    description: Brief description of the problem and current state
    required: false
  requirements:
    type: string
    description: Key requirements and constraints
    required: false
---

# Spec Writing Skill

Provides guidance for creating clear, executable `spec.md` files that match the repository workflow.

## When to Use

Use this skill when:

- creating `docs/<feature-name>/spec.md`
- revising an existing spec after scope changes
- checking whether a spec is complete before implementation

## Required Output

Every `spec.md` must include these sections in order:

### Context

- What problem is being solved?
- What is the current state versus the desired state?
- Why is this change needed now?

### Goals

- What outcomes will be achieved?
- What are the acceptance criteria?

### Non-goals

- What is explicitly out of scope?

### Approach

- High-level solution strategy
- Key decisions and trade-offs

### Impact

- Files or areas expected to change
- Coupling points from `AGENTS.md`
- Breaking changes or migration needs

### Risks

- Main risks
- Mitigations

### Test Plan

- Validation appropriate to the change type
- Code changes: unit/integration/lint/build/E2E as applicable
- Docs-only changes: reference review, path checks, `git diff --check`, and docs lint if available

## Writing Workflow

1. Capture the requirement, constraints, and expected outcome.
2. Separate scope from non-goals.
3. Describe the approach at the decision level, not the line-by-line implementation level.
4. Record coupling in the Impact section. Use `coupling-analysis` when the affected areas are not obvious.
5. Keep the Test Plan realistic for the kind of change being made.
6. Update the spec when the implementation scope changes.

## Best Practices


- Keep it concise and decision-focused.
- Prefer bullets over long prose.
- Use concrete acceptance criteria.
- Include coupling whenever shared contracts, configuration, auth, schemas, or workflow docs are affected.

## Related Skills

- `coupling-analysis`: Confirm cross-component or cross-document impacts
- `task-breakdown`: Turn the spec into executable tasks
- `testing-strategy`: Refine the validation plan
