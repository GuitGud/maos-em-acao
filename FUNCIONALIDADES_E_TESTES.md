# Funcionalidades, execução e testes

## 1. Preparação

Instale JDK 21 (ou 25), Node.js 24 e pnpm 11.19.0. O Maven Wrapper fixa Maven 3.9.11 e faz o download inicial automaticamente. Use um terminal na raiz do repositório.

```powershell
java -version
node --version
npm install --global pnpm@11.19.0
pnpm --version
```

Se o Wrapper solicitar `JAVA_HOME`, aponte para a pasta do JDK. Exemplo para a máquina em que o projeto foi preparado:

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-25'
```

## 2. Compilar e executar a aplicação completa

### Windows / PowerShell

```powershell
.\scripts\build.ps1
$env:DEMO_MODE='true'
java -jar target/maos-em-acao-1.0.0.jar
```

Se a política local bloquear scripts PowerShell, execute os mesmos passos manualmente:

```powershell
cd frontend
pnpm install --frozen-lockfile
pnpm build
cd ..
.\mvnw.cmd clean verify
$env:DEMO_MODE='true'
java -jar target/maos-em-acao-1.0.0.jar
```

### Linux / macOS

```bash
chmod +x mvnw scripts/build.sh
./scripts/build.sh
DEMO_MODE=true java -jar target/maos-em-acao-1.0.0.jar
```

Acesse `http://localhost:8080`. Pare com **Ctrl+C**. O banco padrão é H2 em arquivo (`data/maos.mv.db`), com migração Flyway e validação JPA. Os testes automatizados usam bancos em memória separados; não apagam seus dados locais.

### Contas de demonstração

| Perfil | E-mail | Senha |
|---|---|---|
| Gestor | `admin@maosemacao.local` | `DemoGestor2026!` |
| Voluntária | `ana@example.test` | `DemoVoluntario2026!` |

Os dados demo incluem campanha publicada, campanha em rascunho, itens, doações, um turno futuro e uma inscrição. Só são criados quando o banco está vazio. Alterar variáveis depois não troca a senha de contas existentes. Para uma nova demonstração sem afetar dados salvos, use outro `DB_URL` em memória.

### Iniciar sem dados de demonstração

```powershell
$env:DEMO_MODE='false'
$env:ADMIN_EMAIL='gestao@minhaong.org'
$env:ADMIN_PASSWORD='defina-uma-senha-forte-aqui'
$env:DB_URL='jdbc:h2:file:./data/minha-ong;MODE=PostgreSQL;DB_CLOSE_ON_EXIT=FALSE'
java -jar target/maos-em-acao-1.0.0.jar
```

A senha inicial da gestão precisa ter pelo menos 12 caracteres. Não publique senhas reais ou `.env`. Cadastros feitos pela interface sempre recebem papel de voluntário e ficam pendentes de aprovação. Configure o administrador antes de permitir cadastros públicos.

### Desenvolvimento com recarga do Angular

Terminal 1, raiz:

```powershell
$env:DEMO_MODE='true'
.\mvnw.cmd spring-boot:run
```

Terminal 2:

```powershell
cd frontend
pnpm start
```

Abra `http://127.0.0.1:4200`. O proxy encaminha `/api` ao Spring em 8080. O Angular usa horário local nos formulários e converte para UTC; o backend e os dados armazenados trabalham em UTC.

### PostgreSQL com Docker

```powershell
Copy-Item .env.example .env
# Edite as senhas no .env antes de iniciar.
docker compose up --build
```

O Compose sobe PostgreSQL 17 e o aplicativo em `http://localhost:8080`; os dados persistem no volume `postgres_data`. `docker compose down` para os serviços e preserva o volume. O Dockerfile executa os testes Java antes de construir a imagem final. A configuração Docker é fornecida; consulte o registro de validação para saber quais ambientes foram efetivamente testados.

## 3. Funcionalidades e regras

