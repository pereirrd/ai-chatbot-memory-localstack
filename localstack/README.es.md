[English](README.md) | [Português](README.pt-br.md)

# Instrucciones para ejecutar deploy.sh

## Requisitos previos

1. **AWS CLI** instalado y configurado (`aws --version`)
2. **LocalStack** en ejecución (si se usa entorno local)

## Ejecución con LocalStack

1. Iniciar LocalStack:
   ```bash
   localstack start
   ```
   o, si se usa Docker Compose:
   ```bash
   docker compose up -d localstack
   ```

2. Ejecutar el script desde el directorio del proyecto:
   ```bash
   ./localstack/deploy.sh
   ```
   o:
   ```bash
   cd localstack && ./deploy.sh
   ```

## Variables de entorno (opcionales)

| Variable | Valor por defecto | Uso |
|----------|-------------------|-----|
| `BUCKET_NAME` | `cf-templates` | Bucket donde se subirán los templates |
| `ENDPOINT_URL` | `http://localhost:4566` | Endpoint de LocalStack |
| `AWS_REGION` | `us-east-1` | Región |
| `STACK_NAME` | `chat-memory-stack` | Nombre de la stack |
| `TEMPLATE_BASE_URL` | calculado automáticamente | URL base de los templates en S3 (override manual) |

**Ejemplos:**

```bash
# LocalStack en otro puerto
ENDPOINT_URL=http://localhost:4567 ./localstack/deploy.sh

# Usar otro bucket
BUCKET_NAME=meus-templates ./localstack/deploy.sh

# Combinar variables
ENDPOINT_URL=http://localhost:4566 BUCKET_NAME=cf-templates ./localstack/deploy.sh
```

## Ejecución contra AWS (producción)

1. Subir los templates a un bucket S3 de la cuenta:
   ```bash
   aws s3 mb s3://meu-bucket-cf-templates --region us-east-1  # si es necesario
   aws s3 cp localstack/dynamodb/ s3://meu-bucket-cf-templates/dynamodb/ --recursive
   aws s3 cp localstack/s3/ s3://meu-bucket-cf-templates/s3/ --recursive
   aws s3 cp localstack/lambda-dynamodb-streams/ s3://meu-bucket-cf-templates/lambda-dynamodb-streams/ --recursive
   ```

2. Ejecutar el deploy sin usar el endpoint de LocalStack:
   ```bash
   ENDPOINT_URL="" \
   TEMPLATE_BASE_URL="https://s3.us-east-1.amazonaws.com/meu-bucket-cf-templates" \
   ./localstack/deploy.sh
   ```

## Resultado esperado

El script sube los templates al S3, crea/actualiza la stack y muestra los outputs de la stack al final.

```
=== Deploy Chat Memory Stack ===
Endpoint: http://localhost:4566
Bucket: cf-templates
TemplateBaseUrl: http://localhost:4566/cf-templates

>>> Criando bucket e fazendo upload dos templates...
make_bucket: cf-templates
upload: localstack/dynamodb/chat-memory-table.yaml to s3://cf-templates/dynamodb/chat-memory-table.yaml
upload: localstack/s3/chat-memory-history.yaml to s3://cf-templates/s3/chat-memory-history.yaml
upload: localstack/lambda-dynamodb-streams/chat-memory-stream-to-s3.yaml to s3://cf-templates/lambda-dynamodb-streams/chat-memory-stream-to-s3.yaml
>>> Upload concluído.

>>> Deploy da stack chat-memory-stack...
>>> Criando nova stack...
{
    "StackId": "arn:aws:cloudformation:us-east-1:000000000000:stack/chat-memory-stack/f1440f46-eff0-4ba6-8428-3f56b4301069"
}

=== Stack deploy concluída com sucesso ===
-------------------------------------------------------------------------------------------------------------
|                                              DescribeStacks                                               |
+-------------------------------+---------------------------------------------------------------------------+
|           OutputKey           |                                OutputValue                                |
+-------------------------------+---------------------------------------------------------------------------+
|  ChatMemoryHistoryBucketName  |  chat-memory-history                                                      |
|  ChatMemoryStreamProcessorArn |  arn:aws:lambda:us-east-1:000000000000:function:chat-memory-stream-to-s3  |
|  ChatMemoryTableName          |  ChatMemory                                                               |
+-------------------------------+---------------------------------------------------------------------------+
```
