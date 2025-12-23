from fastapi import FastAPI
import yfinance as yf

app = FastAPI()

@app.get("/prices")
def get_prices(symbols: str):
    symbol_list = symbols.split(",")
    result = {}

    for symbol in symbol_list:
        ticker = yf.Ticker(symbol)
        data = ticker.history(period="1d")
        if not data.empty:
            price = float(data["Close"].iloc[-1])
            result[symbol] = price

    return result