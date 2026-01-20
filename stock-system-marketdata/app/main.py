import os
import requests
from fastapi import FastAPI, HTTPException, Query
from mangum import Mangum
from typing import Dict
from dotenv import load_dotenv

# Initialize FastAPI application for stock price service
app = FastAPI(title="Stock Price Service")

# Load environment variables from .env file
load_dotenv()

# API key for Finnhub service (get latest stock prices)
FINNHUB_KEY = os.getenv("FINNHUB_KEY")

def fetch_from_finnhub(symbol: str) -> float:
    """
    Fetch stock price from Finnhub API
    
    Args:
        symbol: Stock ticker symbol (e.g., 'AAPL')
    
    Returns:
        float: Current stock price
        
    Raises:
        ValueError: If API key not set or price data invalid
    """
    if not FINNHUB_KEY:
        raise ValueError("FINNHUB_KEY is not configured")
    
    # Call Finnhub API to get current price
    url = f"https://finnhub.io/api/v1/quote?symbol={symbol.upper()}&token={FINNHUB_KEY}"

    response = requests.get(url, timeout=5)
    response.raise_for_status()
    
    data = response.json()
    # 'c' key contains the current price in Finnhub response
    price = data.get("c")
    
    if price is None or price == 0:
        raise ValueError(f"Invalid price data for {symbol}")
        
    return float(price)

@app.get("/prices")
async def get_prices(symbols: str = Query(..., description="Comma separated symbols, e.g. AAPL,TSLA")):
    """
    Get current stock prices for given symbols.
    
    This endpoint fetches prices from Finnhub for multiple stocks.
    If some requests fail, returns prices for successful ones.
    """
    # Parse and clean symbol list
    symbol_list = [s.strip() for s in symbols.split(",") if s.strip()]
    result: Dict[str, float] = {}
    errors = []

    # Fetch price for each symbol
    for symbol in symbol_list:
        try:
            price = fetch_from_finnhub(symbol)
            result[symbol] = price
        except Exception as e:
            # Log error but continue processing other symbols
            errors.append(f"Error fetching {symbol}: {str(e)}")
    
    # Return error only if all symbols failed
    if not result and errors:
        raise HTTPException(status_code=502, detail=f"Failed to fetch any prices: {'; '.join(errors)}")

    return result

# Lambda handler for AWS deployment
handler = Mangum(app, lifespan="off")