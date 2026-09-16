# language: pt
Funcionalidade: Estoque e distribuição rastreáveis
  Esquema do Cenário: Entregar sem permitir saldo negativo
    Dado uma campanha publicada com estoque de 10 unidades
    Quando registro uma entrega de <quantidade> unidades
    Então o estoque deve ser <saldo>
    E a operação deve ser concluída
    Exemplos:
      | quantidade | saldo |
      | 1          | 9     |
      | 10         | 0     |

  Cenário: Rejeitar entrega acima do saldo
    Dado uma campanha publicada com estoque de 10 unidades
    Quando registro uma entrega de 11 unidades
    Então a operação deve ser recusada com "insuficiente"
    E o estoque deve ser 10

  Cenário: Estornar entrega uma única vez
    Dado uma campanha publicada com estoque de 10 unidades
    Quando registro uma entrega de 4 unidades
    E estorno a entrega
    Então o estoque deve ser 10
    Quando estorno a entrega
    Então a operação deve ser recusada com "já estornada"
    E o estoque deve ser 10

  Esquema do Cenário: Ajustar contagem física
    Dado uma campanha publicada com estoque de 10 unidades
    Quando registro um ajuste de <ajuste> unidades
    Então o estoque deve ser <saldo>
    E a operação deve ser concluída
    Exemplos:
      | ajuste | saldo |
      | 3      | 13    |
      | -4     | 6     |

  Cenário: Recusar ajuste que criaria saldo negativo
    Dado uma campanha publicada com estoque de 10 unidades
    Quando registro um ajuste de -11 unidades
    Então a operação deve ser recusada com "insuficiente"
    E o estoque deve ser 10
