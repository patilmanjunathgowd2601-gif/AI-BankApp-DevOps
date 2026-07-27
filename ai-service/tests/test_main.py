from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)


def test_health():
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json()["status"] == "UP"


def test_predict_normal_transaction():
    payload = {
        "fromAccountNumber": "ACC1",
        "toAccountNumber": "ACC2",
        "amount": 50.0,
        "fromAccountBalance": 2000.0,
        "transactionsLastHour": 1,
    }
    response = client.post("/api/v1/predict", json=payload)
    assert response.status_code == 200
    body = response.json()
    assert "fraudScore" in body
    assert isinstance(body["isFraud"], bool)


def test_predict_flags_large_drain():
    payload = {
        "fromAccountNumber": "ACC1",
        "toAccountNumber": "ACC2",
        "amount": 9500.0,
        "fromAccountBalance": 10000.0,
        "transactionsLastHour": 7,
    }
    response = client.post("/api/v1/predict", json=payload)
    assert response.status_code == 200
    body = response.json()
    assert body["isFraud"] is True
