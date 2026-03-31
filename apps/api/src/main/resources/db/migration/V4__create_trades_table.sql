CREATE TABLE trades (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    stock_id BIGINT NOT NULL,
    type VARCHAR(10) NOT NULL,   -- BUY or SELL
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_trade_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_trade_stock FOREIGN KEY (stock_id) REFERENCES stocks(id)
);