# Roteiro de apresentação: Mãos em Ação

Duração principal: cerca de 10 minutos. São 14 slides principais e 3 de apoio, ocultos no modo de apresentação do PowerPoint. Os slides de apoio não entram no tempo. Use o Modo do Apresentador para ver as notas.

## Slide 1: Mãos em Ação

Início aproximado: 00:00. Tempo sugerido: 25 segundos.

Bom dia. Vou apresentar o Mãos em Ação, uma plataforma para uma ONG organizar campanhas solidárias e a participação de voluntários. O foco desta apresentação é mostrar como as necessidades dos usuários orientam o mapa de histórias e como essas histórias aparecem nas telas do sistema. Primeiro apresento a jornada, depois os protótipos e as regras que podemos verificar em cada um.

## Slide 2: Problema e usuários

Início aproximado: 00:25. Tempo sugerido: 35 segundos.

A proposta parte de uma situação comum no domínio: a ONG precisa coordenar necessidades, materiais e disponibilidade de pessoas. Se essas informações ficam separadas, fica difícil saber o que falta e quem pode ajudar. Por isso, considerei dois perfis principais. A gestão planeja as campanhas e controla os recursos. O voluntário oferece seu tempo e acompanha sua participação. Esses perfis são hipóteses do projeto acadêmico, não resultados de entrevistas com uma ONG real. Cada instalação atende uma ONG.

## Slide 3: Mapa de histórias: organização

Início aproximado: 01:00. Tempo sugerido: 70 segundos.

O mapa organiza o sistema pela jornada do usuário. Na horizontal, temos as atividades, começando por entrar na rede, planejar a ação e mobilizar pessoas. Na vertical, temos os incrementos de escopo. A primeira linha reúne a operação essencial. A segunda acrescenta consistência, como controlar permissões e impedir conflito de horário. A terceira melhora o acompanhamento com avisos. Um exemplo de leitura é seguir a coluna de mobilização: primeiro existe turno e inscrição, depois entram regras de cancelamento e presença, e também os avisos sobre mudanças. Os três incrementos estão contemplados no projeto. A divisão serve para explicar a prioridade e a evolução das funcionalidades.

## Slide 4: Mapa de histórias: distribuição

Início aproximado: 02:10. Tempo sugerido: 60 segundos.

Esta é a continuação do mesmo mapa, mantendo as três linhas de incremento. Depois de planejar e mobilizar, a ONG recebe recursos, distribui os itens e presta contas. Na operação essencial, uma doação aumenta o saldo e uma entrega reduz esse saldo. Na linha de consistência, os ajustes precisam de justificativa e o estorno corrige uma entrega sem apagar seu histórico. Na participação, entram o progresso das metas e o feedback após a ação. Essa leitura mostra que o sistema precisa completar uma atividade de negócio. Por exemplo, registrar uma entrega envolve conferir o estoque e manter o histórico, além de mostrar uma tela.

## Slide 5: Histórias e critérios de aceitação

Início aproximado: 03:10. Tempo sugerido: 45 segundos.

As histórias descrevem quem precisa de algo e por quê. A HU03 representa a gestora que cria uma campanha com necessidades para orientar a arrecadação. Seu critério de aceitação exige uma meta positiva antes da publicação. A HU08 representa o voluntário que quer participar de um turno. O sistema precisa verificar aprovação, vaga e conflito de horário. Esses critérios tornam a história verificável: consigo observar quando a ação deve funcionar e quando deve ser recusada. Na sequência, vou mostrar onde esses requisitos aparecem nos protótipos.

## Slide 6: Acesso à plataforma

Início aproximado: 03:55. Tempo sugerido: 30 segundos.

A experiência começa por esta tela de acesso, que também oferece o cadastro de novos voluntários. Depois do cadastro, a pessoa aguarda aprovação da gestão para participar dos turnos. O sistema apresenta opções diferentes conforme o perfil. O protótipo já usa a identidade visual do produto, com verde, fundo claro e hierarquia de texto. As imagens que mostro são capturas da aplicação com dados fictícios de demonstração.

## Slide 7: Campanhas e necessidades

Início aproximado: 04:25. Tempo sugerido: 45 segundos.

A tela de campanhas mostra o propósito da ação, sua situação e os itens necessários. Aqui podemos relacionar diretamente a HU03 ao protótipo. A gestão cria a campanha, informa as necessidades e publica quando o planejamento mínimo está pronto. A campanha em rascunho ainda precisa de necessidades. A campanha publicada pode receber doações e organizar turnos. O acompanhamento das metas ajuda a gestão a observar o que falta arrecadar. Encerrar uma campanha também tem uma regra: antes é necessário finalizar ou cancelar os turnos abertos.

