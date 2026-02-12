# Instruções para executar o deploy.sh

## Pré-requisitos

1. **AWS CLI** instalado e configurado (`aws --version`)
2. **LocalStack** em execução (se for usar ambiente local)

## Execução com LocalStack

1. Subir o LocalStack:
   ```bash
   docker run -d -p 4566:4566 localstack/localstack
   ```
   ou, se estiver usando Docker Compose:
   ```bash
   docker compose up -d localstack
   ```

2. Rodar o script a partir do diretório do projeto:
   ```bash
   ./localstack/deploy.sh
   ```
   ou:
   ```bash
   cd localstack && ./deploy.sh
   ```

## Variáveis de ambiente (opcionais)

| Variável | Valor padrão | Uso |
|----------|--------------|-----|
| `BUCKET_NAME` | `cf-templates` | Bucket onde os templates serão enviados |
| `ENDPOINT_URL` | `http://localhost:4566` | Endpoint do LocalStack |
| `AWS_REGION` | `us-east-1` | Região |
| `STACK_NAME` | `chat-memory-stack` | Nome da stack |
| `TEMPLATE_BASE_URL` | calculado automaticamente | URL base dos templates no S3 (override manual) |

**Exemplos:**

```bash
# LocalStack em outra porta
ENDPOINT_URL=http://localhost:4567 ./localstack/deploy.sh

# Usar outro bucket
BUCKET_NAME=meus-templates ./localstack/deploy.sh

# Combinar variáveis
ENDPOINT_URL=http://localhost:4566 BUCKET_NAME=cf-templates ./localstack/deploy.sh
```

## Execução contra AWS (produção)

1. Fazer upload dos templates para um bucket S3 da conta:
   ```bash
   aws s3 mb s3://meu-bucket-cf-templates --region us-east-1  # se necessário
   aws s3 cp localstack/dynamodb/ s3://meu-bucket-cf-templates/dynamodb/ --recursive
   aws s3 cp localstack/s3/ s3://meu-bucket-cf-templates/s3/ --recursive
   aws s3 cp localstack/lambda-dynamodb-streams/ s3://meu-bucket-cf-templates/lambda-dynamodb-streams/ --recursive
   ```

2. Executar o deploy sem usar o endpoint do LocalStack:
   ```bash
   ENDPOINT_URL="" \
   TEMPLATE_BASE_URL="https://s3.us-east-1.amazonaws.com/meu-bucket-cf-templates" \
   ./localstack/deploy.sh
   ```

## Resultado esperado

O script envia os templates para o S3, cria/atualiza a stack e exibe os outputs da stack no final.
