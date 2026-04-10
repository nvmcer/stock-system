---
name: review-checklist
version: 1.0.0
description: Comprehensive checklist for code review and PR preparation
parameters:
  review_type:
    type: string
    description: Type of review (self-review, pr-prep, code-review)
    required: true
  feature_name:
    type: string
    description: The kebab-case feature name
    required: true
  language:
    type: string
    description: Programming language (java, typescript)
    required: false
---

# Review Checklist Skill

Provides a final review checklist for self-review, handoff readiness, and PR preparation.

## When to Use

Use this skill when:

- preparing a PR summary
- self-reviewing completed work
- checking whether a handoff contains enough state to resume safely

## Review Checklist

- [ ] Scope still matches `spec.md`
- [ ] `tasks.md` reflects the real task status
- [ ] Coupled files and documents were updated together
- [ ] Validation appropriate to the change type passed
- [ ] No unrelated changes were introduced
- [ ] Remaining risks, blockers, or follow-ups are documented

## Docs and Workflow Checks

- [ ] Every referenced path exists
- [ ] `docs/AI_WORKFLOW.md`, `AGENTS.md`, templates, and skills do not contradict each other
- [ ] Handoff file exists if active work needs state transfer

## PR Readiness

- [ ] PR summary explains what changed and why
- [ ] PR summary links `spec.md` and `tasks.md`
- [ ] PR summary includes validation performed
- [ ] PR summary includes risks or follow-up items when relevant

## Common Misses

- Validation was run, but the result was not recorded.
- The implementation drifted from the documented scope.
- A coupled doc or consumer was left stale.
- The work is pausing, but no handoff was written.

## Related Skills

- `testing-strategy`: Confirm validation expectations
- `coupling-analysis`: Recheck impacted areas
- `implementation-guide`: Fix execution gaps before handoff or PR
