# Evidências de validação

## Revalidação em 14/10/2026

Build Angular, 53 testes Java/Cucumber, 1 validação CML e 5 testes Playwright/Chrome passaram novamente: **59 verificações aprovadas**. JAR iniciado com banco H2 novo em memória para o navegador. PostgreSQL e Docker Compose não foram reexecutados nesta data.

Um caso exploratório adicional encontrou um defeito fora da cobertura atual: 220 kg e 2 kits são consolidados no relatório como 222 unidades. Portanto, a aprovação da suíte não representa aprovação integral dos requisitos. Veja a [auditoria detalhada](AUDITORIA_DE_REQUISITOS.md), com comparação ao PDF original, reprodução e pendências acadêmicas.

## Registro histórico de 16/09/2026

Validação local realizada em **16/09/2026**, em Windows, JDK 25, Maven 3.9.11, Node.js 24.19.0, pnpm 11.19.0 e Google Chrome. O código Java tem target 21. Dados de testes são fictícios e isolados dos dados persistentes padrão.

## Resultados executados

| Verificação | Resultado |
|---|---|
| Compilação de produção Angular | Sucesso; bundle inicial aproximadamente 214 kB, 58 kB de transferência estimada |
| `ApplicationTest` | 21 testes aprovados |
| `ArchitectureTest` | 1 teste aprovado |
| `ConcurrencyTest` | 3 testes aprovados |
| `DomainTest` | 9 testes aprovados |
| `RunCucumberTest` | 19 cenários aprovados |
| **Total backend** | **53 testes, 0 falhas, 0 erros, 0 ignorados** |
| Validação oficial do CML | 1 teste aprovado, sem erro sintático ou semântico |
| Playwright / Chrome | 5 testes aprovados |
| JAR com Angular incluído | Empacotado e iniciado com sucesso |
| Capturas de UI | 11 telas, desktop 1440px e mobile 390px; script sem erro JavaScript ou bloqueio CSP |

Total: **59 verificações automatizadas aprovadas** entre backend, CML e E2E. O número se refere a casos executados, não a uma promessa de ausência de defeitos.

## Validação no GitHub Actions

O [workflow 35101178309](https://github.com/GuitGud/maos-em-acao/actions/runs/35101178309), executado sobre o commit de implementação `28b8e9c`, terminou com **sucesso nos dois jobs** em 16/09/2026:

- `test`: Angular, 53 testes Java/Cucumber, validador CML, cinco testes Playwright/Chromium e publicação dos relatórios.
- `postgres`: os 21 testes de aplicação/API e 19 cenários Cucumber passaram também com **PostgreSQL 17 real** no serviço do runner Linux, em Java 21.

As 40 execuções no PostgreSQL repetem casos já contabilizados; não são 40 funcionalidades adicionais. A configuração completa do Docker Compose não foi executada localmente.

## Cobertura Java (JaCoCo)

Na execução de `verify`:

- Linhas gerais: **384/437 (87,9%)**.
- Linhas de domínio: **58/58 (100%)**.
- Linhas da aplicação: **120/122 (98,4%)**.
- Branches gerais: **244/333 (73,3%)**.

Métricas medem a suíte Java; os fluxos Playwright não foram instrumentados pelo JaCoCo. Cobertura não representa cobertura total de requisitos. O relatório reproduzível está em `target/site/jacoco/index.html` após `verify`; não se versionam relatórios binários ou pastas de build.

## O que os testes realmente verificam

- Invariantes de saldo, overflow e transições de estado.
- Publicação condicionada a necessidades e encerramento sem turnos abertos.
- Aprovação, capacidade, duplicidade, conflito de horário e reinscrição.
- Notificações após alteração e proteção por proprietário.
- Presença única, horas calculadas e feedback elegível.
- Autorização para entrega, estorno único e histórico consistente.
- Corrida real de duas transações pela última vaga e pelo mesmo estoque.
- Rollback de uma operação que grava saldo e movimento antes de falhar.
- Senhas protegidas, dados sensíveis ausentes dos DTOs, sessão e CSRF.
- Direção das dependências da arquitetura limpa.
- Uso dos cinco padrões no domínio/aplicação.
- Fluxos completos pelo Angular: campanha até estorno; cadastro até notificação; relatório persistente; responsividade e escape de HTML.

## Correções encontradas durante a verificação

1. Nome de bean da configuração de segurança conflitava com seu método fábrica: renomeado.
2. `email` é palavra reservada no CML: atributo do modelo renomeado para `enderecoEmail`.
3. Ícones compunham o nome acessível da navegação: botões receberam rótulo explícito.
4. Otimização de CSS usava evento inline bloqueado pela CSP: desabilitada a extração inline de CSS crítico; a política de scripts permanece sem `unsafe-inline`.
5. Seletor de teste de `<select>` foi ajustado para usar o papel acessível `combobox`.

Todas as suítes afetadas foram executadas novamente após suas respectivas correções.

## Ambientes e limites

H2 em arquivo está configurado para uso normal; os testes locais automatizados e o servidor E2E usaram H2 em memória, com migrações reais. O Docker não estava disponível na máquina para execução local. PostgreSQL 17 foi validado pelo job dedicado do GitHub Actions, conforme a execução bem-sucedida vinculada acima.

Não foram feitos testes de carga, pentest, certificação formal de acessibilidade ou operação em produção. A galeria contém capturas do estado demo anterior à execução E2E; os testes criam registros extras em seu banco isolado.

## Reproduzir

Todos os comandos estão em [FUNCIONALIDADES_E_TESTES.md](../FUNCIONALIDADES_E_TESTES.md). A automação no GitHub Actions está em `.github/workflows/ci.yml`; seus relatórios ficam no artefato `evidencias-de-testes`.
