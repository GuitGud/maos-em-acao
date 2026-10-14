# Auditoria dos requisitos iniciais

Data: **14/10/2026**. Base examinada: commit `2e10d77`, comparado diretamente com as seis páginas de **Trabalho em grupo.pdf** fornecido pelo responsável, com o pedido inicial e com a documentação do projeto. As alterações desta auditoria são documentais e de versionamento; o código funcional não foi alterado.

## Conclusão

**Não é possível declarar conformidade integral.** Os artefatos técnicos exigidos estão presentes, a aplicação compila e as 59 verificações automatizadas passaram novamente. Permanecem duas pendências acadêmicas: a equipe tem um integrante e o registro completo das interações com IA ainda precisa ser entregue ao professor. A classificação de complexidade também depende da avaliação docente.

Foi identificado e reproduzido um defeito funcional nos totais dos relatórios: somam quantidades de unidades diferentes. A existência de testes aprovados não elimina esse achado. A correção do cálculo não faz parte das alterações realizadas nesta auditoria.

## Comparação com o PDF original

“Atendido” abaixo significa evidência técnica encontrada na revisão, não aprovação ou nota do professor. “Pendente” indica uma condição não demonstrada ou uma divergência literal.

| Exigência e página do PDF | Resultado | Evidência e limite |
|---|---|---|
| Sistema web (p. 1) | Atendido | Angular em `frontend/src`, API Spring MVC e JAR com interface; build e testes de navegador aprovados. |
| Banco relacional (p. 1) | Atendido | Migração Flyway com 11 tabelas, PKs, FKs e constraints; H2 executado nesta auditoria. PostgreSQL possui evidência anterior na CI. |
| Carga por integrante: 2 altas, ou 1 média + 2 baixas, ou 4 baixas (pp. 1–2) | Condicionado à avaliação docente | Campanhas, estoque, turnos e distribuição são quatro fluxos candidatos a alta complexidade. Justificativas em [REQUISITOS.md](REQUISITOS.md). Isso não substitui aprovação da classificação nem o tamanho mínimo do grupo. |
| Funcionalidades completas, com escrita (p. 2) | Atendido nos fluxos centrais; ressalva nos relatórios | Casos de uso, persistência, interface e testes para campanhas, estoque, turnos e entregas. Relatórios têm escrita real, mas o total de unidades heterogêneas precisa de correção. |
| Grupo de 4 a 6 integrantes (p. 2) | **Não atendido literalmente** | O responsável informou um integrante. É necessário obter autorização explícita para trabalho individual ou constituir equipe com contribuições reais. |
| Publicação dos artefatos no GitHub, público ou acessível ao professor (pp. 2–3) | Público confirmado; ressalva documental | API do GitHub confirmou `private=false`. Código, documentação, CML, protótipos e apresentação estão versionados. O registro de interações solicitado para remoção fica somente local; sua entrega por outro meio deve ser acordada com o professor. |
| Java (p. 3) | Atendido | Código e build Java com target 21; execução local no JDK 25. |
| Spring Boot, JPA e Angular/Vaadin (p. 3) | Tecnologias sugeridas utilizadas | Spring Boot, JPA/Hibernate e Angular presentes. O PDF as apresenta como sugestões, não como três obrigações adicionais. |
| Descrição de domínio com linguagem onipresente (p. 3) | Atendido | [DOMINIO_E_DDD.md](DOMINIO_E_DDD.md), glossário e correspondência com entidades/tabelas. Não se alegam entrevistas reais. |
| Mapa da história do usuário (p. 3) | Atendido | [MAPA_DE_HISTORIAS.md](MAPA_DE_HISTORIAS.md): seis atividades, três incrementos, 16 histórias e critérios. Mapa editável também na apresentação. |
| Protótipos de alta fidelidade (p. 3) | Atendido | Onze capturas, galeria HTML e interface Angular executável em [prototipos](prototipos/README.md). |
| Modelo Context Mapper em CML (p. 4) | Atendido | [maos-em-acao.cml](modelo/maos-em-acao.cml): cinco contextos lógicos, relações, agregados e entidades. Parser/validador oficial passou. |
| Cenários BDD (p. 4) | Atendido | Três arquivos `.feature` em português para campanhas, estoque/distribuição e voluntariado. |
| Automação BDD com Cucumber (p. 4) | Atendido | `RunCucumberTest`, `Steps` e 19 cenários executados sem falhas; integração com casos de uso e persistência. |
| DDD preliminar, estratégico, tático e operacional (p. 4) | Evidência técnica presente | Limites e linguagem; contextos e relações; entidades, agregados e invariantes; casos de uso, transações e repositório. Contextos são lógicos em um monólito; não há isolamento por módulos independentes. A avaliação qualitativa cabe ao professor. |
| Arquitetura limpa (p. 4) | Atendido | Domínio e aplicação independentes de Spring/JPA/HTTP, porta `Store`, adaptador `JpaStore`; ArchUnit aprovado. |
| Pelo menos quatro padrões da lista (p. 5) | Atendido: cinco padrões | Strategy, Observer, Decorator, Iterator e Proxy efetivamente chamados pela aplicação; detalhes abaixo. Template Method não é necessário para atingir quatro. |
| Persistência com ORM (p. 5) | Atendido | Onze entidades JPA `*Row`, `EntityManager`, conversão explícita para domínio, `@Version` e Hibernate. |
| Camada de apresentação web (p. 6) | Atendido | Formulários Angular, ações por perfil, mensagens e interface móvel; cinco fluxos Playwright aprovados. |
| Não uso de IA ou entrega das interações (p. 6) | **Entrega pendente de comprovação** | Houve uso de IA e ele está declarado em [USO_DE_IA.md](USO_DE_IA.md). Pedido e resumo não são o registro completo. A remoção do arquivo do GitHub não elimina a exigência de entrega ao professor. |

