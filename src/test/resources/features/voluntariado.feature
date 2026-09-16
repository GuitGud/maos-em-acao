# language: pt
Funcionalidade: Participação segura nos turnos
  Contexto:
    Dado uma campanha publicada com estoque de 10 unidades
    E um turno futuro com 1 vagas

  Cenário: Inscrever voluntário aprovado
    Quando o voluntário se inscreve
    Então há 1 inscrições ativas
    E a operação deve ser concluída

  Cenário: Rejeitar cadastro pendente
    Dado um voluntário com cadastro pendente
    Quando o voluntário se inscreve
    Então a operação deve ser recusada com "não aprovado"

  Cenário: Aprovar e avisar novo voluntário
    Dado um voluntário com cadastro pendente
    Quando a gestão aprova o voluntário
    E o voluntário se inscreve
    Então a operação deve ser concluída
    E o voluntário recebe uma notificação

  Cenário: Não exceder vagas com inscrição repetida
    Quando o voluntário se inscreve
    E o voluntário se inscreve
    Então a operação deve ser recusada com "lotado"
    E há 1 inscrições ativas

  Cenário: Cancelamento devolve a vaga
    Quando o voluntário se inscreve
    E o voluntário cancela a inscrição
    Então há 0 inscrições ativas

  Cenário: Alteração de turno dispara aviso persistente
    Quando o voluntário se inscreve
    E a gestão altera o título do turno
    Então o voluntário recebe uma notificação

  Cenário: Presença gera horas e permite feedback
    Quando o voluntário se inscreve
    Dado que o turno inscrito já terminou
    Quando a gestão confirma a presença
    E o voluntário avalia a ação com nota 5
    Então a operação deve ser concluída
    E o voluntário acumula 4 horas

  Cenário: Avaliação exige participação confirmada
    Quando o voluntário se inscreve
    E o voluntário avalia a ação com nota 5
    Então a operação deve ser recusada com "presença"

  Cenário: Relatórios protegidos por perfil
    Quando o voluntário gera um relatório
    Então a operação deve ser recusada com "restrito"
