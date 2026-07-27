# AI Fraud Detection Service

FastAPI microservice that scores banking transactions for fraud risk using:
- An Isolation Forest anomaly detection model (scikit-learn), trained on synthetic transaction data (`train_model.py`).
- Explainable rule-based checks (large amount, high balance-drain ratio, high transaction velocity).

## Run locally
```bash
pip install -r requirements.txt
python app/train_model.py
uvicorn app.main:app --reload --port 8000
```

## Endpoints
- `GET /health` — liveness check
- `POST /api/v1/predict` — score a transaction
- `GET /metrics` — Prometheus metrics

## Tests
```bash
pytest tests/
```
