# Conversa: Passo a passo do projeto "Mãos em Ação"

> Registro de uma conversa entre um **usuário** (aluno) e um **assistente de IA** durante o desenvolvimento do trabalho em grupo.
> Combinado inicial: o assistente prepara o esqueleto; o usuário implementa as funcionalidades e os testes unitários; o assistente **apenas valida**.

---

## Etapa 0 — Definição do combinado

**Usuário:** Oi! Vou fazer o trabalho em grupo com a plataforma de gestão de campanhas solidárias e voluntários (Mãos em Ação). Quero que você prepare o esqueleto, mas as funcionalidades e os testes unitários eu faço sozinho. Só quero ajuda na validação. Pode ser?

**Assistente:** Pode, sim. Combinado:

1. Eu monto o esqueleto (estrutura de pacotes, entidades base, interfaces dos padrões, configuração).
2. Você implementa as funcionalidades e escreve os testes unitários.
3. Quando disser que concluiu uma etapa, você me envia o código e eu verifico: aderência ao DDD, ao BDD, aos padrões de projeto, cobertura de testes e boas práticas. Eu aponto problemas e sugiro caminhos, mas não escrevo a solução por você.

Stack sugerida: **Java 17+, Spring Boot, Spring Data JPA, banco relacional (PostgreSQL/H2), JUnit 5, Mockito e Angular**.

---

## Etapa 1 — Esqueleto do projeto

**Assistente:** Esqueleto proposto (monolito modular, um pacote por subdomínio):

```
maos-em-acao/
├── backend/
│   └── src/main/java/br/edu/maosemacao/
│       ├── MaosEmAcaoApplication.java
│       ├── campanha/        # criar, editar, publicar, encerrar
│       │   ├── domain/
│       │   ├── application/
│       │   ├── infra/
│       │   └── web/
│       ├── voluntariado/    # cadastro, aprovação, turnos, inscrições
│       ├── estoque/         # itens, doações, movimentações
│       ├── distribuicao/    # entregas, famílias/instituições
│       ├── notificacao/     # Observer
│       ├── relatorio/       # Proxy
│       └── compartilhado/   # exceções, value objects, utilitários
│   └── src/test/java/...    # espelha a estrutura acima
├── frontend/                # Angular
├── docs/
│   ├── linguagem-ubiqua.md
│   ├── historias-de-usuario.md
│   └── bdd/                 # arquivos .feature
└── README.md
```

Os pacotes dos padrões ficam dentro de cada subdomínio:

| Padrão | Onde fica | Uso |
|---|---|---|
| Strategy | `distribuicao/domain/prioridade` | Regras de prioridade de entrega |
| Observer | `notificacao` | Avisar voluntários sobre mudança de turno/campanha |
| Decorator | `voluntariado/domain/selo` | Selos e benefícios por participação |
| Iterator | `distribuicao/domain` e `estoque/domain` | Percorrer entregas/itens de uma campanha |
| Proxy | `relatorio` | Controle de acesso a relatórios administrativos |

Deixei apenas interfaces e classes vazias com `// TODO` nos pontos onde você vai implementar.

**Usuário:** Perfeito, vou começar.

---

## Etapa 2 — Linguagem ubíqua e subdomínios (DDD)

**Usuário:** Terminei a etapa de DDD. Escrevi o `docs/linguagem-ubiqua.md` com estes termos: campanha, necessidade, doação, item, turno, inscrição, voluntário, movimentação de estoque, entrega, beneficiário. Defini quatro subdomínios: Campanha, Voluntariado, Estoque e Distribuição. Pode verificar?

**Assistente:** Boa base. Minha verificação:

**Pontos positivos**
- Os termos batem com os nomes das classes e dos pacotes. Isso é o ponto central da linguagem ubíqua.
- Os quatro subdomínios têm fronteiras claras.

