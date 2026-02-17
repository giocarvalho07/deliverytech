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

## Modelo de Dados

A API utiliza relacionamentos complexos para garantir a integridade do negócio:


* **Cliente ↔ Pedido**: Um cliente pode ter vários pedidos (1:N).
* **Restaurante ↔ Produto**: Um restaurante gerencia seu próprio cardápio (1:N).
* **Pedido ↔ ItemPedido**: Um pedido é composto por vários itens (1:N).
* **Produto ↔ ItemPedido**: Um produto pode estar presente em vários itens de pedidos diferentes.

## Arquitetura do Projeto

O projeto segue o padrão de camadas:

* Model (Entities): Representação do banco de dados.
* Repository: Camada de acesso aos dados com JPQL e SQL Nativo.
* Service: Regras de negócio e cálculos.
* Controller: Exposição dos Endpoints REST.
* DTO/Projection: Camada de transporte e otimização de dados.


---

## Configuração e Execução

### Como Rodar o Projeto
Clone o repositório.

Certifique-se de que o Lombok está instalado na sua IDE.

Execute a aplicação através da classe DeliveryApiApplication.

O sistema utiliza um DataLoader automático que popula o banco de dados H2 ao iniciar.


### Certifique-se de ter o Maven instalado e execute:

Bash

./mvnw spring-boot:run
A API estará disponível em: http://localhost:8080

---

CLIENTE
* POST/clientes,Cadastra um novo cliente no sistema.
* GET/clientes,Retorna a lista de todos os clientes com status ativo.
* GET/clientes/{id},Busca os detalhes de um cliente específico pelo seu ID.
* PUT/clientes/{id},"Atualiza as informações cadastrais (nome, endereço, etc)."
* DELETE/clientes/{id},Realiza a inativação (exclusão lógica) do cliente.

RESTAURANTE
* POST/restaurantes,Cadastra um novo estabelecimento.
* GET/restaurantes,Lista todos os restaurantes cadastrados.
* GET/restaurantes/categoria/{cat},"Filtra estabelecimentos por categoria (ex: Pizza, Japonesa)."

PRODUTO
* POST/produtos,Adiciona um novo item ao cardápio de um restaurante.
* GET/produtos/restaurante/{id},Lista todos os produtos vinculados a um restaurante específico.

PEDIDO
* POST/pedidos,Registra um novo pedido contendo múltiplos itens.
* GET/pedidos/cliente/{id},Consulta o histórico completo de pedidos de um cliente.
* PATCH/pedidos/{id}/status,"Atualiza o status do pedido (Ex: PENDENTE, CONFIRMADO, ENTREGUE)."

ITEMPEDIDO
* GET/item-pedidos,Consulta o histórico completo de itens de pedidos.
* GET/item-pedidos/pedido{id},Busca os detalhes de um item pedido específico pelo seu ID.

RELATORIO
* GET/relatorio/total-vendas-por-restaurante,Consulta o histórico total de vendas por restaurantes.


---


Postman Collection
* Para facilitar os testes de integração e validar os fluxos da API, disponibilizamos uma collection pronta:

* Localize o arquivo: O arquivo encontra-se na raiz do projeto com o nome delivery_api.postman_collection.json.

* Importação: No Postman, clique no botão Import e selecione o arquivo mencionado.

* Variáveis: A coleção já está configurada para usar a variável {{baseUrl}}. Certifique-se de que ela aponta para http://localhost:8080.


Interface Visual (Swagger UI): http://localhost:8080/swagger-ui/index.html

* (É aqui que você visualiza os botões, testa os endpoints e vê os modelos JSON).

* Documentação Técnica (OpenAPI JSON): http://localhost:8080/v3/api-docs

* (Este link retorna um JSON puro com toda a especificação da sua API, usado por ferramentas de automação).


## Guia de Testes e Qualidade

### Tecnologias de Teste Utilizadas
JUnit 5: Framework base para execução de testes unitários e de integração.

Mockito: Utilizado para criar dublês de teste (Mocks) em camadas de Service.

MockMvc: Utilizado para simular requisições HTTP em testes de integração de Controllers.

JaCoCo: Ferramenta de análise de cobertura de código.

### Cobertura de Código (JaCoCo)
Configuramos o projeto para manter um alto padrão de qualidade. O build do Maven está condicionado às seguintes métricas:

Meta de Cobertura: Mínimo de 80% de linhas cobertas.

Exclusões: Classes de configuração (config), DTOs, entidades (model) e exceções personalizadas são excluídas da métrica para focar na lógica de negócio (Services e Controllers).

Comandos Maven
Utilize os comandos abaixo no terminal para gerenciar o ciclo de testes e relatórios.

1. Execução Completa (Verificação de Qualidade)
Executa todos os testes, gera o relatório visual e valida a regra de 80% de cobertura. Este comando falha o build se a meta não for atingida.

---

Bash
mvn clean test jacoco:report jacoco:check
2. Apenas Testes de Integração
Para rodar especificamente os testes que utilizam o contexto do Spring e MockMvc:

---

Bash
mvn test -Dtest=*IT
3. Gerar Relatório Visual
Caso queira apenas atualizar o relatório HTML sem realizar o check de cobertura:

---

Bash
mvn jacoco:report
O relatório será gerado em: target/site/jacoco/index.html

4. Ignorar Verificação de Cobertura
Para realizar o build ignorando temporariamente a trava de 80% (uso restrito):

---

Bash
mvn install -Djacoco.skip=true

### Como analisar o Relatório
Ao abrir o index.html gerado pelo JaCoCo, observe as cores:

🟢 Verde: Código totalmente coberto por testes.

🟡 Amarelo: Branches (desvios como if/else) parcialmente cobertos.

🔴 Vermelho: Código não exercitado pelos testes.

### Autenticação nos Testes
Como a API é protegida por Spring Security, os testes de integração de Controller utilizam a anotação @WithMockUser para simular credenciais válidas e evitar erros 403 Forbidden.

Dica de Desenvolvimento: Antes de realizar um push, sempre execute o "Combo de Qualidade" para garantir que sua alteração não reduziu a cobertura global do projeto para menos de 80%.


󰞵 Desenvolvedor
[Giovanni de Carvalho] - [TURMA 2602] Desenvolvido com JDK 21 e Spring Boot 3.2.x