## Slide 8: Criação de uma campanha

Início aproximado: 05:10. Tempo sugerido: 30 segundos.

Este formulário detalha a primeira interação do fluxo de planejamento. A gestão informa nome e propósito, salva e depois cadastra as necessidades na tela da campanha. O formulário não solicita decisões que pertencem a outra etapa. Os campos obrigatórios e o retorno da operação orientam o usuário. O botão cancelar permite sair sem salvar. O estado inicial da campanha é rascunho, então o cadastro não publica a ação automaticamente.

## Slide 9: Turnos e inscrições

Início aproximado: 05:40. Tempo sugerido: 50 segundos.

Este recorte mostra o turno na visão da gestão. A tela reúne atividade, datas, ocupação e participações. Para o voluntário, a ação principal é se inscrever. A HU08 exige que o cadastro esteja aprovado, que exista vaga e que o horário não conflite com outra inscrição. A HU09 permite cancelar antes do início para liberar a vaga. Se a gestão alterar o turno, os participantes recebem um aviso interno. Depois que o turno termina, a gestão conclui a atividade e confirma as presenças, que geram as horas de participação.

## Slide 10: Estoque e histórico de doações

Início aproximado: 06:30. Tempo sugerido: 45 segundos.

No estoque, a gestão observa o saldo de cada item e o histórico que explica esse saldo. A HU05 registra doações positivas. A HU06 permite corrigir uma contagem física por meio de ajuste justificado. Toda entrada ou saída mantém um registro. Um exemplo simples é receber dez unidades e distribuir três: o saldo passa a sete. O sistema recusa uma saída maior que o disponível. Neste projeto, o estoque pertence à ONG e é compartilhado entre as campanhas. A movimentação registra a campanha relacionada, mas não cria uma reserva exclusiva.

## Slide 11: Registro de entregas

Início aproximado: 07:15. Tempo sugerido: 40 segundos.

A distribuição registra para onde os itens foram enviados e reduz o saldo correspondente. O protótipo exibido está no estado vazio, antes da primeira entrega. O botão de registrar inicia o formulário com campanha, item, quantidade e código do destinatário. A HU11 exige estoque suficiente e uma pessoa autorizada. Se os itens retornarem, a HU12 permite um estorno integral, com justificativa e uma única vez. A entrega anterior permanece no histórico. O código do destinatário evita usar dados pessoais desnecessários na demonstração.

## Slide 12: Navegação e uso no celular

Início aproximado: 07:55. Tempo sugerido: 35 segundos.

A visão geral resume as campanhas ativas, o estoque e as próximas ações. No celular, o conteúdo se reorganiza em uma coluna e a navegação permite rolagem horizontal. O recorte à direita mostra o início dessa versão móvel. A proposta mantém os mesmos termos e a mesma identidade entre as telas. O objetivo é permitir que a gestão acompanhe a operação e que o voluntário encontre sua próxima ação mesmo em uma tela menor.

## Slide 13: Relação entre história, tela e regra

Início aproximado: 08:30. Tempo sugerido: 40 segundos.

Esta tabela resume a ligação entre o mapa e os protótipos. A história de planejar uma campanha aparece nas telas de campanha e necessidades, com a regra de publicação. A participação aparece no turno, com validação de vaga e horário. A doação aparece no estoque e a entrega aparece na distribuição. Assim, o mapa orienta quais atividades precisam existir, a história explica a necessidade do usuário e o protótipo mostra a interação. Os critérios de aceitação permitem verificar se o comportamento atende àquela necessidade.

## Slide 14: Demonstração e materiais do projeto

Início aproximado: 09:10. Tempo sugerido: 25 segundos.

Para concluir, o mapa apresenta a jornada e os incrementos de escopo. Os protótipos mostram como essa jornada se transforma em ações que o usuário consegue realizar. O fluxo de demonstração começa pela campanha e termina na entrega, com participação de voluntários e controle de estoque. O repositório reúne as histórias, as telas e a implementação. O desenvolvimento contou com apoio de IA, declarado na documentação. Fico à disposição para explicar uma história ou uma regra específica.

## Slide 15: Aprovação e pessoas

Apoio para perguntas.

Este slide é de apoio para perguntas sobre cadastro e autorização. A gestão consulta os voluntários e aprova quem ainda está pendente. A captura mostra contas de demonstração já aprovadas. A tabela também apresenta horas e reconhecimento. Um novo cadastro não pode escolher o papel de gestor nem se aprovar. O cadastro, a aprovação e as notificações compõem a entrada na rede do mapa de histórias.

