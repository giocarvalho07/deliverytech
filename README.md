# Delivery Tech API 🍔

Uma API REST completa para gerenciamento de ecossistemas de delivery, permitindo o controle de clientes, cardápios de restaurantes e o fluxo de processamento de pedidos em tempo real.

## Tecnologias Utilizadas

* **Java 17/21**: Linguagem base do projeto.
* **Spring Boot 3**: Framework para construção da aplicação e gestão de dependências.
* **Spring Data JPA**: Abstração de persistência de dados.
* **H2 Database**: Banco de dados em memória para desenvolvimento ágil.
* **Lombok**: Redução de código boilerplate (Getters, Setters, Construtores).
* **Jakarta Persistence (Hibernate)**: Mapeamento Objeto-Relacional (ORM).

---

## Arquitetura e Modelo de Dados

A API utiliza relacionamentos complexos para garantir a integridade do negócio:



* **Cliente ↔ Pedido**: Um cliente pode ter vários pedidos (1:N).
* **Restaurante ↔ Produto**: Um restaurante gerencia seu próprio cardápio (1:N).
* **Pedido ↔ ItemPedido**: Um pedido é composto por vários itens (1:N).
* **Produto ↔ ItemPedido**: Um produto pode estar presente em vários itens de pedidos diferentes.

---

## Configuração e Execução

### 1. Ajuste de Inicialização (Importante)
Para que os dados iniciais do arquivo `data.sql` sejam carregados após a criação das tabelas pelo Hibernate, o arquivo `src/main/resources/application.properties` deve conter:

properties
# Adia a execução do data.sql até que o Hibernate crie as tabelas
spring.jpa.defer-datasource-initialization=true
spring.jpa.hibernate.ddl-auto=update```


2. Rodar o Projeto
Certifique-se de ter o Maven instalado e execute:

Bash

./mvnw spring-boot:run
A API estará disponível em: http://localhost:8080


Método,Endpoint,Descrição
POST,/clientes,Cadastra um novo cliente no sistema.
GET,/clientes,Retorna a lista de todos os clientes com status ativo.
GET,/clientes/{id},Busca os detalhes de um cliente específico pelo seu ID.
PUT,/clientes/{id},"Atualiza as informações cadastrais (nome, endereço, etc)."
DELETE,/clientes/{id},Realiza a inativação (exclusão lógica) do cliente.

Método,Endpoint,Descrição
POST,/restaurantes,Cadastra um novo estabelecimento.
GET,/restaurantes,Lista todos os restaurantes cadastrados.
GET,/restaurantes/categoria/{cat},"Filtra estabelecimentos por categoria (ex: Pizza, Japonesa)."

Método,Endpoint,Descrição
POST,/produtos,Adiciona um novo item ao cardápio de um restaurante.
GET,/produtos/restaurante/{id},Lista todos os produtos vinculados a um restaurante específico.

Método,Endpoint,Descrição
POST,/pedidos,Registra um novo pedido contendo múltiplos itens.
GET,/pedidos/cliente/{id},Consulta o histórico completo de pedidos de um cliente.
PATCH,/pedidos/{id}/status,"Atualiza o status do pedido (Ex: PENDENTE, CONFIRMADO, ENTREGUE)."


Postman Collection
Para facilitar os testes de integração e validar os fluxos da API, disponibilizamos uma collection pronta:

Localize o arquivo: O arquivo encontra-se na raiz do projeto com o nome delivery_api.postman_collection.json.

Importação: No Postman, clique no botão Import e selecione o arquivo mencionado.

Variáveis: A coleção já está configurada para usar a variável {{baseUrl}}. Certifique-se de que ela aponta para http://localhost:8080.


󰞵 Desenvolvedor
[Giovanni de Carvalho] - [TURMA 2602] Desenvolvido com JDK 21 e Spring Boot 3.2.x
