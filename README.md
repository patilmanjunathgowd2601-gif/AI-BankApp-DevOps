# AI-BankApp-DevOps

[![Backend CI](https://github.com/patilmanjunathgowd2601-gif/AI-BankApp-DevOps/actions/workflows/backend-ci.yml/badge.svg)](https://github.com/patilmanjunathgowd2601-gif/AI-BankApp-DevOps/actions/workflows/backend-ci.yml)
[![Frontend CI](https://github.com/patilmanjunathgowd2601-gif/AI-BankApp-DevOps/actions/workflows/frontend-ci.yml/badge.svg)](https://github.com/patilmanjunathgowd2601-gif/AI-BankApp-DevOps/actions/workflows/frontend-ci.yml)
[![AI Service CI](https://github.com/patilmanjunathgowd2601-gif/AI-BankApp-DevOps/actions/workflows/ai-service-ci.yml/badge.svg)](https://github.com/patilmanjunathgowd2601-gif/AI-BankApp-DevOps/actions/workflows/ai-service-ci.yml)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen)
![React](https://img.shields.io/badge/React-18-blue)
![Python](https://img.shields.io/badge/Python-3.12-yellow)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)
![Kubernetes](https://img.shields.io/badge/Kubernetes-EKS-326CE5)
![ArgoCD](https://img.shields.io/badge/GitOps-ArgoCD-EF7B4D)
![Terraform](https://img.shields.io/badge/IaC-Terraform-7B42BC)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

A three-tier banking application built as a hands-on DevOps portfolio project:
**React** (presentation) + **Java Spring Boot** (application) + **PostgreSQL** (data),
plus a **Python/FastAPI AI microservice** that scores every transfer for fraud risk
using an Isolation Forest anomaly model combined with explainable rule-based checks.

## Table of contents

- [Architecture](#architecture)
- [Tech stack](#tech-stack)
- [Repository structure](#repository-structure)
- [Quick start (Docker Compose)](#quick-start-docker-compose)
- [Kubernetes deployment](#kubernetes-deployment)
- [CI/CD and DevSecOps pipeline](#cicd-and-devsecops-pipeline)
- [GitOps with ArgoCD](#gitops-with-argocd)
- [Infrastructure as Code (Terraform)](#infrastructure-as-code-terraform)
- [Monitoring](#monitoring)
- [AI fraud detection](#ai-fraud-detection)

## Architecture

See [docs/architecture.md](docs/architecture.md) for the full request-flow and
pipeline diagrams. In short: the React SPA talks to the Spring Boot REST API,
which persists to PostgreSQL and calls the FastAPI fraud-detection service
synchronously on every transfer (failing open if that service is down, so an
AI-tier outage never blocks legitimate banking traffic).

## Tech stack

| Layer | Technology |
|---|---|
| Presentation | React 18, nginx (production) |
| Application | Java 17, Spring Boot 3.3, Spring Security (JWT) |
| Data | PostgreSQL 16 |
| AI / Fraud detection | Python 3.12, FastAPI, scikit-learn (Isolation Forest) |
| Containers | Docker, Docker Compose |
| Orchestration | Kubernetes (EKS), HPA, Ingress |
| CI/CD | GitHub Actions |
| Security scanning | Gitleaks, Semgrep, OWASP Dependency-Check, Trivy, OWASP ZAP |
| GitOps | ArgoCD |
| Infrastructure as Code | Terraform (VPC, EKS, ECR, RDS) |
| Monitoring | Prometheus, Grafana |

## Repository structure

```
frontend/    React SPA (nginx in production)
backend/     Spring Boot REST API (Java 17)
ai-service/  FastAPI fraud-detection microservice (Python)
database/    PostgreSQL schema
k8s/         Kubernetes manifests (namespace, deployments, services, ingress, HPA, monitoring)
gitops/argocd/  ArgoCD Application/Project manifests for GitOps deployment
terraform/   AWS infrastructure (VPC, EKS, ECR, RDS) as code
monitoring/  Prometheus + Grafana config for local docker-compose
.github/workflows/  CI (build/test/security-scan/docker push) + CD (GitOps manifest bump)
```

## Quick start (Docker Compose)

```bash
cp .env.example .env       # edit secrets if you like
docker compose up --build
```

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- AI fraud service: http://localhost:8000/docs
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3001 (admin / value of GRAFANA_ADMIN_PASSWORD)

## Kubernetes deployment

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -R -f k8s/postgres
kubectl apply -R -f k8s/backend
kubectl apply -R -f k8s/ai-service
kubectl apply -R -f k8s/frontend
kubectl apply -f k8s/ingress/ingress.yaml
kubectl apply -R -f k8s/monitoring
```

Or let ArgoCD manage it continuously — see [gitops/argocd/README.md](gitops/argocd/README.md).

## CI/CD and DevSecOps pipeline

Every push to `main` under `backend/`, `frontend/`, or `ai-service/` triggers
that service's GitHub Actions workflow, which runs tests and a security-gate
sequence before building and pushing a Docker image to GHCR:

1. Gitleaks - scans the diff for committed secrets/credentials
2. Semgrep - static application security testing (SAST)
3. OWASP Dependency-Check - flags known-vulnerable dependencies
4. Trivy - scans the built container image for OS/library CVEs
5. OWASP ZAP - dynamic scan (DAST) against a running instance

A follow-up workflow (cd-gitops.yml) then bumps the image tag in the
relevant k8s/*/deployment.yaml and pushes that change back to main -
ArgoCD picks up the git change and rolls it out to the cluster automatically.

## GitOps with ArgoCD

See gitops/argocd/README.md for bootstrap steps. ArgoCD continuously
reconciles the cluster to match the k8s/ directory in this repo, with
self-heal and prune enabled.

## Infrastructure as Code (Terraform)

```bash
cd terraform
terraform init
terraform plan
terraform apply
```

Provisions a VPC, EKS cluster, ECR repositories, and an RDS PostgreSQL instance on AWS.

## Monitoring

Prometheus scrapes the Spring Boot Actuator /actuator/prometheus endpoint
and the FastAPI /metrics endpoint; Grafana visualizes both via a
pre-provisioned dashboard.

## AI fraud detection

Every transfer is scored by the AI service (Isolation Forest + rule-based
checks for large amounts, high balance-drain ratio, and high transaction
velocity). Transactions flagged as high-risk are held with status FLAGGED
instead of moving funds, and the fraud score/reason are stored alongside the
transaction for review.
