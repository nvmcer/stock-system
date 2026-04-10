- [x] Update portfolio report request contracts to carry a language value
- Acceptance criteria: frontend `PortfolioAiReportRequest` and backend `PortfolioAnalysisRequestDto` both expose `language`
- Acceptance criteria: backend treats missing or unsupported language values as English

- [x] Add a language selector to the portfolio report form with English as the default
- Acceptance criteria: `PortfolioPage.tsx` renders a language select near the provider/model controls
- Acceptance criteria: generate-report submissions include the selected language in the request payload

- [x] Make backend prompt generation language-aware without changing persistence schema
- Acceptance criteria: English prompts are used by default
- Acceptance criteria: explicitly choosing Chinese generates Chinese prompt instructions

- [x] Update automated tests and run relevant verification commands
- Acceptance criteria: portfolio analysis service tests cover default English and explicit Chinese behavior
- Acceptance criteria: relevant frontend/backend verification commands pass or failures are documented
