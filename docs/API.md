# Contrato da API

Base: `/api`. JSON UTF-8, exceto o login (`application/x-www-form-urlencoded`). A interface Angular é o cliente principal. IDs são inteiros gerados no servidor. Quantidades são inteiras; datas representam UTC no formato `2030-01-02T12:00:00`.

## Sessão e CSRF

1. `GET /api/csrf` retorna `{ "token": "...", "headerName": "X-XSRF-TOKEN" }` e cookie CSRF.
2. Envie esse token no cabeçalho `X-XSRF-TOKEN` em POST/PUT/DELETE, preservando cookies.
3. `POST /api/login` recebe os campos de formulário `email` e `password`; sucesso 204 cria sessão, falha 401.
4. Busque novo token após login/logout: o Spring invalida o anterior.
5. `POST /api/logout` encerra a sessão (204).

Cadastro não requer sessão, mas requer CSRF. Cookies e CSRF devem vir da mesma sessão. Não há token JWT nem papel enviado pelo cliente.

## Rotas

| Método e rota | Corpo JSON | Permissão / resposta |
|---|---|---|
| POST `/register` | `name`, `email`, `password` (12–64 caracteres) | Público; pessoa pendente (sem hash) |
| GET `/state` | — | Autenticado; painel filtrado por papel/proprietário |
| PUT `/profile` | `name` | Próprio perfil |
| POST `/volunteers/{id}/approve` | — | Gestor; pessoa aprovada |
| POST `/campaigns` | `name`, `description` | Gestor; campanha em rascunho |
| PUT `/campaigns/{id}` | `name`, `description` | Gestor; campanha atualizada |
| POST `/campaigns/{id}/publish` | — | Gestor; campanha publicada |
| POST `/campaigns/{id}/close` | — | Gestor; campanha encerrada |
| POST `/campaigns/{id}/needs` | `itemId`, `target` | Gestor; cria ou substitui meta |
| POST `/items` | `name`, `unit` | Gestor; saldo zero |
| POST `/movements` | `campaignId`, `itemId`, `quantity`, `kind`, `reason` | Gestor; `kind` é DOACAO ou AJUSTE |
| POST `/shifts` | `campaignId`, `title`, `startsAt`, `endsAt`, `capacity` | Gestor; turno aberto |
| PUT `/shifts/{id}` | `title`, `startsAt`, `endsAt`, `capacity` | Gestor; campanha do turno não muda |
| POST `/shifts/{id}/cancel` | — | Gestor; cancela inscrições e avisa |
| POST `/shifts/{id}/finish` | — | Gestor; após término |
| POST `/shifts/{id}/enroll` | — | Próprio usuário aprovado |
| POST `/registrations/{id}/cancel` | — | Proprietário ou gestor; antes do início |
| POST `/registrations/{id}/attendance` | `attended`: boolean | Gestor; presença ou ausência |
| POST `/deliveries` | `campaignId`, `itemId`, `quantity`, `beneficiary`, `vulnerability`, `members`, `strategy` | Gestor ou participante aprovado da campanha |
| POST `/deliveries/{id}/reverse` | `reason` | Gestor; estorno integral |
| POST `/notifications/{id}/read` | — | Destinatário ou gestor |
| POST `/feedback` | `registrationId`, `rating`, `comment` | Proprietário ou gestor, presença confirmada |
| POST `/reports` | `title` | Gestor; snapshot salvo |
| DELETE `/reports/{id}` | — | Gestor; exclui apenas snapshot |

Exemplo de entrega:

```json
{
  "campaignId": 1,
  "itemId": 1,
  "quantity": 3,
  "beneficiary": "FAM-001",
  "vulnerability": 4,
  "members": 5,
  "strategy": "VULNERABILIDADE"
}
```

`strategy` aceita `VULNERABILIDADE` ou `TAMANHO_FAMILIA`. `vulnerability` está entre 1 e 5; `members` entre 1 e 100. O cliente não fornece `actorId`, prioridade calculada, saldo ou papel de usuário.

## Erros

| Status | Significado |
|---|---|
| 400 | JSON malformado, campo desconhecido ou validação de DTO |
| 401 | Sessão ausente/expirada ou login inválido |
| 403 | Papel/propriedade insuficiente ou token CSRF ausente/inválido |
| 404 | Registro inexistente |
| 409 | Constraint ou conflito de atualização |
| 422 | Regra de negócio violada, ex.: estoque insuficiente ou turno lotado |

Erros de domínio retornam `{ "message": "texto legível" }`. Erros do filtro de segurança podem usar o formato padrão do Spring, e o cliente exibe mensagem genérica quando não houver `message`.

Criações respondem 200 com a representação criada. Login/logout usam 204. A exclusão de relatório retorna corpo vazio. Não há paginação no endpoint de estado, nem busca pública de campanhas sem autenticação.
