# Feature: portfolio-report-language

## Context
- The portfolio AI report form currently lets the user choose provider and model, but the generated prompt is hard-coded to Simplified Chinese in the backend.
- Users need to choose the report language at generation time.
- The default experience should generate reports in English when the user does not change the language selector.

## Goals
- Add a language selector to the portfolio AI report form in `apps/web/src/pages/PortfolioPage.tsx`.
- Default the selected report language to English for new report generation requests.
- Send the selected language from the frontend request DTO to the backend analysis endpoint.
- Generate the report prompt in the selected language without changing the saved report schema.

## Non-goals
- Backfilling or translating previously saved reports.
- Storing report language in the database.
- Adding broader i18n support for the entire portfolio page.

## Approach
- Extend the frontend `PortfolioAiReportRequest` type with a `language` field and add a small set of language options in `PortfolioPage.tsx`.
- Initialize the report form with English as the default language and include the chosen value in the generate-report payload.
- Extend `PortfolioAnalysisRequestDto` with the same field.
- Replace the fixed backend Chinese prompt with language-aware prompt builders that default to English when the incoming value is blank or unsupported.
- Keep response and persistence shape unchanged to avoid a schema migration.

## Impact
- Files to change:
- `apps/web/src/pages/PortfolioPage.tsx`
- `apps/web/src/services/types.ts`
- `apps/api/src/main/java/com/portfolio/dto/PortfolioAnalysisRequestDto.java`
- `apps/api/src/main/java/com/portfolio/service/PortfolioAnalysisService.java`
- `apps/api/src/test/java/com/portfolio/service/PortfolioAnalysisServiceTest.java`
- Coupling points:
- Backend request DTO change must be reflected in frontend request typing and payload construction.
- Prompt generation logic changes must be covered by backend tests because report content language is user-visible behavior.
- Breaking changes: none expected because `language` will be optional-compatible on the backend and default to English.

## Risks
- Unsupported language values could produce inconsistent prompts.
- Mitigation: normalize and whitelist supported values, then fall back to English.
- Changing prompt text could make current assertions brittle.
- Mitigation: assert language-specific prompt fragments in tests instead of overfitting to full provider payloads.

## Test Plan
- Unit tests:
- Update `PortfolioAnalysisServiceTest` to verify English is the default prompt language.
- Add coverage that Chinese can still be requested explicitly.
- Integration tests:
- Keep controller tests passing with the extended request DTO.
- Manual verification:
- Open the portfolio page, confirm the new selector defaults to English, and generate a report payload containing the chosen language.
