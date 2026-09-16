$ErrorActionPreference = 'Stop'
Push-Location (Split-Path $PSScriptRoot -Parent)
try {
    Push-Location frontend
    try {
        pnpm install --frozen-lockfile
        if ($LASTEXITCODE -ne 0) { throw 'Falha ao instalar dependências do frontend' }
        pnpm build
        if ($LASTEXITCODE -ne 0) { throw 'Falha ao compilar frontend' }
    } finally { Pop-Location }
    & .\mvnw.cmd clean verify
    if ($LASTEXITCODE -ne 0) { throw 'Falha nos testes ou empacotamento Java' }
    Write-Host 'Projeto pronto: target/maos-em-acao-1.0.0.jar'
} finally { Pop-Location }
