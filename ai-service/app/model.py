import os
from typing import Tuple

import joblib
import numpy as np

MODEL_PATH = os.path.join(os.path.dirname(__file__), "..", "model.joblib")

# Rule-based thresholds used to complement the ML anomaly score. Combining a
# statistical model with explainable business rules is a common, pragmatic
# pattern for fraud detection: the ML score catches novel patterns, the rules
# guarantee well-understood high-risk cases are always caught and explainable.
# Amounts are in INR (Indian Rupees) to match the app's currency.
LARGE_AMOUNT_THRESHOLD = 200000.0  # Rs 2,00,000
HIGH_DRAIN_RATIO_THRESHOLD = 0.85
HIGH_VELOCITY_THRESHOLD = 5

_model = None


def _load_model():
    global _model
    if _model is None:
        if os.path.exists(MODEL_PATH):
            _model = joblib.load(MODEL_PATH)
        else:
            _model = None
    return _model


def score_transaction(amount: float, from_balance: float, tx_last_hour: int,
                       is_new_payee: bool = False) -> Tuple[float, bool, str]:
    """
    Returns (fraud_score in [0,1], is_fraud, reason).
    """
    ratio = amount / (from_balance + 1)
    features = np.array([[amount, ratio, tx_last_hour, int(is_new_payee)]])

    model = _load_model()
    ml_anomaly = False
    ml_raw_score = 0.0
    if model is not None:
        # decision_function: higher = more normal, lower/negative = more anomalous
        ml_raw_score = float(model.decision_function(features)[0])
        ml_anomaly = model.predict(features)[0] == -1

    # Normalize the raw isolation-forest score to a rough 0-1 "risk" scale.
    ml_risk = float(np.clip(0.5 - ml_raw_score, 0.0, 1.0))

    reasons = []
    rule_triggered = False

    if amount >= LARGE_AMOUNT_THRESHOLD:
        rule_triggered = True
        reasons.append(f"amount>={LARGE_AMOUNT_THRESHOLD}")
    if ratio >= HIGH_DRAIN_RATIO_THRESHOLD:
        rule_triggered = True
        reasons.append(f"balance_drain_ratio>={HIGH_DRAIN_RATIO_THRESHOLD}")
    if tx_last_hour >= HIGH_VELOCITY_THRESHOLD:
        rule_triggered = True
        reasons.append(f"velocity>={HIGH_VELOCITY_THRESHOLD}/hr")
    if ml_anomaly:
        reasons.append("ml_isolation_forest_anomaly")

    final_score = max(ml_risk, 0.75 if rule_triggered else 0.0, 0.6 if ml_anomaly else 0.0)
    is_fraud = rule_triggered or ml_anomaly

    reason = ",".join(reasons) if reasons else "normal"
    return round(final_score, 4), is_fraud, reason
