---
name: task-breakdown
version: 1.0.0
description: Best practices for breaking down features into actionable tasks
parameters:
  spec_path:
    type: string
    description: Path to the spec.md file to break down
    required: true
  feature_name:
    type: string
    description: The kebab-case feature name
    required: true
  max_task_size:
    type: string
    description: Maximum task size (default "2h")
    required: false
    default: "2h"
---

# Task Breakdown Skill

Provides guidance for turning a spec into a small, executable `tasks.md` checklist.

## When to Use

Use this skill when:

- creating `docs/<feature-name>/tasks.md` from a spec
- splitting work that feels too large or ambiguous
- checking whether tasks are ordered and testable

## Task Quality Bar

- Target roughly 1-2 hours per task.
- Each task should describe a real deliverable.
- Each task should be testable or reviewable.
- Dependencies should be obvious from the order.

## Required Format

Use an ordered checklist in `docs/<feature-name>/tasks.md`.

Example:

```markdown
- [ ] rewrite `docs/AI_WORKFLOW.md`
  - Restores explicit branch isolation, validation, and failure handling
- [ ] add `docs/templates/HANDOFF.md`
  - Template path exists and matches workflow references
- [ ] run docs-focused validation
  - `git diff --check` passes
```

## Breakdown Workflow

1. Start from the spec goals and approach.
2. Break the work into ordered, independently reviewable steps.
3. Include tasks for supporting docs, validation, and self-review.
4. Add acceptance criteria where the task description alone is not enough.
5. Keep `tasks.md` updated as work progresses.

## Progress Tracking

Update `tasks.md` during implementation:

- mark completed tasks with `[x]`
- mark blocked items explicitly
- add follow-up tasks if the scope legitimately expands

## Common Pitfalls

- **Tasks too large**: One checkbox hides multiple deliverables
- **Missing validation**: No task proves the change was verified
- **No review task**: Work finishes without checking spec/tasks alignment
- **Outdated status**: `tasks.md` stops reflecting reality

## Related Skills

- `spec-writing`: Source of the implementation scope
- `implementation-guide`: Guidance while executing tasks
- `testing-strategy`: Validation planning for task completion
