FROM python:3.12-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY app ./app
COPY app/train_model.py ./train_model.py

# Bake a trained model into the image so the service is ready to score
# transactions immediately on startup (no cold-start training needed).
RUN python train_model.py

# Numeric UID, not just a named user -- see the matching comment in
# backend/Dockerfile for why this is required for runAsNonRoot: true to work.
RUN useradd --create-home --uid 1000 appuser
USER 1000

EXPOSE 8000
HEALTHCHECK --interval=30s --timeout=3s --start-period=15s \
  CMD python -c "import urllib.request; urllib.request.urlopen('http://localhost:8000/health')" || exit 1

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
