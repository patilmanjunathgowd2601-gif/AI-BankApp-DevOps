"""
Trains a lightweight Isolation Forest anomaly-detection model on synthetic
banking transaction data and saves it to model.joblib.

Features used per transaction:
  1. amount
  2. amount / (from_account_balance + 1)   -> how much of the balance is being drained
  3. transactions_last_hour                -> velocity signal
  4. is_new_payee (0/1)                    -> simplistic proxy, randomized in synthetic data

Run:
    python train_model.py
"""
import numpy as np
from sklearn.ensemble import IsolationForest
import joblib

RANDOM_STATE = 42
rng = np.random.default_rng(RANDOM_STATE)

N_NORMAL = 4000
N_ANOMALOUS = 200

# Amounts/balances below are in INR (Indian Rupees), scaled ~50x from the
# original USD-cent synthetic data so "normal" reflects typical rupee-value
# transfers against a Rs 10,000-1,000,000 balance range.

# Normal transactions: modest amounts relative to balance, low velocity
normal_amount = rng.gamma(shape=2.0, scale=2500, size=N_NORMAL)
normal_balance = rng.uniform(10000, 1000000, size=N_NORMAL)
normal_ratio = normal_amount / (normal_balance + 1)
normal_velocity = rng.poisson(1.0, size=N_NORMAL)
normal_new_payee = rng.binomial(1, 0.1, size=N_NORMAL)

# Anomalous transactions: large amounts, high balance-drain ratio, high velocity
anom_amount = rng.gamma(shape=6.0, scale=40000, size=N_ANOMALOUS)
anom_balance = rng.uniform(10000, 1000000, size=N_ANOMALOUS)
anom_ratio = np.clip(anom_amount / (anom_balance + 1), 0, 5)
anom_velocity = rng.poisson(6.0, size=N_ANOMALOUS)
anom_new_payee = rng.binomial(1, 0.7, size=N_ANOMALOUS)

X_normal = np.column_stack([normal_amount, normal_ratio, normal_velocity, normal_new_payee])
X_anom = np.column_stack([anom_amount, anom_ratio, anom_velocity, anom_new_payee])
X = np.vstack([X_normal, X_anom])

model = IsolationForest(
    n_estimators=200,
    contamination=0.05,
    random_state=RANDOM_STATE,
)
model.fit(X)

joblib.dump(model, "model.joblib")
print(f"Model trained on {len(X)} synthetic samples and saved to model.joblib")
