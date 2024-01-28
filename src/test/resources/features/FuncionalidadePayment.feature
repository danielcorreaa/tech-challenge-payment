# language: pt
Funcionalidade: API - Payment

  Cenário: Criar Pagamento
    Dado que quero criar um pagamento
    E quando informar todos os campos obrigatórios
    Entao quero cadastrar um pagamento

  Cenário: Busca por pagamento
    Dado que tenho um pagamento cadastrado
    Quando fizer uma consulta por externalReference
    Entao devo retornar o pagamento