## Slide 16: Notificações internas

Apoio para perguntas.

Este slide é de apoio para perguntas sobre mudanças de turno ou campanha. As notificações pertencem ao usuário autenticado e permitem marcar uma mensagem como lida. A captura mostra o estado sem mensagens. O sistema gera avisos quando existe uma alteração relevante para o participante, além do aviso de aprovação. A implementação usa caixa de entrada interna. Não envia e-mail, SMS ou notificações push.

## Slide 17: Relatórios e prestação de contas

Apoio para perguntas.

Este slide é de apoio para perguntas sobre os resultados da ação. O gestor salva um retrato dos totais de doações, saldo, entregas e horas voluntárias. Esse relatório permanece com os valores do momento da geração e pode ser baixado como texto. A captura mostra o estado inicial, antes de salvar um balanço. As avaliações após presença confirmada completam o aprendizado sobre a ação, conforme a última coluna do mapa.

## Antes da apresentação

1. Abra o PowerPoint e confira se as notas aparecem no Modo do Apresentador.
2. Use F5 para começar do primeiro slide. A versão PDF serve como reserva, mas não mostra suas notas.
3. Reserve cerca de 2 minutos para os dois slides do mapa. Leia as atividades da esquerda para a direita e depois explique as linhas de incremento.
4. Nas telas, aponte a ação principal e explique a regra. Evite ler todos os textos pequenos da interface.
5. As capturas usam dados fictícios e incluem estados vazios. Não anuncie entrevistas, pesquisa de campo ou resultados sociais que não foram realizados.
6. Caso faça demonstração ao vivo, inicie a aplicação antes da aula seguindo o guia de execução. Não dependa de um servidor local antigo ainda estar aberto.
7. Se faltar tempo, resuma os slides 6 e 12. Preserve o mapa, os critérios de aceitação e os fluxos de campanha e voluntariado.

## Demonstração opcional, fora dos 10 minutos

O roteiro principal usa as capturas e funciona sem internet. Para uma demonstração interativa, use a aplicação conforme [FUNCIONALIDADES_E_TESTES.md](../../FUNCIONALIDADES_E_TESTES.md) (arquivo na raiz do projeto). A galeria está em `../prototipos/index.html` e pode ser aberta sem iniciar o backend.

Roteiro: campanha em rascunho, necessidade, publicação, turno, inscrição, doação, entrega e saldo. Não tente concluir um turno futuro, pois o sistema exige que o horário de término tenha passado.

## Perguntas prováveis e respostas

**O que diferencia o mapa de uma lista de funcionalidades?**
O mapa organiza atividades na jornada horizontal e incrementos na vertical. Isso permite ver a sequência de uso e o que cada incremento entrega.

**O que representam as três linhas?**
Operação essencial, consistência e participação. Elas organizam o escopo, e as três estão contempladas no projeto. Não são datas de lançamento nem promessas de entrevistas.

**Por que o protótipo é de alta fidelidade?**
Ele apresenta a identidade visual, os componentes, os formulários e os estados da interface com conteúdo de demonstração. As telas vêm da aplicação Angular e o fluxo interativo existe no projeto.

**Qual é a diferença entre história e critério de aceitação?**
A história explica a necessidade de um ator e seu objetivo. O critério descreve uma condição observável para aceitar ou recusar a operação.

**O estoque é reservado por campanha?**
Não. O saldo é compartilhado por item na ONG. Cada movimentação registra a campanha relacionada.

**As notificações chegam por e-mail?**
Nesta versão, elas ficam na caixa de entrada da plataforma. E-mail, SMS e push estão fora do escopo.

**Como o projeto evita lotação e estoque negativo?**
O backend valida as regras e executa as alterações em transações com locks. Existem testes de concorrência para última vaga e uso simultâneo de estoque. O foco desta apresentação, porém, é mostrar como isso atende às histórias.

**Os perfis vieram de entrevistas?**
Não. São perfis propostos para o trabalho acadêmico. Não foi realizada pesquisa de campo com uma ONG.

**Você trabalhou sozinho e usou IA?**
O projeto tem um integrante e usa assistência de IA declarada na documentação. O enunciado prevê grupos de 4 a 6 pessoas, então a modalidade individual depende de autorização do professor.

**O que está fora do escopo?**
Multi-ONG, doações financeiras, rotas de entrega, envio de mensagens externas e cadastro pessoal detalhado de beneficiários.
