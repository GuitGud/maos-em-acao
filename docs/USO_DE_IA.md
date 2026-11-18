# Declaração de uso de inteligência artificial

**Houve uso de IA.** Durante o desenvolvimento do projeto em grupo **"Mãos em Ação"** (plataforma de gestão de campanhas solidárias e voluntários), um assistente de inteligência artificial foi utilizado como apoio estrutural inicial e, principalmente, como **revisor técnico e validador** das entregas realizadas ao longo do ciclo de desenvolvimento.

A transcrição completa da conversa está registrada neste repositório em [log-de-interacao.md](log-de-interacao.md).

---

## Combinado inicial e divisão de responsabilidades

Logo no início do projeto (**Etapa 0**), foi estabelecido um acordo explícito de limites para garantir que o desenvolvimento das regras de negócio, da arquitetura e dos testes fosse conduzido pelos integrantes do grupo:

* **Papel do assistente de IA:**
  1. Sugerir a stack tecnológica (**Java 17+, Spring Boot, Spring Data JPA, PostgreSQL/H2, JUnit 5, Mockito e Angular**) e montar apenas o **esqueleto inicial** do projeto (estrutura de pacotes em monolito modular, organização de pastas e interfaces/classes vazias marcadas com `// TODO`).
  2. Atuar exclusivamente na **validação técnica** das etapas concluídas, avaliando aderência a DDD, BDD, padrões de projeto, cobertura de testes e boas práticas — apontando falhas e sugerindo melhorias sem escrever a solução final.
* **Papel do aluno / grupo:**
  1. Definir a linguagem ubíqua, os contextos delimitados, as histórias de usuário e os cenários BDD.
  2. Implementar todas as entidades, regras de negócio, persistência, padrões de projeto, endpoints REST e o frontend em Angular.
  3. Escrever os testes unitários e aplicar as refatorações necessárias após cada rodada de validação.

---

## Resumo das interações por etapa

O desenvolvimento foi conduzido de forma incremental em 11 etapas documentadas no registro de interação:

| Etapa | Tema | Ação do aluno / grupo | Validação e orientação da IA |
|---|---|---|---|
| **Etapa 0** | Definição do combinado | Definiu o escopo do trabalho em grupo e solicitou apenas o esqueleto inicial e revisões de validação. | Formalizou a divisão de responsabilidades e recomendou a stack tecnológica. |
| **Etapa 1** | Esqueleto do projeto | Recebeu a estrutura base para iniciar a implementação. | Propôs a organização em monolito modular por subdomínio (`campanha`, `voluntariado`, `estoque`, `distribuicao`, `notificacao`, `relatorio`, `compartilhado`), além de `frontend` e `docs`, com interfaces iniciais dos 5 padrões (`// TODO`). |
| **Etapa 2** | Linguagem ubíqua e DDD | Redigiu `docs/linguagem-ubiqua.md` e delimitou 4 subdomínios. | Apontou ambiguidade no termo *"Item"* (sugerindo separar `Necessidade` e `ItemEstoque`), ausência de estados da campanha, necessidade de padronizar *"Beneficiário"* e criação de um mapa de contexto. |
| **Etapa 3** | Histórias de usuário e BDD | Escreveu histórias e cenários Gherkin (`.feature`) para inscrição em turnos. | Identificou lacunas de regras de negócio (voluntário não aprovado, inscrição duplicada, cancelamento, conflito de horário, campanha encerrada) e recomendou desacoplar mensagens de interface dos cenários. |
| **Etapa 4** | Modelo de domínio e JPA | Implementou as entidades principais (`Campanha`, `Turno`, `Inscricao`, `Voluntario`, `ItemEstoque`, `MovimentacaoEstoque`, `Entrega`) e repositórios. | Alertou sobre modelo anêmico em `Turno`, exposição de coleções mutáveis, uso de `String` em vez de `enum` para status, `setVagas` público, riscos de `CascadeType.ALL` e necessidade de `@Version` para concorrência. |
| **Etapa 5** | Funcionalidades principais | Refatorou as regras para dentro das entidades e implementou fluxos de campanha, turnos, voluntários, estoque e entregas. | Validou `publicar()` e apontou melhorias em `registrarSaida()`: bloqueio de `quantidade <= 0`, fonte única da verdade entre saldo e movimentações, registro de metadados (data, responsável, motivo) e controle transacional (`@Transactional`). |
| **Etapa 6** | Padrões de projeto | Implementou `Strategy`, `Observer`, `Decorator`, `Iterator` e `Proxy`. | Identificou falhas no `Observer` (ausência de registro/remoção de observadores, falta de validação de horário e acoplamento na entidade) e no `Proxy` (ausência de verificação real de permissão), além de fornecer critérios de revisão para os demais padrões. |
| **Etapa 7** | Testes unitários | Desenvolveu testes com JUnit 5 e Mockito alcançando 78% de cobertura no JaCoCo. | Recomendou validar a não alteração de estado em exceções, testar remoção de observadores, cobrir casos de borda, priorizar cobertura de ramos (*branch coverage*), isolar testes de domínio do Spring e testar cada padrão de projeto. |
| **Etapa 8** | Notificações, relatórios e feedback | Concluiu notificações de campanha, relatórios protegidos pelo `Proxy` (`ADMIN`) e avaliações pós-ação. | Levantou pontos de verificação sobre regras de cômputo de horas voluntárias (comparecimento vs. inscrição), consistência de estoque, registro de leitura de notificações e restrições de avaliação e acesso. |
| **Etapa 9** | Frontend e integração | Desenvolveu as telas em Angular (campanhas, turnos e estoque) integradas à API REST. | Forneceu diretrizes para validar contrato via DTOs (sem expor entidades JPA), tratamento de erros HTTP via `@ControllerAdvice`, Bean Validation, bloqueio de segurança no backend e estados de interface. |
| **Etapa 10** | Revisão final | Solicitou verificação geral antes da entrega do trabalho. | Entregou um checklist final cobrindo requisitos funcionais, DDD/BDD, os 5 padrões de projeto, qualidade de código/testes e rastreabilidade da divisão do grupo no Git. |

---

## Principais melhorias incorporadas a partir das revisões

As revisões realizadas pelo assistente orientaram diretamente ajustes técnicos feitos pelo grupo no código e na documentação:

* **Domínio rico e proteção de invariantes:** regras de negócio como limite de vagas em `Turno`, transição de estados em `Campanha` e validação de saldo e quantidades positivas em `ItemEstoque` foram encapsuladas nas próprias entidades, removendo `setters` que pudessem violar a consistência do domínio.
* **Refinamento da linguagem ubíqua:** separação explícita entre `Necessidade` (contexto de Campanha) e `ItemEstoque` (contexto de Estoque), acompanhada de mapa de contexto entre subdomínios.
* **Efetividade dos padrões de projeto:** correção do `RelatorioAdministrativoProxy` para aplicar verificação real de perfil administrativo (`ADMIN`) antes de delegar a geração de relatórios, e ajuste no ciclo de vida de notificações do `Observer`.
* **Qualidade e abrangência dos testes:** ampliação dos cenários de teste unitário para cobrir casos de borda, falhas de permissão, exceções de negócio e verificação de estado pós-operação.

---

## Rastreabilidade e responsabilidade autoral

Todo código-fonte, modelagem, documentação e suíte de testes presentes neste repositório foram escritos, revisados e validados pelos integrantes do grupo, assumindo total responsabilidade técnica sobre o funcionamento do sistema e o domínio de suas decisões arquiteturais. Para consulta detalhada de cada interação, consulte [log-de-interacao.md](log-de-interacao.md).
