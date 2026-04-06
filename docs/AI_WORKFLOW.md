# AI Engineering Workflow (Canonical)

This document defines the canonical workflow for all AI agents working in this repository.

---

# 0. Core Principles

1. Design before code
2. Isolation by default
3. Tests are mandatory
4. Small, reviewable changes
5. All intermediate artifacts MUST be persisted

---

# 1. Workflow Overview

1. Requirement Understanding
2. Design (Spec) → saved to docs
3. Task Breakdown → saved to docs
4. Worktree / Branch Setup
5. Implementation
6. Testing & Validation
7. Self Review
8. PR Preparation

---

# 2. Feature Naming (MANDATORY)

Before creating spec, define a feature name:

## Rules
- lowercase
- kebab-case
- short but descriptive

## Examples
- user-auth-refactor
- add-payment-api
- fix-login-bug

## Usage
All artifacts MUST be placed under:

docs/<feature-name>/

---

# 3. Requirement Understanding

## Output
- problem summary
- expected outcome
- constraints

DO NOT start coding

---

# 4. Design (Spec)

## File Location (MANDATORY)

docs/<feature-name>/spec.md

## Format

### Context
### Goals
### Non-goals
### Approach
### Impact
### Risks
### Test Plan

## Rules

- MUST create spec.md BEFORE implementation
- MUST write to file (not just chat)
- If spec changes → update file

---

# 5. Task Breakdown

## File Location (MANDATORY)

docs/<feature-name>/tasks.md

## Format

- Ordered checklist

Example:

- [ ] create API endpoint
- [ ] implement validation
- [ ] write unit tests
- [ ] update docs

## Rules

- MUST derive from spec
- MUST be actionable
- MUST be persisted to file

---

# 6. Worktree / Branch Setup

## Rules

- MUST create isolated environment

branch name:
feature/<feature-name>

OR

use git worktree:
<feature-name>

---

# 7. Implementation

## Rules

- Follow spec.md strictly
- Execute tasks.md sequentially
- Update tasks.md:
  - mark completed tasks

Example:

- [x] create API endpoint

---

# 8. Testing & Validation

## Required

- run tests
- run lint
- run build (if applicable)

## Rules

- ALL must pass
- if fail → fix before continuing

---

# 9. Self Review

## Checklist

- spec matches implementation
- tasks completed
- no unrelated changes
- edge cases handled

---

# 10. PR Preparation

## MUST reference docs

PR must include:

- link to:
  - spec.md
  - tasks.md

## Required content

### Summary
### Changes
### Testing
### Risks
### Rollback

---

# 11. Failure Handling

If any step fails:

- STOP
- FIX
- CONTINUE

---

# 12. Forbidden Actions

- skipping spec.md
- skipping tasks.md
- coding without docs
- not updating tasks status
- editing main branch
- ignoring failing tests

---

# 13. Completion Criteria

A task is complete ONLY IF:

- spec.md exists
- tasks.md exists
- tasks marked done
- tests pass
- PR ready with doc links

---