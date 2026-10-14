# Pedido original do usuário

Transcrição do conteúdo textual do pedido. O arquivo de referência fornecido foi `Trabalho em grupo.pdf`.

> Uma ótima ideia é criar uma **plataforma web de gestão de campanhas solidárias e voluntários**.
>
> Uma ONG poderia criar campanhas (ex.: arrecadação de alimentos), cadastrar necessidades, abrir turnos de voluntariado e acompanhar a distribuição das doações. Voluntários se inscrevem nos turnos, registram entregas e recebem notificações sobre mudanças.
>
> Funcionalidades que podem ser divididas entre 4 a 6 integrantes:

| Funcionalidade | Complexidade proposta no pedido |
|---|---|
| Criar, editar, publicar e encerrar campanhas | Alta |
| Gerenciar estoque de itens doados e registrar movimentações | Alta |
| Criar turnos e permitir inscrição/cancelamento de voluntários | Alta |
| Registrar entregas para famílias/instituições atendidas | Média/Alta |
| Notificações de mudanças de turno ou campanha | Média |
| Cadastro e aprovação de voluntários | Média |
| Relatórios de doações, estoque e horas voluntárias | Média |
| Avaliação/feedback após uma ação | Baixa/Média |

> Ela atende bem aos requisitos porque:
>
> - É um sistema web com banco relacional: usuários, campanhas, itens, doações, estoque, turnos, inscrições e entregas.
> - Permite funcionalidades completas, não apenas consultas.
> - Tem um domínio claro para DDD: campanhas, voluntariado, estoque e distribuição podem ser subdomínios.
> - Dá para usar linguagem ubíqua, como: “campanha”, “doação”, “turno”, “inscrição”, “movimentação de estoque” e “entrega”.
> - Facilita criar histórias de usuário e cenários BDD, por exemplo: “Como voluntário, quero me inscrever em um turno disponível para participar da ação.”
> - Pode usar os padrões exigidos:
>   - **Strategy:** regras diferentes para calcular prioridade de entrega.
>   - **Observer:** avisar voluntários quando um turno for alterado.
>   - **Decorator:** adicionar benefícios/selos a voluntários conforme participação.
>   - **Iterator:** percorrer entregas ou itens de uma campanha.
>   - **Proxy:** controlar acesso a relatórios administrativos.
> - Combina bem com Java, Spring Boot, JPA e Angular.
>
> Nome possível: **Mãos em Ação**.
>
> Prepare o esqueleto desse projeto, mas deixe para eu fazer as funcionalidades e os testes unitários sozinho, quero só ajuda na validação...