**Pontos de atenção**
1. **"Item" é ambíguo.** Ele aparece como *necessidade da campanha* (o que se precisa) e como *item em estoque* (o que se tem). Considere separar: `Necessidade` (em Campanha) e `ItemEstoque` (em Estoque), ou deixar explícito no glossário o significado em cada contexto.
2. **Falta o estado das campanhas.** O glossário não diz quando uma campanha está *rascunho*, *publicada* ou *encerrada*. Como as regras de negócio dependem disso, vale registrar.
3. **"Beneficiário"** aparece no glossário, mas o enunciado fala em "famílias/instituições atendidas". Padronize um termo só e use o mesmo no código.
4. **Contextos delimitados:** você definiu os subdomínios, mas não indicou como se relacionam. Por exemplo: a Distribuição consome estoque? Pelo domínio ou por evento? Um pequeno mapa de contexto resolveria.

**Usuário:** Entendi. Vou separar Necessidade e ItemEstoque, adicionar os estados e criar o mapa de contexto. Já ajusto e te aviso.

---

## Etapa 3 — Histórias de usuário e cenários BDD

**Usuário:** Ajustei o DDD. Agora fiz as histórias de usuário e os cenários BDD. Colei um exemplo:

```gherkin
Funcionalidade: Inscrição em turno

  Cenário: Voluntário se inscreve em turno disponível
    Dado que existe um turno com 2 vagas livres
    E que sou um voluntário aprovado
    Quando me inscrevo no turno
    Então minha inscrição é confirmada
    E o turno passa a ter 1 vaga livre

  Cenário: Voluntário tenta se inscrever em turno lotado
    Dado que existe um turno sem vagas livres
    Quando me inscrevo no turno
    Então recebo a mensagem "Turno sem vagas"
```

Verifique, por favor.

**Assistente:** Os cenários estão bem escritos e na linguagem do domínio. Minha verificação:

**Pontos positivos**
- Seguem o formato Dado/Quando/Então.
- Incluem caminho feliz e caminho de falha.
- Usam termos do glossário (*turno*, *inscrição*, *voluntário*).

**Lacunas nas regras de negócio**
1. **Voluntário não aprovado:** o cenário 1 exige voluntário aprovado, mas não há cenário para o caso contrário.
2. **Inscrição duplicada:** o que acontece se o voluntário já está inscrito no turno?
3. **Cancelamento:** o enunciado pede inscrição *e cancelamento*. Falta o cenário em que a vaga é liberada.
4. **Conflito de horário:** o voluntário pode se inscrever em dois turnos sobrepostos?
5. **Campanha encerrada:** é possível se inscrever em turnos de uma campanha encerrada?

**Sobre o texto**
- A mensagem `"Turno sem vagas"` está acoplada à interface. Prefira descrever o resultado de negócio ("a inscrição é recusada por falta de vagas").
- Considere `Esquema do Cenário` com exemplos para variar a quantidade de vagas.

Sugestão: liste uma tabela com as regras da inscrição e verifique se cada uma tem pelo menos um cenário.

**Usuário:** Faz sentido. Vou completar os cenários e depois partir pro código.

---

## Etapa 4 — Modelo de domínio e persistência (JPA)

**Usuário:** Implementei as entidades principais: `Campanha`, `Turno`, `Inscricao`, `Voluntario`, `ItemEstoque`, `MovimentacaoEstoque`, `Entrega`. Criei os repositórios Spring Data. Colei `Turno` e `Inscricao`:

```java
@Entity
public class Turno {
    @Id @GeneratedValue
    private Long id;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private int vagas;

    @OneToMany(mappedBy = "turno", cascade = CascadeType.ALL)
    private List<Inscricao> inscricoes = new ArrayList<>();

    public List<Inscricao> getInscricoes() { return inscricoes; }
    public void setVagas(int vagas) { this.vagas = vagas; }
    // ... getters e setters
}

@Entity
public class Inscricao {
    @Id @GeneratedValue
    private Long id;
    @ManyToOne private Turno turno;
    @ManyToOne private Voluntario voluntario;
    private String status;
}
```

Está correto?

**Assistente:** O mapeamento JPA está funcional, mas há problemas de modelagem. Minha análise:

