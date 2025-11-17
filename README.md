# WebLogic + OpenAI Analyzer

Projeto pronto para deploy no WebLogic, Docker e Kubernetes (WKO).

## Conteúdo
- Aplicação Java (WAR) que envia logs para OpenAI e retorna diagnósticos.
- `deploy-wlst.py` script para deploy via WLST.
- `Dockerfile` e `docker-compose.yml` para levantar AdminServer localmente.
- Exemplo Model-in-Image (MII) para WKO (kubernetes/mii/*).
- GitHub Actions CI/CD em `.github/workflows/ci-cd.yml`.
- Scripts de health/readiness/liveness em `scripts/`.

## Instruções rápidas
### Build local
```bash
mvn clean package
```

### Deploy com WLST (local admin)
```bash
# ajustar adminUrl, adminUser e adminPass em deploy-wlst.py
java weblogic.WLST deploy-wlst.py
```

### Docker Compose (dev)
```bash
docker build -t weblogic-openai .
docker-compose up --build
```

### Model-in-Image (MII) - WKO
1. Ajuste `kubernetes/mii/model.yaml` com sua configuração de domínio.
2. Construa a imagem MII:
```bash
docker build -f kubernetes/mii/Dockerfile.mii -t <registry>/weblogic-openai:mii .
```
3. Faça push para seu registry e ajuste `kubernetes/domain.yaml` para usar a imagem.
4. Aplique com `kubectl apply -f kubernetes/domain.yaml` (assumindo WKO e operator instalados).

### GitHub Actions
- Configure secrets no repositório: `DOCKER_REGISTRY`, `DOCKER_USERNAME`, `DOCKER_PASSWORD`, `KUBE_CONFIG` (base64 ou conteúdo do kubeconfig), `K8S_DEPLOY` = 'true' para deploy automático.

### Probes
- `scripts/healthcheck.sh` — healthcheck simples para admin console
- `scripts/readiness.sh` — checa admin + app
- `scripts/liveness.sh` — checa porta do admin

## Segurança
- NÃO comite `OPENAI_API_KEY` ou credenciais no repositório.
- Use secrets do GitHub e secret management no cluster.

