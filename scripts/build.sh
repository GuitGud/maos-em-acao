#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")/.."
(cd frontend && pnpm install --frozen-lockfile && pnpm build)
./mvnw clean verify
echo 'Projeto pronto: target/maos-em-acao-1.0.0.jar'