**Principais problemas**
1. **Modelo anêmico.** `Turno` só tem getters/setters. A regra "não aceitar inscrição acima do limite de vagas" não está no domínio. Quem garante isso hoje? Se for o serviço, a regra fica espalhada. Em DDD, `Turno` deve proteger as próprias invariantes.
2. **Coleção exposta.** `getInscricoes()` devolve a lista mutável. Qualquer classe pode adicionar inscrições sem passar pela regra de vagas.
3. **`status` como `String`.** Use `enum` com `@Enumerated(EnumType.STRING)` para evitar valores inválidos e facilitar o tratamento por estado.
4. **`setVagas` público** permite reduzir as vagas abaixo do número de inscritos. Pergunte-se: que operação de negócio representa isso?
5. **`CascadeType.ALL`** em `inscricoes` pode apagar o histórico se o turno for removido. Avalie se faz sentido.
6. **Agregado:** decida se `Inscricao` faz parte do agregado `Turno` ou se é agregado próprio. Essa decisão afeta transações e concorrência.

**Pontos de atenção**
- Falta construtor ou fábrica que garanta objeto válido (início antes do fim, vagas > 0).
- Considere versionamento otimista (`@Version`) em `Turno`, porque duas inscrições simultâneas podem estourar o limite de vagas.

**Usuário:** Entendi, a regra tem que morar na entidade. Vou refatorar.

---

## Etapa 5 — Funcionalidades principais

**Usuário:** Refatorei e implementei as funcionalidades:
- Criar, editar, publicar e encerrar campanhas
- Inscrição e cancelamento de turnos
- Cadastro e aprovação de voluntários
- Registro de doações e movimentações de estoque
- Registro de entregas

Colei o método de publicar campanha e o de registrar saída de estoque:

```java
public void publicar() {
    if (this.status != StatusCampanha.RASCUNHO) {
        throw new CampanhaException("Só é possível publicar campanhas em rascunho");
    }
    if (necessidades.isEmpty()) {
        throw new CampanhaException("Campanha precisa de ao menos uma necessidade");
    }
    this.status = StatusCampanha.PUBLICADA;
}

public void registrarSaida(int quantidade) {
    if (quantidade > saldo) {
        throw new EstoqueInsuficienteException();
    }
    this.saldo -= quantidade;
    movimentacoes.add(new MovimentacaoEstoque(TipoMovimentacao.SAIDA, quantidade));
}
```

Pode validar?

**Assistente:** Melhorou bastante. As regras agora estão no domínio. Minha verificação:

**`publicar()`**
- Correto: valida o estado de origem e a pré-condição (ao menos uma necessidade).
- Atenção: considere validar também datas (campanha com data de término no passado).

**`registrarSaida()`**
- Está correto no caminho feliz e valida saldo insuficiente.
- **Falta validar `quantidade <= 0`.** Uma saída de valor negativo *aumentaria* o saldo.
- O saldo é mantido como campo e também como histórico de movimentações. Escolha uma fonte da verdade e garanta que as duas coisas nunca divirjam (por exemplo, o saldo é sempre derivado das movimentações, ou toda alteração passa por um único método).
- Se `MovimentacaoEstoque` guarda só tipo e quantidade, falta **data**, **responsável** e **motivo/campanha de origem**. Isso fará falta nos relatórios.

**Pontos transversais**
- Verifique concorrência: duas saídas simultâneas podem deixar o saldo negativo.
- Confirme que toda alteração de estoque acontece dentro de uma transação (`@Transactional` na camada de aplicação).
- Há controle de **ordem das operações** entre inscrição e aprovação de voluntário?

Checklist para você revisar sozinho:
- [ ] Cada regra do BDD tem uma implementação correspondente
- [ ] Nenhum `setter` público que quebre invariantes
- [ ] Exceções de domínio com mensagens claras
- [ ] Controllers sem regra de negócio

**Usuário:** Corrigi a quantidade e a data na movimentação. Vou seguir pros padrões de projeto.

---

## Etapa 6 — Padrões de projeto

**Usuário:** Implementei os cinco padrões:
- **Strategy:** `PrioridadeEntregaStrategy` com `PorNumeroDePessoas` e `PorUrgencia`
- **Observer:** `TurnoAlteradoEvento` e `NotificadorVoluntario`
- **Decorator:** `Voluntario` com selos `SeloBronze` e `SeloOuro`
- **Iterator:** percorrer entregas de uma campanha
- **Proxy:** `RelatorioAdministrativoProxy`

Trecho do Observer e do Proxy:

