from fastapi import FastAPI
from pydantic import BaseModel, Field
from prometheus_fastapi_instrumentator import Instrumentator

from app.model import score_transaction

app = FastAPI(
    title="AI-BankApp-DevOps Fraud Detection Service",
    description="Lightweight ML + rule-based transaction fraud scoring microservice.",
    version="1.0.0",
)

Instrumentator().instrument(app).expose(app, endpoint="/metrics")


class FraudCheckRequest(BaseModel):
    fromAccountNumber: str
    toAccountNumber: str
    amount: float = Field(gt=0)
    fromAccountBalance: float = Field(ge=0)
    transactionsLastHour: int = Field(ge=0, default=0)


class FraudCheckResponse(BaseModel):
    fraudScore: float
    isFraud: bool
    reason: str


@app.get("/health")
def health():
    return {"status": "UP"}


@app.post("/api/v1/predict", response_model=FraudCheckResponse)
def predict(request: FraudCheckRequest):
    is_new_payee = request.toAccountNumber != request.fromAccountNumber and request.transactionsLastHour == 0
    score, is_fraud, reason = score_transaction(
        amount=request.amount,
        from_balance=request.fromAccountBalance,
        tx_last_hour=request.transactionsLastHour,
        is_new_payee=is_new_payee,
    )
    return FraudCheckResponse(fraudScore=score, isFraud=is_fraud, reason=reason)
