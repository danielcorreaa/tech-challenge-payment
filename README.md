# 
# Microsserviço tech-challenge-payment

Microsserviço responsável pelo gerenciamento de pagamentos


## Autores

- [@danielcorreaa](https://github.com/danielcorreaa)


## Documentação da API


#### Pagamento do pedido 

```http
  POST api/v1/payment/pay
```

| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `externalReference` | `string` |  **Obrigatório** Identificador do pedido |


#### Webhook do pedido 

```http
  POST api/v1/payment/webhook
```
Mercado pago vai retornar um endpoint no campo resource, com o status do pagamento.


#### Buscar pagamento

```http
  GET api/v1/payment/find/{externalReference}
```
| Parâmetro   | Tipo       | Descrição                           |
| :---------- | :--------- | :---------------------------------- |
| `externalReference` | `string` |  **Obrigatório** Identificador do pedido |


## Stack utilizada


**Back-end:** Java, Spring Boot, Mongodb, Kafka


## Rodando localmente

Clone o projeto

```bash
  git clone https://github.com/danielcorreaa/tech-challenge-payment
```

Entre no diretório do projeto

```bash
  cd tech-challenge-payment
```

Docker

```bash
  docker compose up -d
```

No navegador

```bash
  http://localhost:8085/
```



## Deploy

### Para subir a aplicação usando kubernetes

#### Infraestrutura:

Clone o projeto com a infraestrutura

```bash
  git clone danielcorreaa/tech-challenge-infra-terraform-kubernetes
```
Entre no diretório do projeto

```bash
  cd tech-challenge-infra-terraform-kubernetes/
````

Execute os comandos

```bash   
- run: kubectl apply -f kubernetes/metrics.yaml     
- run: kubectl apply -f kubernetes/mongo/mongo-secrets.yaml 
- run: kubectl apply -f kubernetes/mongo/mongo-configmap.yaml 
- run: kubectl apply -f kubernetes/mongo/mongo-pvc.yaml 
- run: kubectl apply -f kubernetes/mongo/mongo-service.yaml 
- run: kubectl apply -f kubernetes/mongo/mongo-statefulset.yaml

- run: kubectl apply -f kubernetes/kafka/kafka-configmap.yaml
- run: kubectl apply -f kubernetes/kafka/zookeeper-deployment.yaml
- run: kubectl apply -f kubernetes/kafka/zookeeper-service.yaml
- run: kubectl apply -f kubernetes/kafka/kafka-deployment.yaml
- run: kubectl apply -f kubernetes/kafka/kafka-service.yaml
- run: kubectl apply -f kubernetes/kafka/kafka-ui-deployment.yaml

````

#### Aplicação:

docker hub [@repositorio](https://hub.docker.com/r/daniel36/tech-challenge-payment/tags)

Clone o projeto

```bash
  git clone https://github.com/danielcorreaa/tech-challenge-payment
```

Entre no diretório do projeto

```bash
  cd tech-challenge-payment
```

Execute os comandos
```bash   
- run: kubectl apply -f k8s/payment-deployment.yaml
- run: kubectl apply -f k8s/payment-service.yaml     
- run: kubectl apply -f k8s/payment-hpa.yaml
- run: kubectl get svc

````





## OWASP ZAP
*Resultado ataques na api de pagamento e webhook*

No endpoint de pagamento não foi encontrada vunerabilidades

```http
  POST api/v1/payment/pay
  ```

- [@report-payment](https://danielcorreaa.github.io/tech-challenge-payment/before/pay/report.html)


No endpoint webhook foi encontrado uma vunerabilidade de nivel baixo


- [@report-webhook](https://danielcorreaa.github.io/tech-challenge-payment/before/webhook/report.html)



## Documentação Saga

### Padrão escolhido: Coreografia 

#### Razão de utilizar a coreografia
*Escolhi o padrão coreografado para evitar deixar tudo centralizado no serviço de pedidos, no caso de acontecer alguma falha no serviço de pedidos toda a operação de notificar cliente e enviar os pedidos pagos para a cozinha seria paralizada, com a coreografia mesmo que tenha algum problema com o serviço de pedidos, a cozinha ainda recebe os pedidos com pagamentos aprovados, nao parando a produção de pedidos pagos, e os clientes recebem notificaçao de problemas com o pagamento.*

#### Desenho da solução

![Desenho Padrão Saga coreografado.](/images/saga-diagrama.png)