## Padrões conferidos no código

| Padrão | Implementação e chamada real |
|---|---|
| Strategy | `domain/PriorityStrategy.java`; seleção em `Actions.deliver`, pontuação persistida na entrega. |
| Observer | `application/Events.java` e `NotificationObserver.java`; alterações/aprovação publicam eventos que gravam avisos. |
| Decorator | `domain/VolunteerBadge.java`; componentes envolvidos para compor selos em `Api.person`. |
| Iterator | `domain/DeliveryBook.java`; iterador independente usado por `Reports.Summary` para percorrer entregas. |
| Proxy | `application/Reports.java`, `AccessProxy`; configurado em `AppConfiguration` e bloqueia geração antes de chamar o relatório real. |

## Execuções desta auditoria

| Verificação | Resultado em 14/10/2026 |
|---|---|
| Compilação Angular | Sucesso; bundle inicial 214,22 kB. |
| `ApplicationTest` | 21 testes aprovados. |
| `ArchitectureTest` | 1 teste aprovado. |
| `ConcurrencyTest` | 3 testes aprovados. |
| `DomainTest` | 9 testes aprovados. |
| `RunCucumberTest` | 19 cenários aprovados. |
| Backend total | **53 testes, zero falhas, erros ou ignorados**; `verify` e empacotamento aprovados. |
| Context Mapper | **1 teste aprovado** com o validador oficial. |
| Playwright/Chrome | **5 testes aprovados**, aplicação real em `127.0.0.1:18087`, banco H2 em memória novo. |
| Caso exploratório de unidades | **Defeito reproduzido**, descrito abaixo; não integra os 59 testes automatizados existentes. |

Os comandos reproduzíveis estão em [FUNCIONALIDADES_E_TESTES.md](../FUNCIONALIDADES_E_TESTES.md). Nesta máquina foi usado Maven 3.9.11 já disponível em `.tools`, com cache local e modo offline (`-o -Dmaven.repo.local=.tools/m2`), executando os mesmos objetivos `verify` e `-f tools/cml-validation/pom.xml test`. O backend precisou executar fora do sandbox da ferramenta, pois a primeira tentativa restrita não avançou; essa tentativa foi interrompida e não foi contabilizada como aprovação. O servidor temporário não usou o banco persistente do usuário.

O [workflow anterior 35101178309](https://github.com/GuitGud/maos-em-acao/actions/runs/35101178309) foi consultado novamente: está concluído com sucesso no commit `28b8e9c`. A evidência anterior inclui PostgreSQL 17. PostgreSQL e Docker Compose **não foram reexecutados** nesta auditoria local; o resultado anterior não é apresentado como uma nova execução.

## Achado funcional: quantidades com unidades diferentes

**Prioridade média — relatório e indicadores consolidados.** `Reports.Summary` soma quantidades de todos os itens sem agrupar a unidade cadastrada. `totalStock` e `totalDelivered`, em `frontend/src/main.ts`, fazem a mesma agregação.

Caso reproduzido por HTTP no banco temporário, após os testes de navegador:

1. O saldo continha **220 kg** entre arroz e feijão.
2. Foi cadastrado um item de unidade **kit** e registrada uma doação de **2 kits**.
3. Ao salvar um relatório, o sistema retornou `Estoque atual: 222 unidades` e `Doações recebidas: 222 unidades`.

Esse total perde o significado físico das quantidades. O resultado deveria discriminar item/unidade, como **220 kg e 2 kits**, ou usar outro indicador explicitamente definido. Saldo por item e movimentações individuais continuam preservados; o problema está na consolidação.

Recomendação para correção: agrupar os valores por item/unidade no relatório e nos indicadores, definir o que ocorre com snapshots antigos e incluir teste com unidades heterogêneas. A suíte atual verifica salvar/persistir relatórios e autorizações, mas não detecta esse caso.

## Remoção solicitada do registro

`docs/interacoes/registro-da-tarefa.md` foi retirado do índice com `git rm --cached` e incluído no `.gitignore`. A cópia local permanece. A documentação deixou de ter um link para esse arquivo ausente na versão atual do repositório.

Essa remoção afeta a versão atual após o push; **o arquivo continua acessível nos commits antigos**. O histórico não foi reescrito. A declaração verdadeira de uso de IA foi mantida.

## Pendências para a entrega

1. Resolver com o professor a divergência entre um integrante e o grupo mínimo de quatro.
2. Entregar as interações com IA por meio aceito pelo professor e confirmar como isso se concilia com a exigência de publicar os artefatos.
3. Confirmar a classificação de complexidade dos fluxos escolhidos para avaliação.
4. Corrigir a consolidação de unidades diferentes antes de apresentar os totais como medida de estoque/doações.

Não foram executados pentest, teste de carga ou certificação de acessibilidade; tais verificações não são exigências expressas do PDF fornecido. Esta auditoria distingue atendimento documental, testes executados, achados funcionais e decisões que só o professor pode validar.
