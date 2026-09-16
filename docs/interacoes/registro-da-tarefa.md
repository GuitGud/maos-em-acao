# Registro das interações de desenvolvimento

Data: 16/09/2026. Este arquivo registra mensagens públicas relevantes da interação, sem credenciais, saídas brutas de ferramentas ou conteúdo interno do sistema. O pedido completo está em `pedido-original.md`; a declaração está em `../USO_DE_IA.md`.

## Usuário

Solicitou implementar o Mãos em Ação, atender aos requisitos do PDF, adicionar testes, publicar no GitHub e escrever um arquivo com funcionalidades e comandos de teste.

## Assistente — início

“Vou ler os requisitos do PDF e verificar o projeto local para montar o Mãos em Ação, implementar os testes e preparar a documentação de execução. Também vou verificar o acesso ao GitHub para publicar o projeto. Vou usar a habilidade de PDF para consultar o enunciado.”

## Perguntas e respostas

Assistente: “Qual é seu usuário do GitHub e prefere o repositório ‘maos-em-acao’ público ou privado? O PDF exige público ou privado com acesso ao professor.”

Usuário: “Público, com nome maos-em-acao”.

Assistente: “Quantos integrantes haverá no grupo? Vou preparar uma divisão inicial para 4 pessoas, ajustável para 5 ou 6.”

Usuário: “1 integrante”.

## Assistente — requisitos

“O PDF exige também mapa de histórias, protótipos, modelo CML, cenários Cucumber, arquitetura limpa e registro do uso de IA. Há um detalhe importante: a carga mínima é por integrante; vou documentar a divisão e os critérios de complexidade para o grupo validar com o professor.”

## Assistente — implementação

“Vou implementar Java/Spring Boot com JPA no backend e Angular na interface, com perfis de gestor e voluntário. Os testes vão cobrir os fluxos principais e regras como impedir estoque negativo, inscrição duplicada, excesso de vagas e acesso indevido.”

## Assistente — resposta à definição do usuário

“Vou criar o repositório público `maos-em-acao`. Também registrei que você fará o projeto sozinho: o PDF pede grupos de 4 a 6 pessoas, então essa parte precisa de autorização do professor. Vou deixar essa diferença explícita na documentação, sem inventar integrantes ou contribuições.”

## Assistente — andamento

“A estrutura principal já está escrita, incluindo autenticação, campanhas, estoque, turnos, entregas e notificações. Encontrei a conta GitHub autenticada, `GuitGud`. Agora estou ligando a interface aos fluxos e preparando os testes de integração para verificar as regras no banco.”

“A interface Angular compilou. A primeira rodada de testes encontrou um conflito de configuração do Spring, que já corrigi; vou rodar a suíte novamente e verificar os fluxos no navegador. Também vou testar inscrições e entregas simultâneas, para conferir a proteção de vagas e estoque.”

“Os primeiros 50 testes passaram, incluindo 19 cenários BDD com Cucumber. Estou rodando agora os testes adicionais de concorrência e preparando a documentação de requisitos, arquitetura, DDD, funcionalidades e comandos. A aplicação será empacotada em um único JAR com a interface Angular.”

“Passaram os 53 testes do backend, incluindo concorrência, e a validação do CML pelo parser oficial. Nos testes de navegador, os ícones estavam interferindo nos nomes acessíveis dos botões; corrigi os rótulos e vou repetir os fluxos antes de publicar.”

## Evidências técnicas

Assistente, após validação local: “Os cinco testes de navegador passaram. A validação local soma 53 testes Java/Cucumber, 1 validação CML e 5 testes E2E aprovados. O repositório público foi criado; vou enviar o projeto e conferir a execução da integração contínua no GitHub.”

Assistente, após publicação: “O projeto já está publicado em GuitGud/maos-em-acao. No GitHub Actions, passaram a compilação, os testes Java, o CML e os testes com PostgreSQL 17. Falta apenas concluir a etapa de navegador no ambiente Linux.”

Resultado posterior: ambos os jobs da CI concluíram com sucesso, incluindo Playwright no Linux. A execução está vinculada em `../VALIDACAO.md`.

As implementações e correções são verificáveis no código versionado. Os resultados finais dos testes e a descrição dos ambientes estão em `../VALIDACAO.md`. Logs regeneráveis, binários de dependências, dados locais, cookies e credenciais não fazem parte do repositório público.

Este registro acompanha a entrega acadêmica, mas não pretende reproduzir cada comando de ferramenta. Caso seja exigido arquivo original da conversa, a tarefa no aplicativo é a fonte para exportação.
