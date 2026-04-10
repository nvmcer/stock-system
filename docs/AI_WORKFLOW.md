# AI Engineering Workflow (Canonical)

This document defines the canonical workflow for all AI agents working in this repository.

---

# 0. Core Principles

1. Repository artifacts over chat memory
2. Design before implementation
3. Isolate work before editing repository files
4. Validate every change before completion
5. Keep changes small, reviewable, and resumable

---

# 1. Workflow Overview

1. Understand
2. Isolate
3. Spec
4. Tasks
5. Implement
6. Validate
7. Review
8. Handoff and PR

---

# 2. Naming and Paths

- **Feature name**: lowercase kebab-case, for example `user-auth-refactor`
- **Branch/worktree**: `feature/<feature-name>` for features, `fix/<feature-name>` for fixes
- **Feature docs**: `docs/<feature-name>/`
- **Spec**: `docs/<feature-name>/spec.md`
- **Tasks**: `docs/<feature-name>/tasks.md`
- **Optional handoff file**: `docs/<feature-name>/handoff.md`
- **Handoff template**: `docs/templates/HANDOFF.md`
- **Skill index**: `.agents/skills/README.md`

---

# 3. Understand (MANDATORY)

Before editing repository files:

1. Capture the problem, desired outcome, and constraints.
2. Ask clarifying questions only when the answer changes scope, behavior, or implementation.
3. If the request is already clear, restate the agreed scope and continue.

**Do not start implementation before scope is understood.**

---

# 4. Isolate

As soon as the feature name is known:

1. Create or switch to `feature/<feature-name>` or `fix/<feature-name>`.
2. Use a dedicated worktree if that is the safer isolation boundary.
3. Never edit `main` directly.
4. Do not revert unrelated changes already present in the worktree.
5. If concurrent changes conflict with the task, stop and resolve the conflict before continuing.

---

# 5. Design (Spec)

Create `docs/<feature-name>/spec.md` before implementation.

Every spec must contain these sections in order:

1. Context
2. Goals
3. Non-goals
4. Approach
5. Impact
6. Risks
7. Test Plan

Spec rules:

- Keep it concise but explicit.
- Document coupling points from `AGENTS.md` in the Impact section.
- Define validation expectations in the Test Plan.
- Update the spec if the scope changes.
- Docs-only or process-only changes still require a spec, but it can be brief.

---

# 6. Task Breakdown

Create `docs/<feature-name>/tasks.md` from the spec before implementation.

Task rules:

- Use an ordered checklist.
- Make tasks small, actionable, and testable.
- Include acceptance criteria when the task is not self-evident.
- Include validation and self-review tasks.
- Update progress as work happens: `[ ]`, `[x]`, or explicitly mark blocked items.

---

# 7. Implementation

Implementation rules:

- Execute tasks in order unless the task list explicitly says otherwise.
- Prefer the smallest correct change.
- Keep docs, code, and tests aligned with the spec.
- Do not silently skip required steps.
- If blocked, document the blocker in `tasks.md` and `handoff.md` when needed.

---

# 8. Validation

Validation is mandatory, but the exact checks depend on the change type.

For application code changes:

- Unit tests for changed code with a target of `>80%` coverage for changed code paths
- Integration tests for API, database, auth, or external integration changes
- Lint with no errors
- Build must pass
- E2E or manual flow validation for user-facing behavior changes

For docs-only or workflow-only changes:

- Verify referenced files and paths exist
- Review cross-document consistency
- Run `git diff --check`
- Run docs-specific linting only if the repository provides it

Validation rules:

- Coverage targets apply to changed code, not pure documentation files.
- If any required validation fails, stop, fix the issue, and rerun validation.
- Do not proceed to completion while known required validation is failing.

---

# 9. Self Review

Before considering the task complete:

- Spec matches the implemented scope
- Tasks are up to date
- Coupled components or documents were updated together
- Validation appropriate to the change type passed
- No unrelated changes were introduced
- Remaining risks or follow-up work are documented

Use the `review-checklist` skill if available, or follow the equivalent checklist manually.

---

# 10. Context Management and Handoff

Repository files are the source of truth for state transfer.

Use `docs/<feature-name>/handoff.md` when:

- work spans multiple sessions
- work is being transferred between agents
- blockers or partial validation need to be preserved

Handoff rules:

- Start from `docs/templates/HANDOFF.md`.
- Record current status, completed work, remaining tasks, blockers, validation, and the next recommended step.
- Keep the handoff file updated only while the task is active; remove or archive it when no longer needed.

Optional tooling:

- Use subagents or skill-loading features when the host environment supports them.
- If the host does not support those features, execute the same workflow manually using the repository docs.

---

# 11. PR Preparation

When preparing a PR, include:

- Summary of what changed and why
- Links to `spec.md` and `tasks.md`
- Link to `handoff.md` if it remains relevant to reviewers
- Validation performed
- Risks, rollout notes, or follow-up items

---

# 12. Failure Handling

If any required step fails:

1. Stop subsequent steps.
2. Fix the failure or document the blocker.
3. Re-run the affected validation.
4. Continue only after the workflow is back in a valid state.

---

# 13. Forbidden

- Skip `spec.md` or `tasks.md`
- Edit `main` directly
- Ignore failing required validation
- Reference missing templates or workflow artifacts
- Leave coupled changes undocumented
- Mix unrelated work into the same task without documenting it

---

# 14. Completion Criteria

A task is complete only when:

- `spec.md` and `tasks.md` exist and match the final scope
- Tasks are marked complete, or blocked/cancelled with an explanation
- Validation appropriate to the change type has passed
- Handoff state is either updated or not needed
- PR-ready summary information exists with links to workflow docs

---

# 15. References

- [AGENTS.md](../AGENTS.md) - Repository-specific constraints and couplings
- [STYLE_GUIDE.md](./STYLE_GUIDE.md) - Code and implementation standards
- [HANDOFF.md template](./templates/HANDOFF.md) - State transfer template
- [Skills README](../.agents/skills/README.md) - Optional workflow guidance
