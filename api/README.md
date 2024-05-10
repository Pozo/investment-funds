GOOGLEFINANCE(ticker, [attribute], [start_date], [end_date|num_days], [interval])


- **ticker** - The ticker symbol for the security to consider. It’s mandatory to use both the exchange symbol and ticker symbol for accurate results and to avoid discrepancies. For example, use “NASDAQ:GOOG” instead of “GOOG.”
- **attribute** - [ OPTIONAL - "price" by default ] - The attribute to fetch about ticker from Google Finance and is required if a date is specified.
- **start_date** - [ OPTIONAL ] - The start date when fetching historical data.
- **end_date|num_days** - [ OPTIONAL ] - The end date when fetching historical data, or the number of days from start_date for which to return data.
- **interval** - [ OPTIONAL ] - The frequency of returned data; either "DAILY" or "WEEKLY".

```commandline
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"attribute":"rate", "startDate":"2024/03/01"}' \
  http://localhost:8080/sheets/funds/HU0000731914 | jq .
```