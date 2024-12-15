
# Gerenciamento de Pedidos - Projeto Spring Boot

## Descrição do Projeto

Este projeto é uma API RESTful para um sistema de gerenciamento de pedidos de uma loja online. Ele oferece funcionalidades como CRUD de pedidos e produtos, cálculo de preços, autenticação e autorização com perfis de usuário, integração de pagamentos e mais. Também há um microsserviço auxiliar para simular pagamentos. Toda a documentação do Swagger foi redigida em inglês para atender as melhores praticas de desenvolvimento;

## Tecnologias Utilizadas

- **Linguagem:** Java 17
- **Framework:** Spring Boot (versão 3.4.0)
- **Banco de Dados:** MySQL 8
- **Mensageria:** RabbitMQ
- **Segurança:** Spring Security (OAuth2 e JWT)
- **Documentação da API:** Swagger/OpenAPI
- **Testes:** JUnit 5, Mockito
- **CI/CD:** GitHub Actions

## Funcionalidades Implementadas

1. **Gerenciamento de Pedidos:**
   - CRUD completo para pedidos.
   - Listagem de pedidos com filtros por status, data de criação e valor.
   - Consulta detalhada de pedidos.

2. **Gerenciamento de Produtos:**
   - CRUD completo para produtos.
   - Consulta de produtos por ID ou listagem de todos os produtos.

3. **Integração de Pagamentos:**
   - Suporte para diferentes formas de pagamento (microsserviço auxiliar).

4. **Autenticação e Autorização:**
   - Perfis de usuário: Admin, Cliente, Operador.
   - Controle de acessos baseado em permissões.

5. **Cálculo de Preços:**
   - Cálculo de total do pedido com aplicação de descontos e taxas.
   - Recalcula automaticamente ao modificar itens do pedido.

6. **Documentação:**
   - Documentação automática gerada com Swagger.
   - Acesse via [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html).

## Configuração do Ambiente

### Dependências Necessárias

- Java 17
- Maven 3.8+
- Docker (para RabbitMQ e MySQL 8)

### Configuração do Banco de Dados

1. Configure o MySQL com as credenciais:
   - **Usuário:** root
   - **Senha:** password
2. Altere as configurações em `application.yml` se necessário.

### Subir o Ambiente com Docker

Execute o comando para iniciar RabbitMQ e o MySQL:

   ```bash
      docker-compose up -d
   ```

## Execução

1. Clone o repositório:
   ```bash
   git clone git@github.com:AndrewPolengolas/teste-guarani.git
   
   cd demo
   ```
2. Os testes podem ser com o plugin do jacoco, os relatoriós estarão em target/site/jacoco/index.html:

   ```bash
   mvn test
   
   mvn jacoco:report
   ```
3. Compile o projeto:
   ```bash
   mvn clean install
   ```
4. Execute a aplicação:
   ```bash
   mvn spring-boot:run
   ```
5. Também será necessário rodar o microsserviço de pagamentos:
   ```bash
   git clone git@github.com:AndrewPolengolas/Teste-guarani-payment.git
   
   cd payment
   ```
6. Compile o projeto:
   ```bash
   mvn clean install
   ```
7. Execute a aplicação:
   ```bash
   mvn spring-boot:run
   ```
8. Acesse a documentação da API no Swagger:
   [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## CI/CD

Pipeline configurado com GitHub Actions:

1. **Branch `develop`:** Executa testes unitários.
2. **PR para `master`:** Executa testes e cria imagem Docker para publicação no Docker Hub.

### Configuração do Docker Hub

Para configurar o Docker Hub:

1. Certifique-se de ter uma conta no [Docker Hub](https://hub.docker.com/).
2. Crie um repositório para a imagem Docker.
3. Configure as credenciais do Docker Hub como segredos no GitHub Actions (`DOCKER_USERNAME` e `DOCKER_PASSWORD`).
4. Atualize o arquivo de workflow do GitHub Actions para usar essas credenciais ao fazer o push da imagem.

## Contato

Para dúvidas, entre em contato pelo e-mail: [andrewnd2009@hotmail.com](mailto:andrewnd2009@hotmail.com).
