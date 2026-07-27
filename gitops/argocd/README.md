# ArgoCD GitOps Setup

1. Install ArgoCD on your cluster:
```bash
   kubectl create namespace argocd
   kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
```

2. `repoURL` in `application.yaml` and `project.yaml` already points at
   https://github.com/patilmanjunathgowd2601-gif/AI-BankApp-DevOps.git -- update it if you fork this elsewhere.

3. Bootstrap the project and root application:
```bash
   kubectl apply -f gitops/argocd/project.yaml
   kubectl apply -f gitops/argocd/application.yaml
```

4. ArgoCD will now continuously sync the `k8s/` directory to the `ai-bankapp`
   namespace. The `cd-gitops.yml` GitHub Actions workflow updates image tags
   in `k8s/*/deployment.yaml` after each successful CI build, so every merge
   to `main` automatically rolls out via ArgoCD -- self-healing and pruning
   are enabled, so manual drift in the cluster gets reverted automatically.
