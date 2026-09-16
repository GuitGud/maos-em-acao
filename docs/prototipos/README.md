# Protótipos de alta fidelidade

As telas foram implementadas em Angular como protótipo executável conectado aos casos de uso. Os PNGs desta pasta são capturas reais da aplicação em modo de demonstração, com dados fictícios. A [galeria HTML](index.html) permite navegar pelas propostas de tela; abra o arquivo localmente. Para interagir com formulários, use a aplicação conforme o guia de execução.

| Tela | Arquivo | Intenção |
|---|---|---|
| Acesso e identidade visual | [login.png](login.png) | Entrada ou cadastro com mensagem de retorno |
| Visão geral | [dashboard.png](dashboard.png) | Indicadores, campanhas e próximos turnos |
| Campanhas | [campanhas.png](campanhas.png) | Estados, necessidades e ações de gestão |
| Nova campanha | [nova-campanha.png](nova-campanha.png) | Formulário com campos obrigatórios |
| Estoque | [estoque.png](estoque.png) | Saldo por item e trilha de movimentações |
| Voluntariado | [voluntariado.png](voluntariado.png) | Agenda, capacidade e participação |
| Entregas | [entregas.png](entregas.png) | Registro e estorno de distribuição |
| Pessoas | [pessoas.png](pessoas.png) | Aprovação e reconhecimento |
| Relatórios | [relatorios.png](relatorios.png) | Retratos salvos do impacto |
| Notificações | [notificacoes.png](notificacoes.png) | Caixa de entrada e estado de leitura |
| Mobile | [mobile.png](mobile.png) | Navegação horizontal e conteúdo em coluna |

## Decisões visuais

Verde profundo para ação e confiança, creme no fundo, verde claro para informações de participação e laranja pontual na identidade. Tipografia usa fontes do sistema, sem downloads externos. O símbolo botânico e o coração representam crescimento e cuidado, desenhados com texto/CSS. Cartões separam informações operacionais, sem esconder regras em métricas decorativas.

Os formulários usam labels, limites, botões de envio desabilitados durante gravação e mensagens de erro legíveis. Dados vindos do usuário são interpolados como texto. O estado vazio orienta o próximo passo. O usuário voluntário recebe navegação reduzida; rascunhos e relatórios administrativos não são expostos pela API.

## Jornadas demonstráveis

1. Gestão: Campanhas → Nova campanha → Necessidades → Publicar → Estoque → Registrar doação → Entregas → Registrar entrega.
2. Voluntário: Cadastro → aprovação pela gestão → login → Voluntariado → Quero participar → Notificações → Marcar como lida.
3. Gestão: Voluntariado → Concluir após o término → confirmar presença → Relatórios → Salvar e baixar.

## Reproduzir as capturas

Com aplicação demo em execução e banco limpo, na pasta `frontend`:

```powershell
$env:BASE_URL='http://127.0.0.1:8080'
node scripts/capture.mjs
```

O script navega por telas reais, captura desktop/móvel e falha se ocorrer erro JavaScript ou bloqueio da política CSP. Os testes funcionais de navegador estão separados em `e2e/app.spec.ts`.