| Área | O que fazer pela interface | Regras principais |
|---|---|---|
| Conta | Cadastrar, entrar, sair, editar nome | E-mail único, senha BCrypt, cadastro pendente, sessões autenticadas e CSRF |
| Aprovação | Gestão aprova voluntários | Apenas gestão; aprovação cria notificação; cadastro não se autoaprova |
| Campanhas | Criar, editar, definir necessidades, publicar, encerrar | Rascunho precisa de necessidade; encerrada não pode ser editada; turnos abertos impedem encerramento |
| Necessidades | Escolher item e definir/substituir meta | Meta positiva; uma necessidade por par campanha/item; campanha encerrada bloqueia alteração |
| Catálogo | Cadastrar itens e unidades | Nome único, saldo inicial zero; itens são compartilhados pela ONG |
| Doações | Registrar origem, campanha, item e quantidade | Campanha publicada, quantidade positiva, entrada e histórico gravados na mesma transação |
| Ajustes | Corrigir estoque com quantidade positiva ou negativa e motivo | Quantidade não nula, saldo não negativo, justificativa obrigatória |
| Turnos | Criar, editar, cancelar, concluir | Intervalo futuro válido, máximo de 24h, 1–1000 vagas; não reduzir abaixo da ocupação; concluir só após término |
| Inscrição | Inscrever-se, cancelar antes do início e reinscrever-se | Aprovação, disponibilidade, ausência de duplicidade e de conflito de horário; capacidade protegida contra concorrência |
| Presença | Gestão marca presente ou ausente após conclusão | Registro único; horas derivadas da duração do turno, não de valor digitado pelo voluntário |
| Entregas | Registrar campanha, item, quantidade, destino e prioridade | Baixa atômica de estoque; gestor ou voluntário aprovado participante da campanha; destino por código |
| Estorno | Devolver integralmente os itens de uma entrega ao saldo | Somente gestão; motivo obrigatório; uma única vez; entrega original e histórico preservados |
| Notificações | Consultar caixa de entrada e marcar como lida | Criadas por aprovação e alterações de campanha/turno; leitura protegida por destinatário |
| Relatórios | Salvar balanço, baixar TXT e excluir relatório salvo | Somente gestão via Proxy; soma doações, saldo, entregas não estornadas e horas; histórico não muda retroativamente |
| Feedback | Registrar ou editar nota e comentário após a ação | Presença confirmada, nota 1–5, uma avaliação por inscrição |
| Reconhecimento | Ver selos de participação | Decorator adiciona participação com horas > 0 e dedicação com horas >= 20 |

**Estoque compartilhado:** as movimentações registram a campanha de origem/destino, mas o saldo pertence ao item da ONG; não existe reserva exclusiva por campanha. A meta acompanha doações recebidas e não é reduzida por distribuições ou estornos. Quantidades são inteiras na unidade cadastrada; para frações, cadastre unidade menor (ex.: gramas).

**Totais por unidade:** os novos relatórios e os indicadores de estoque/entregas agrupam somente itens com a mesma unidade cadastrada. Exemplo: `220 kg; 2 kit`. Unidades diferentes não são somadas nem convertidas automaticamente: `g`, `kg` e grafias diferentes permanecem separadas. Doações incluem apenas entradas de tipo `DOACAO`; ajustes afetam o saldo e entregas estornadas não entram no total entregue. Unidades cadastradas sem movimentação aparecem com zero.

**Relatórios anteriores à correção de 14/10/2026:** são snapshots históricos e mantêm seu conteúdo, inclusive o total antigo. Gere um novo relatório para obter os totais atuais separados por unidade; isso não reconstrói retroativamente o saldo de uma data passada.

**Prioridade:** `VULNERABILIDADE = vulnerabilidade × 100 + membros`; `TAMANHO_FAMILIA = membros × 100 + vulnerabilidade`. A pontuação fica registrada; a gestão decide a ordem de atendimento. Não há fila automática nem recomendação social baseada em dados reais.

**Privacidade:** o voluntário vê somente seu perfil, inscrições, entregas próprias, avaliações próprias e notificações. Gestão vê dados operacionais. Rascunhos e relatórios administrativos não são retornados ao voluntário. Famílias são identificadas por código livre; não há cadastro de beneficiários com dados pessoais.

## 4. Comandos de testes

### Suíte completa de backend

```powershell
.\mvnw.cmd clean verify
```

Executa JUnit (domínio, aplicação, API, segurança, concorrência, arquitetura), os cenários Cucumber e o relatório JaCoCo; empacota o JAR. Para incluir a interface atualizada, faça `pnpm build` antes, ou use `scripts/build.ps1`.

