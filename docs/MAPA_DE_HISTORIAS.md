# Mapa de histórias de usuário

Personas propostas: **gestora da ONG**, que planeja e presta contas; **voluntário**, que oferece tempo e registra sua atuação. Não são personas derivadas de entrevistas reais.

## Backbone e entregas verticais

Cada coluna representa uma atividade na jornada. A leitura horizontal mostra o fluxo; as linhas agrupam incrementos funcionais. As três linhas estão incluídas nesta entrega.

| Incremento | Entrar na rede | Planejar a ação | Mobilizar pessoas | Receber recursos | Distribuir | Aprender e prestar contas |
|---|---|---|---|---|---|---|
| 1 — Operação essencial | Cadastro, login, aprovação | Campanha, catálogo, metas, publicação | Turno e inscrição | Doação com entrada de saldo | Entrega com baixa de saldo | Balanço salvo |
| 2 — Consistência | Perfil e restrições por ator | Edição e encerramento | Cancelamento, conflito, lotação e presença | Ajuste justificado e trilha | Estorno único e autorização | Horas e download |
| 3 — Participação | Aviso de aprovação | Aviso de alteração de campanha | Aviso de mudança de turno | Progresso de metas para gestão | Pontuação por estratégia | Feedback, selos e exclusão de snapshot |

## Histórias e critérios de aceitação

| ID | História | Critérios / cenário verificável |
|---|---|---|
| HU01 | Como pessoa interessada, quero me cadastrar para participar da rede. | Nome/e-mail/senha válidos criam voluntário pendente; e-mail duplicado não cria segunda conta. |
| HU02 | Como gestora, quero aprovar voluntários para liberar sua participação. | Somente gestão aprova; a aprovação gera aviso; pendente não se inscreve. |
| HU03 | Como gestora, quero criar uma campanha com necessidades para orientar a arrecadação. | Rascunho é editável, cada item possui uma meta positiva, publicação sem necessidade é recusada. |
| HU04 | Como gestora, quero encerrar uma campanha para sinalizar o fim da ação. | Campanha publicada sem turnos abertos pode encerrar; a encerrada não aceita edição ou novas operações de campanha. |
| HU05 | Como gestora, quero registrar doações para manter o saldo correto. | Quantidade positiva gera saldo e movimento; origem e campanha são obrigatórias. |
| HU06 | Como gestora, quero corrigir a contagem física sem apagar o histórico. | Ajuste exige justificativa e não pode resultar em estoque negativo. |
| HU07 | Como gestora, quero criar e alterar turnos para coordenar o trabalho. | Datas válidas, capacidade positiva, sem redução abaixo de inscritos; alterações avisam participantes. |
| HU08 | Como voluntário, quero me inscrever em um turno disponível para participar da ação. | Aprovação, vaga, ausência de duplicidade/conflito; duas pessoas não ocupam a última vaga. |
| HU09 | Como voluntário, quero cancelar minha inscrição para liberar a vaga. | Só a própria inscrição, antes do início; histórico permanece; reinscrição é possível se elegível. |
| HU10 | Como gestora, quero registrar presença para contabilizar horas reais. | Turno concluído, inscrição pendente; duração deriva das datas; ausência gera zero hora. |
| HU11 | Como participante, quero registrar a entrega para acompanhar o destino da doação. | Acesso elegível, estoque suficiente, destinatário e quantidade válidos; saldo e movimento mudam juntos. |
| HU12 | Como gestora, quero estornar uma entrega devolvida para corrigir o saldo. | Motivo obrigatório, estorno integral e único, histórico da entrega preservado. |
| HU13 | Como voluntário, quero ler avisos de alteração para ajustar minha participação. | Caixa privada por ator, leitura persistente e aviso derivado de mudança salva. |
| HU14 | Como gestora, quero salvar um relatório para prestar contas da ação. | Snapshot com doações, estoque, entregas líquidas de estorno e horas; voluntário não gera relatório administrativo. |
| HU15 | Como voluntário, quero avaliar uma ação para ajudar a melhorar a organização. | Presença confirmada, nota 1–5, comentário; editar substitui a avaliação da mesma inscrição. |
| HU16 | Como voluntário, quero ver minhas horas e reconhecimento para acompanhar minha participação. | Horas apenas de presença; selos compostos conforme limiares, sem alteração manual pelo participante. |

Os cenários executáveis estão em `src/test/resources/features`. Casos de fronteira adicionais vivem nos testes JUnit; fluxos completos de interface em `frontend/e2e/app.spec.ts`. Consulte `REQUISITOS.md` para ligar histórias, funcionalidades e evidências.

## Fora do mapa desta entrega

Multi-ONG, doações financeiras, envio de e-mail/SMS, documentos de beneficiários, anexos, recuperação de senha e rotas de entrega. São evoluções, não funcionalidades implícitas ou parcialmente simuladas.
