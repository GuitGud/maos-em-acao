# language: pt
Funcionalidade: Ciclo de vida das campanhas
  Cenário: Exigir necessidades antes de publicar
    Quando publico a campanha sem necessidades
    Então a operação deve ser recusada com "necessidade"

  Cenário: Impedir encerramento com trabalho pendente
    Dado uma campanha publicada com estoque de 10 unidades
    E um turno futuro com 2 vagas
    Quando a gestão encerra a campanha
    Então a operação deve ser recusada com "turnos abertos"

  Cenário: Encerrar após cancelar os turnos
    Dado uma campanha publicada com estoque de 10 unidades
    E um turno futuro com 2 vagas
    Quando a gestão cancela o turno
    E a gestão encerra a campanha
    Então a operação deve ser concluída