| Comando PowerShell | Finalidade |
|---|---|
| `.\mvnw.cmd test` | Todos os testes Java e Cucumber |
| `.\mvnw.cmd '-Dtest=DomainTest' test` | Invariantes e padrões no domínio |
| `.\mvnw.cmd '-Dtest=ApplicationTest' test` | Casos de uso, HTTP, autorização e validação |
| `.\mvnw.cmd '-Dtest=ConcurrencyTest' test` | Última vaga, estoque simultâneo e rollback |
| `.\mvnw.cmd '-Dtest=ArchitectureTest' test` | Direção das dependências pela arquitetura limpa |
| `.\mvnw.cmd '-Dtest=RunCucumberTest' test` | Cenários BDD em português |
| `.\mvnw.cmd -f tools/cml-validation/pom.xml test` | Parser e validador semântico oficial do Context Mapper |

Linux/macOS: substitua `.\mvnw.cmd` por `./mvnw`. O validador CML é um módulo isolado para manter suas dependências fora do aplicativo.

Resultados gerados:

- `target/surefire-reports/`: relatórios JUnit em XML e texto.
- `target/cucumber.html`: relatório BDD navegável.
- `target/cucumber.json`: relatório BDD estruturado.
- `target/site/jacoco/index.html`: cobertura, gerada por `verify`.
- `tools/cml-validation/target/surefire-reports/`: validação do CML.

### Testes de navegador (Playwright)

Use um banco isolado: os testes cadastram e alteram registros de demonstração.

Terminal 1, depois do build:

```powershell
$env:DEMO_MODE='true'
$env:DB_URL='jdbc:h2:mem:e2e;MODE=PostgreSQL;DB_CLOSE_DELAY=-1'
java -jar target/maos-em-acao-1.0.0.jar
```

Terminal 2:

```powershell
cd frontend
pnpm test
```

Por padrão os testes usam o Google Chrome instalado. Como alternativa, use o Chromium do Playwright:

```powershell
pnpm exec playwright install chromium
$env:PLAYWRIGHT_CHANNEL='chromium'
pnpm test
```

Se a aplicação usar outra porta:

```powershell
$env:BASE_URL='http://127.0.0.1:8087'
pnpm test
```

Para abrir o relatório:

```powershell
pnpm exec playwright show-report
```

Os seis testes E2E cobrem campanha/meta/publicação/doação/entrega/estorno; cadastro/aprovação/inscrição/aviso/cancelamento; relatório persistente; responsividade móvel; escape de conteúdo HTML; e totais por unidade no relatório, download TXT e painel, incluindo estorno e preservação do snapshot. Asserções e ações passam pela interface Angular e API real. Eles não substituem os cenários Cucumber.

## 5. Roteiro para demonstrar ao professor

1. Entrar como gestor; apresentar painel e campanhas.
2. Criar item, campanha e necessidade; publicar.
3. Registrar uma doação de 10 unidades; conferir histórico e saldo.
4. Criar turno futuro com uma vaga. Cadastrar voluntário em outra sessão e aprová-lo.
5. Voluntário se inscreve; gestão altera turno; voluntário recebe aviso.
6. Registrar entrega de 3 unidades; tentar entregar acima do saldo; mostrar recusa.
7. Estornar a entrega; mostrar saldo restaurado e trilha preservada.
8. Salvar e baixar relatório. Mostrar bloqueio de relatórios ao voluntário.
9. Executar Cucumber e abrir relatório HTML; explicar concorrência, DDD, CML e cinco padrões.
10. Apresentar a declaração de IA e a situação de trabalho individual.

## 6. Limites da entrega

**Correção da auditoria de 14/10/2026:** os totais passaram a ser separados por unidade. O registro do defeito original e de sua resolução está em [AUDITORIA_DE_REQUISITOS.md](docs/AUDITORIA_DE_REQUISITOS.md).

Não inclui multi-ONG, recuperação de senha, mensagens externas, anexos, pagamento, entregas parciais de uma mesma requisição, geolocalização ou fila automática de prioridade. A caixa de notificações atualiza ao carregar a página ou concluir uma ação; não há push. O endpoint de estado carrega coleções completas e foi projetado para volume acadêmico; bases grandes exigem paginação. A interface inclui labels, estados de erro, foco visível e layout móvel; não é uma certificação formal de acessibilidade.

Para uma implantação pública real, configure HTTPS, cookies seguros (`SERVER_SERVLET_SESSION_COOKIE_SECURE=true`), política de backups e operação, e avalie os requisitos reais da ONG. As senhas de demonstração existem exclusivamente para avaliação local.