```java
public class Turno {
    private List<Observador> observadores = new ArrayList<>();

    public void alterarHorario(LocalDateTime novoInicio, LocalDateTime novoFim) {
        this.inicio = novoInicio;
        this.fim = novoFim;
        for (Observador o : observadores) {
            o.atualizar(this);
        }
    }
}

public class RelatorioAdministrativoProxy implements Relatorio {
    private RelatorioReal real = new RelatorioReal();

    public String gerar(Usuario usuario) {
        return real.gerar(usuario);
    }
}
```

Está de acordo com os padrões?

**Assistente:** Vou verificar padrão por padrão.

**Observer**
- A estrutura básica existe (sujeito, observadores, notificação).
- Problemas:
  - Não há `adicionarObservador`/`removerObservador` no trecho. Sem isso, o voluntário nunca é registrado nem removido ao **cancelar a inscrição**.
  - O horário é alterado **sem validar** (início antes do fim), e as notificações disparam mesmo assim.
  - Se um observador lançar exceção, os demais não são notificados. Pense em como isolar falhas.
  - O `Turno` conhecer a lista de observadores aumenta o acoplamento com a entidade JPA. Avalie se `Turno` deve emitir um evento e outro componente notificar.

**Proxy**
- **Não está cumprindo o papel.** O proxy apenas delega; não há **nenhuma verificação de acesso**. O objetivo do padrão aqui é controlar quem pode gerar relatórios administrativos.
- `RelatorioReal` é instanciado dentro do proxy. Prefira injetar, para permitir testes com dublê.
- O que acontece quando o usuário não tem permissão? Defina a exceção/comportamento e escreva um cenário BDD para isso.

**Strategy, Decorator e Iterator**
Para validar com segurança, preciso ver o código. Enquanto isso, pontos para você se perguntar:
- **Strategy:** a estratégia é escolhida em um único lugar (fábrica ou configuração)? Ou há `if/else` espalhado, o que anularia o padrão?
- **Decorator:** os selos implementam a mesma interface de `Voluntario` e delegam ao objeto decorado? A composição de vários selos funciona?
- **Iterator:** ele expõe a coleção interna ou só percorre? Respeita a ordem esperada (por prioridade)?

**Usuário:** Entendi o problema do Proxy. Vou corrigir e te mando Strategy, Decorator e Iterator.

---

## Etapa 7 — Testes unitários

**Usuário:** Escrevi os testes unitários com JUnit 5 e Mockito. Cobertura de 78% segundo o JaCoCo. Colei dois testes:

```java
@Test
void deveImpedirInscricaoEmTurnoLotado() {
    Turno turno = new Turno(inicio, fim, 1);
    turno.inscrever(voluntarioA);

    assertThrows(TurnoLotadoException.class,
        () -> turno.inscrever(voluntarioB));
}

@Test
void deveNotificarVoluntarios() {
    Observador obs = mock(Observador.class);
    turno.adicionarObservador(obs);

    turno.alterarHorario(novoInicio, novoFim);

    verify(obs).atualizar(turno);
}
```

Pode verificar a qualidade?

**Assistente:** Os dois testes estão bem estruturados. Minha avaliação:

**Pontos positivos**
- Nomes descrevem o comportamento esperado.
- Usam o padrão Arrange-Act-Assert.
- O teste de Observer usa `mock` e `verify`, o que é adequado para testar interação.

**Pontos de atenção**
1. **Teste de lotação:** além de verificar a exceção, confirme que o **estado não mudou** (o voluntário B não aparece nas inscrições).
2. **Teste do Observer:** verifica que o observador foi chamado, mas não que o horário **foi atualizado**. Considere também testar:
   - observador removido **não** é notificado;
   - alteração com horário inválido **não** notifica ninguém.
3. **Casos de borda ausentes** (pelo que você descreveu): quantidade zero/negativa no estoque, inscrição duplicada, voluntário não aprovado, saldo exato (quantidade == saldo).
4. **Cobertura de 78% não garante qualidade.** Olhe a cobertura de **ramos** (branch), especialmente nas regras de negócio, e considere mutation testing (PIT) se houver tempo.
5. **Isolamento:** testes de domínio não devem depender de Spring nem de banco. Reserve `@DataJpaTest` e `@WebMvcTest` para as camadas de infraestrutura e web.
6. **Padrões:** há testes para cada padrão? Especialmente:
   - Strategy: uma entrada, resultados diferentes por estratégia
   - Decorator: composição de selos
   - Proxy: acesso permitido **e** negado
   - Iterator: coleção vazia e ordem

