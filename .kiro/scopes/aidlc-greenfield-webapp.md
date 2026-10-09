---
name: greenfield-webapp
depth: Standard
keywords:
  - greenfield
  - new web app
  - new web application
  - web app from scratch
description: Greenfield web application — full ideation spine, quality gates, no brownfield discovery
skeleton: on
runner: false
guard_policy: strict
sensors: on
learnings: on
summary_confirmation: on
---

# greenfield-webapp scope

Standard depth for a brand-new web application starting from zero. The ideation
phase runs its full ceremony (intent-capture, feasibility, scope-definition,
rough-mockups, approval-handoff) to resolve high intent ambiguity and unresolved
assumptions. The inception phase focuses on practices-discovery, requirements
analysis, and refined-mockups, skipping decomposition stages that are only needed
when structural complexity is high. Construction runs code-generation,
build-and-test, and ci-pipeline, then the operation phase adds observability-setup
and performance-validation to close the high verification-entropy loop.

Guard Policy defaults to strict: all gates require explicit approval. Appropriate
for a first-build where every decision is new.

## Why these stages, why skip those

- **market-research** SKIP: IAE is resolved by intent-capture + scope-definition;
  no unknown external market to research.
- **team-formation** SKIP: single team, no multi-team coordination overhead.
- **reverse-engineering** SKIP: greenfield condition — no existing code.
- **user-stories** SKIP: subsumed by requirements-analysis + refined-mockups.
- **domain-design / units / contract / delivery / functional-design** SKIP:
  CSU=LOW on a greenfield project; structural decisions are made inline during
  code-generation.
- **nfr-design** SKIP: single measurable NFR targets defined in nfr-requirements
  are closed by code-generation + performance-validation.
- **infrastructure-design** SKIP: R=MED, no complex infrastructure changes.
- **deployment-pipeline / environment-provisioning / deployment-execution** SKIP:
  out of scope for the initial build.
- **incident-response / feedback-optimization** SKIP: initial build scope.

## Membership

Keyword triggers: `greenfield`, `new web app`, `new web application`,
`web app from scratch`. Initialization, full ideation, focused inception, and
the full build + quality path of construction and early operations run.
