# Context

- `docs/AI_WORKFLOW.md` is the canonical AI workflow, but the current revision is not fully executable from repository artifacts alone.
- The workflow references a `HANDOFF.md` template that does not exist, assumes subagent/skill support without a fallback path, and leaves branch isolation and failure handling less explicit than `AGENTS.md` requires.
- The new `.agents/skills/` content is useful, but the skill documents contain duplicated sections and do not yet present a clear entry point for how they fit into the workflow.

# Goals

- Make the workflow self-contained and executable from the repository alone.
- Keep `docs/AI_WORKFLOW.md`, `AGENTS.md`, and `.agents/skills/` aligned on required steps and terminology.
- Add the missing handoff/template artifacts referenced by the workflow.
- Preserve the strict spec-first and tasks-first process while making validation expectations realistic for docs-only work.
- Reduce ambiguity around branch/worktree isolation, failure handling, context transfer, and optional skill usage.

# Non-goals

- Changing application runtime code in `apps/` or infrastructure behavior in `infra/`.
- Introducing CI automation for the workflow itself.
- Replacing `AGENTS.md` as the repository-specific constraints file.

# Approach

- Rewrite `docs/AI_WORKFLOW.md` into a complete, end-to-end workflow that covers understanding, isolation, spec, tasks, implementation, validation, review, handoff, and PR preparation.
- Add concrete repository paths for reusable templates and workflow support files.
- Introduce `docs/templates/HANDOFF.md` as the handoff template referenced by the canonical workflow.
- Add a `.agents/skills/README.md` index so skill usage is discoverable and tied back to the workflow.
- Clean up duplicated or conflicting guidance inside the existing skill documents so they reinforce one consistent process.
- Update `AGENTS.md` only where necessary to keep its hard rules and references aligned with the rewritten workflow.

# Impact

- Files to change: `docs/AI_WORKFLOW.md`, `AGENTS.md`, `.agents/skills/*.md`, plus new supporting docs under `docs/templates/` and `.agents/skills/`.
- Coupling points: `AGENTS.md` must remain consistent with the canonical workflow; skill docs must not contradict the workflow they support.
- Breaking changes: AI agents following the old wording may need to adopt the new explicit handoff/template paths and validation guidance.

# Risks

- Over-correcting the workflow could make it verbose or too rigid for small documentation changes.
- Changing canonical instructions can create temporary confusion if supporting docs are not updated atomically.
- Adding optional/fallback behavior must not weaken the hard requirements around spec, tasks, isolation, and validation.

# Test Plan

- Review all updated docs together to confirm cross-references, paths, and required terminology are consistent.
- Run `git diff --check` to catch formatting issues in edited markdown files.
- Manually verify that every referenced workflow artifact now exists in the repository.