**Usuário:** Vou adicionar esses casos e subir a cobertura de ramos.

---

## Etapa 8 — Notificações, relatórios e feedback

**Usuário:** Também concluí as funcionalidades de menor complexidade: notificações de mudança de campanha, relatórios de doações/estoque/horas voluntárias e avaliação após a ação. Os relatórios passam pelo Proxy agora, com verificação de perfil `ADMIN`.

**Assistente:** Ótimo. Pontos a verificar:

- **Relatório de horas voluntárias:** conta apenas turnos em que o voluntário **compareceu**, ou todos os inscritos? A regra deve estar no glossário e em um cenário BDD.
- **Relatório de estoque:** deriva do histórico de movimentações ou de um campo? Confirme consistência (veja Etapa 5).
- **Notificação:** o que acontece se o envio falhar? Há registro da notificação (lida/não lida)?
- **Feedback:** só é possível avaliar uma ação em que o voluntário participou? Uma avaliação por participação?
- **Proxy:** teste o acesso negado para voluntário comum e coordenador.

---

## Etapa 9 — Frontend e integração

**Usuário:** Fiz o frontend em Angular com as telas de campanhas, turnos e estoque, consumindo a API REST. como valido isso baseado no arquivo de entregas que mandei?

**Assistente:** Para validar a integração, confira:

1. **Contrato da API:** os DTOs não expõem entidades JPA diretamente? Evita vazamento de dados e problemas de serialização (referências circulares).
2. **Erros:** as exceções de domínio viram respostas HTTP adequadas (400, 404, 409, 403) via `@ControllerAdvice`? O Angular exibe mensagens úteis?
3. **Validação:** validação de entrada no backend (Bean Validation) além do frontend.
4. **Segurança:** o frontend esconde o menu de relatórios para não administradores, mas o **backend** também bloqueia? (Proxy + autorização.)
5. **Estados da interface:** carregando, vazio, erro e sucesso.
6. **Concorrência na tela:** inscrição em turno que lotou segundos antes. A interface lida com o erro?

---

## Etapa 10 — Revisão final antes da entrega

**Usuário:** Terminei tudo. Pode fazer uma verificação geral antes de entregar?

**Assistente:** Segue o checklist de validação final. Marque o que você confirmou.

### Requisitos do trabalho
- [ ] Sistema web com banco relacional (usuários, campanhas, itens, doações, estoque, turnos, inscrições, entregas)
- [ ] Funcionalidades completas (criar, editar, excluir/encerrar), não só consultas
- [ ] Linguagem ubíqua documentada e **usada no código**
- [ ] Subdomínios DDD e mapa de contexto
- [ ] Histórias de usuário e cenários BDD cobrindo caminhos felizes e de falha

### Padrões de projeto
- [ ] **Strategy:** prioridade de entrega, escolhida sem `if/else` espalhado
- [ ] **Observer:** inscrição/remoção de observadores; aviso em mudança de turno
- [ ] **Decorator:** selos combináveis
- [ ] **Iterator:** percorre entregas/itens sem expor a coleção
- [ ] **Proxy:** realmente bloqueia acesso não autorizado

### Qualidade
- [ ] Regras de negócio nas entidades, não nos controllers
- [ ] Testes unitários passando (`mvn test`) e independentes de ordem
- [ ] Cobertura de ramos nas regras críticas
- [ ] Sem segredos ou senhas no repositório
- [ ] README com instruções para rodar backend e frontend

### Documentação e divisão do grupo
- [ ] Cada integrante com funcionalidades próprias e identificáveis no histórico do Git
- [ ] Diagrama de classes ou de contexto na pasta `docs/`
- [ ] Decisões de projeto registradas (por que cada padrão foi usado onde foi)

**Usuário:** Vou percorrer o checklist item por item. Se algo ficar em dúvida, volto aqui com o trecho de código.

**Assistente:** Perfeito. Quando precisar, envie o código (ou os testes) de qualquer item e eu faço a verificação. Boa entrega!

---

*Fim da conversa.*
