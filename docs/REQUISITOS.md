# Requisitos e rastreabilidade

Referência: enunciado **Trabalho em grupo.pdf**, disponibilizado pelo solicitante. O PDF foi lido como especificação acadêmica. As decisões de publicar no GitHub público e realizar individualmente vieram diretamente do usuário. O documento não foi incluído no repositório porque não é artefato autoral do projeto.

## Critérios do enunciado

| Critério | Evidência entregue | Situação |
|---|---|---|
| Sistema web | Angular em `frontend/`, Spring MVC em `web/` | Implementado |
| Banco relacional | H2 persistente local, configuração PostgreSQL, Flyway | Implementado; ambientes executados em VALIDACAO.md |
| Java | Código de domínio, aplicação e adaptadores Java 21 | Implementado |
| Funcionalidades completas, não apenas leitura | Comandos com validação, persistência, interface e testes | Implementado |
| 4 a 6 integrantes | Usuário informou **1 integrante** | **Divergência que exige autorização do professor** |
| Carga por integrante | Opção: 2 altas, ou 1 média + 2 baixas, ou 4 baixas | Escopo excede 2 candidatas a alta; classificação sujeita ao professor |
| Repositório público ou acesso docente | [GuitGud/maos-em-acao](https://github.com/GuitGud/maos-em-acao) | Publicado; CI aprovada |
| Descrição de domínio e linguagem onipresente | `DOMINIO_E_DDD.md` | Entregue |
| Mapa da história do usuário | `MAPA_DE_HISTORIAS.md` | Entregue |
| Protótipos de alta fidelidade | Interface executável, imagens e galeria em `prototipos/` | Entregue |
| Modelo em Context Mapper (CML) | `modelo/maos-em-acao.cml` e validador oficial separado | Entregue |
| Cenários BDD | `src/test/resources/features/*.feature` | Implementado |
| Automação com Cucumber | `RunCucumberTest`, `Steps`, relatórios HTML/JSON | Implementado |
| DDD preliminar, estratégico, tático e operacional | `DOMINIO_E_DDD.md`, domínio e casos de uso | Documentado e aplicado no monólito |
| Arquitetura limpa | Dependências apontam para dentro; teste ArchUnit | Implementado |
| Quatro ou mais padrões da lista | Strategy, Observer, Decorator, Iterator, Proxy | Cinco padrões utilizados por fluxos reais |
| Persistência ORM | Entidades `*Row`, `JpaStore`, Hibernate/JPA | Implementado |
| Apresentação web | Angular responsivo com perfis | Implementado |
| Declaração de IA / interações | `USO_DE_IA.md` e registro do pedido/respostas | Uso declarado, sem alegar autoria exclusivamente humana |

## Requisitos funcionais

| ID | Requisito verificável | Caso de uso / evidência de teste |
|---|---|---|
| RF01 | Cadastrar voluntário pendente com senha protegida | `Api.register`, `signupValidatesEmailAndPasswordsAndStoresHash`, E2E cadastro |
| RF02 | Autenticar e encerrar sessão | Spring Security, teste de login e CSRF |
| RF03 | Aprovar cadastro e avisar voluntário | `Actions.approve`, cenário BDD de aprovação |
| RF04 | Criar e editar campanha | `createCampaign`, `editCampaign`, E2E campanha |
| RF05 | Definir necessidade com meta por item | `need`, E2E e publicação BDD |
| RF06 | Publicar e encerrar respeitando estados | `Campaign.publish/close`, `campanhas.feature` |
| RF07 | Cadastrar catálogo de itens e unidades | `createItem`, integração |
| RF08 | Receber doações e manter histórico | `movement`, testes de saldo e ledger |
| RF09 | Ajustar estoque mediante justificativa | `movement`, `estoque.feature` |
| RF10 | Criar/editar/cancelar/concluir turno | `createShift/editShift/finishShift`, integração e E2E |
| RF11 | Inscrever/cancelar/reinscrever voluntário | `enroll/cancelRegistration`, Cucumber e concorrência |
| RF12 | Impedir sobreposição, duplicidade e lotação | `checkConflict`, teste integração e última vaga |
| RF13 | Confirmar presença e calcular horas | `attendance`, Cucumber e integração |
| RF14 | Registrar entrega com baixa atômica | `deliver`, Cucumber, concorrência e E2E |
| RF15 | Estornar uma entrega uma única vez | `reverse`, Cucumber e E2E |
| RF16 | Notificar alterações e registrar leitura | `Events`, `NotificationObserver`, E2E e propriedade |
| RF17 | Salvar/baixar/excluir relatório | `Reports.AccessProxy`, `report/deleteReport`, integração e E2E |
| RF18 | Registrar/editar feedback após presença | `feedback`, Cucumber e integração |
| RF19 | Calcular selos a partir das horas | `VolunteerBadge`, testes de composição |
| RF20 | Editar nome do próprio perfil | `updateProfile`, API `/profile` |

## Requisitos não funcionais

| ID | Critério | Implementação / verificação |
|---|---|---|
| RNF01 | Backend Java e banco relacional | JDK 21+, Spring Boot 3.5.7, JPA, migrações |
| RNF02 | Integridade de saldo e vagas | Transações, locks pessimistas, `@Version`, constraints e testes concorrentes |
| RNF03 | Autorização no servidor | Perfil e propriedade validados nos casos de uso; frontend não decide permissões |
| RNF04 | Credenciais protegidas | BCrypt, sessão HttpOnly, SameSite, CSRF e ausência de hash nos DTOs |
| RNF05 | Reprodutibilidade | Wrapper Maven, versões fixadas, lockfile pnpm, CI |
| RNF06 | Separação de dependências | Domínio/aplicação sem Spring e JPA; ArchUnit |
| RNF07 | Usabilidade web | Formulários nomeados, feedback de erro, layout desktop/móvel, Playwright |
| RNF08 | Auditoria operacional | Movimentações imutáveis, autor/data, estorno explícito; não é auditoria completa de todas as edições |
| RNF09 | Internacionalização da entrega | UI, documentação e BDD em português; identificadores de código em inglês com mapeamento no glossário |

## Complexidade e responsabilidade individual

A classificação abaixo é uma **estimativa técnica**, não uma aprovação docente. Cadastrar um campo ou listar registros isoladamente não foi contabilizado como funcionalidade alta.

| Fluxo completo | Estimativa | Justificativa |
|---|---|---|
| Gerir campanha com necessidades e transições | Alta | Regras de publicação, agregados relacionados, encerramento condicionado, autorização e notificações |
| Gerir estoque por doações e ajustes | Alta | Entrada/saída, histórico, invariantes numéricas, transações, concorrência e rastreabilidade |
| Gerir turnos e participação | Alta | Capacidade, calendário, duplicidade, conflito, cancelamento, presença e horas |
| Distribuir e estornar doações | Alta | Estoque integrado, elegibilidade do autor, estratégia de prioridade e reversão única |
| Cadastro e aprovação | Média | Autenticação, proteção de senha, aprovação e perfis |
| Notificações internas | Média | Observer, persistência e controle de proprietário |
| Relatórios salvos | Média | Agregação e histórico de snapshots com Proxy de acesso |
| Feedback e perfil | Baixa/média | Escrita validada, limites, propriedade e estado da participação |

Responsabilidade real: todo o escopo pertence ao único integrante informado, com assistência de IA. Não há divisão nominal fictícia. Se o professor exigir equipe, será necessário formar o grupo e atribuir trabalho e autoria reais; documentação não substitui contribuições efetivas.
