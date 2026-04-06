CREATE TABLE portfolio_analysis_reports (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    provider VARCHAR(80) NOT NULL,
    model VARCHAR(120) NOT NULL,
    report_markdown TEXT NOT NULL,
    generated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    holdings_analyzed INTEGER NOT NULL,
    total_market_value NUMERIC(19, 2) NOT NULL,
    CONSTRAINT fk_portfolio_analysis_reports_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
