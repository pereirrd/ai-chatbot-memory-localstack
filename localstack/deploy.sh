#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUCKET_NAME="${BUCKET_NAME:-cf-templates}"
ENDPOINT_URL="${ENDPOINT_URL:-http://localhost:4566}"
REGION="${AWS_REGION:-us-east-1}"
STACK_NAME="${STACK_NAME:-chat-memory-stack}"

# Para LocalStack usa path-style; para AWS usa formato padrão
if [[ "$ENDPOINT_URL" == *"localhost"* ]] || [[ "$ENDPOINT_URL" == *"localstack"* ]]; then
  TEMPLATE_BASE_URL="${TEMPLATE_BASE_URL:-${ENDPOINT_URL}/${BUCKET_NAME}}"
else
  TEMPLATE_BASE_URL="${TEMPLATE_BASE_URL:-https://s3.${REGION}.amazonaws.com/${BUCKET_NAME}}"
fi

echo "=== Deploy Chat Memory Stack ==="
echo "Endpoint: $ENDPOINT_URL"
echo "Bucket: $BUCKET_NAME"
echo "TemplateBaseUrl: $TEMPLATE_BASE_URL"
echo ""

# 1. Criar bucket e fazer upload dos templates
echo ">>> Criando bucket e fazendo upload dos templates..."
aws s3 mb "s3://${BUCKET_NAME}" --endpoint-url "$ENDPOINT_URL" 2>/dev/null || true
aws s3 cp "${SCRIPT_DIR}/dynamodb/chat-memory-table.yaml" "s3://${BUCKET_NAME}/dynamodb/chat-memory-table.yaml" --endpoint-url "$ENDPOINT_URL"
aws s3 cp "${SCRIPT_DIR}/s3/chat-memory-history.yaml" "s3://${BUCKET_NAME}/s3/chat-memory-history.yaml" --endpoint-url "$ENDPOINT_URL"
aws s3 cp "${SCRIPT_DIR}/lambda-dynamodb-streams/chat-memory-stream-to-s3.yaml" "s3://${BUCKET_NAME}/lambda-dynamodb-streams/chat-memory-stream-to-s3.yaml" --endpoint-url "$ENDPOINT_URL"
echo ">>> Upload concluído."
echo ""

# 2. Deploy da stack principal
echo ">>> Deploy da stack ${STACK_NAME}..."
if aws cloudformation describe-stacks --stack-name "$STACK_NAME" --endpoint-url "$ENDPOINT_URL" &>/dev/null; then
  echo ">>> Stack existente - executando update..."
  aws cloudformation update-stack \
    --stack-name "$STACK_NAME" \
    --template-body "file://${SCRIPT_DIR}/stack.yaml" \
    --parameters "ParameterKey=TemplateBaseUrl,ParameterValue=${TEMPLATE_BASE_URL}" \
    --capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM \
    --endpoint-url "$ENDPOINT_URL"
  aws cloudformation wait stack-update-complete --stack-name "$STACK_NAME" --endpoint-url "$ENDPOINT_URL"
else
  echo ">>> Criando nova stack..."
  aws cloudformation create-stack \
    --stack-name "$STACK_NAME" \
    --template-body "file://${SCRIPT_DIR}/stack.yaml" \
    --parameters "ParameterKey=TemplateBaseUrl,ParameterValue=${TEMPLATE_BASE_URL}" \
    --capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM \
    --endpoint-url "$ENDPOINT_URL"
  aws cloudformation wait stack-create-complete --stack-name "$STACK_NAME" --endpoint-url "$ENDPOINT_URL"
fi

echo ""
echo "=== Stack deploy concluída com sucesso ==="
aws cloudformation describe-stacks --stack-name "$STACK_NAME" --endpoint-url "$ENDPOINT_URL" --query 'Stacks[0].Outputs' --output table 2>/dev/null || true
