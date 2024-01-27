# language: pt
Funcionalidade: API - Payment

  Cenário: Criar Pagamento
    Dado que quero criar um pagamento
    E quando informar todos os campos obrigatórios
    Entao quero cadastrar um pagamento

  Cenário: Realizar Pagamento
    Dado que quero realizar um pagamento
    E existe um pagamento cadastrado
    Entao quero enviar para o mercado livre a requisição e receber um QR code

  Cenário: Busca por pagamento
    Dado que tenho um pagamento cadastrado
    Quando fizer uma consulta por externalReference
    Entao devo retornar o pagamento



