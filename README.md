# User Management API

API REST para gerenciamento de usuários e endereços, com autenticação JWT, controle de acesso por perfil e consulta de CEP via ViaCEP.

## Tecnologias

- **Backend:** Java 17, Spring Boot 4.1.1, Spring Web MVC, Spring Data JPA, Spring Security, Spring Validation e Springdoc OpenAPI.
- **Banco de dados:** H2 e PostgreSQL.
- **Testes:** JUnit 5, Mockito e Spring Boot Test; JaCoCo para geração de relatório de cobertura.
- **Infraestrutura:** Maven Wrapper, Docker e Docker Compose.

Este repositório contém apenas o backend. Não há código Angular, configuração de frontend, Jest ou Nginx versionados aqui.

## Funcionalidades

- Criação, consulta, atualização e desativação de usuários.
- Criação, consulta, atualização e desativação de endereços associados a usuários.
- Definição de endereço principal, com regra para manter um endereço principal por usuário.
- Autenticação JWT com perfis `ADMIN` e `USER` e autorização por endpoint e propriedade do recurso.
- Validação e preenchimento dos dados de endereço consultando o ViaCEP; CEPs consultados são armazenados em cache.
- Listagem paginada de usuários ativos, com filtros opcionais por nome e e-mail.
- Auditoria de criação e atualização de entidades.
- Tratamento centralizado de exceções da aplicação.

## Organização

O código da aplicação está em `user-management-api/src/main/java/com/gustavorodrigues/user_management_api`:

- `controller`: endpoints REST de autenticação, usuários e endereços.
- `services`: regras de negócio e coordenação do acesso aos dados e ao ViaCEP.
- `repository`: interfaces Spring Data JPA.
- `dto`: objetos usados nas requisições e respostas.
- `model`: entidades JPA, incluindo usuário, endereço, perfil e campos auditáveis.
- `config`: configurações de segurança, JWT, OpenAPI, JPA, cache, ViaCEP e criação inicial de perfis/administrador.
- `client/viacep`: cliente HTTP da API ViaCEP.
- `Exceptions`: exceções de domínio e tratamento global.

## Autenticação e autorização

Envie `email` e `password` para `POST /api/v1/auth/login`. A resposta inclui `accessToken`, `expiresIn` e `role`. Para acessar rotas protegidas, envie o token no cabeçalho `Authorization: Bearer <token>`.

As rotas de usuários reservadas à administração exigem `ADMIN`. Um usuário pode consultar e atualizar o próprio cadastro e gerenciar os próprios endereços; `ADMIN` pode administrar usuários e endereços. A configuração Spring Security define sessões como `STATELESS`. As senhas são armazenadas com BCrypt.

## Banco de dados

O perfil padrão configura H2 em memória. O perfil `dev` também configura H2. O perfil `prod` configura PostgreSQL; o Docker Compose define o serviço PostgreSQL e inicia a aplicação com esse perfil. O Hibernate está configurado com `ddl-auto=update`; não foram encontradas migrations.

As configurações JWT esperam os caminhos das chaves RSA nas propriedades `JWT_PUBLIC_KEY_PATH` e `JWT_PRIVATE_KEY_PATH`. Configure esses caminhos para arquivos apropriados no ambiente local sem incluir chaves privadas no controle de versão.

## ViaCEP

O cliente usa `RestClient` com base `https://viacep.com.br/ws`. Antes da consulta, o serviço remove caracteres não numéricos e verifica se o CEP tem oito dígitos. A resposta do ViaCEP fornece logradouro, bairro, cidade e UF para o endereço. CEP inexistente ou inválido gera erro de requisição. As respostas são cacheadas com a chave `cep`.

## Auditoria

As entidades que herdam de `Auditable` registram `createdAt`, `createdBy`, `updatedAt` e `updatedBy`, usando Spring Data JPA Auditing. Os campos de usuário identificam o UUID do principal autenticado quando disponível.

## API e Swagger

Com a aplicação iniciada na porta configurada (`8081`), acesse [Swagger UI](http://localhost:8081/swagger-ui/index.html). A configuração OpenAPI registra autenticação HTTP Bearer no formato JWT. Use **Authorize** e informe o token JWT para testar operações protegidas.

Principais rotas:

| Método | Rota | Ação |
| --- | --- | --- |
| `POST` | `/api/v1/auth/login` | Autenticação |
| `POST` | `/api/v1/users/create` | Criação de usuário por `ADMIN` |
| `GET` | `/api/v1/users/list` | Lista paginada e filtrada por `ADMIN` |
| `GET` | `/api/v1/users/{id}` | Consulta de usuário |
| `PATCH` | `/api/v1/users/{id}` | Atualização de usuário |
| `PATCH` | `/api/v1/users/{id}/deactivate` | Desativação de usuário por `ADMIN` |
| `POST` | `/api/v1/endereco/{userId}` | Criação de endereço |
| `GET` | `/api/v1/endereco/{id}` | Consulta de endereço |
| `PATCH` | `/api/v1/endereco/{id}` | Atualização de endereço |
| `DELETE` | `/api/v1/endereco/{id}` | Desativação de endereço |

## Como executar localmente

Pré-requisitos: JDK 17 e acesso às chaves RSA configuradas pelos caminhos JWT descritos acima. Os comandos a seguir devem ser executados na pasta `user-management-api`.

```powershell
.\mvnw.cmd spring-boot:run
```

O perfil padrão usa H2 em memória na configuração principal. A aplicação escuta na porta `8081`.

## Testes

Os testes backend cobrem serviços, repositórios, consulta ViaCEP e integração dos endpoints com MockMvc. Execute-os com:

```powershell
.\mvnw.cmd test
```

O plugin JaCoCo está configurado para gerar o relatório após a fase de testes em `target/site/jacoco/`. Não há um percentual de cobertura verificado para documentar. A configuração de testes referencia `keys/private.test.key`, que não está presente nos arquivos do projeto; os testes que carregam o contexto podem exigir que essa chave esteja disponível.

## Docker

Há um `Dockerfile` de build em múltiplas etapas para empacotar a aplicação e um `Dockerfile.dev` usado pelo Compose. O arquivo `docker-compose.yml` inicia PostgreSQL e API:

```powershell
docker compose up --build
```

O serviço da API fica exposto na porta `8081` e o PostgreSQL na `5432`. O Compose monta a pasta local `secrets` como somente leitura no container e configura os caminhos das chaves JWT. Não publique credenciais ou material de chave privada.

## Estrutura resumida

```text
.
├── README.md
└── user-management-api/
    ├── Dockerfile
    ├── Dockerfile.dev
    ├── docker-compose.yml
    ├── pom.xml
    └── src/
        ├── main/java/...
        ├── main/resources/
        └── test/
```

## Demonstração

Vídeo demonstrativo: https://youtu.be/kqB2W80dzU4

## Decisões técnicas observáveis

- O backend usa Spring Data JPA com persistência relacional em H2 ou PostgreSQL.
- A autenticação usa JWT assinado com chaves RSA e a aplicação opera sem estado de sessão.
- Usuários e endereços são desativados por campo de estado, preservando os registros no banco.
- A consulta de CEP está isolada em um cliente e suas respostas usam o cache do Spring.

## Considerações finais

O repositório documentado entrega a API backend de gerenciamento de usuários e endereços. A interface Angular e os artefatos de frontend mencionados no escopo do teste não estão presentes neste projeto.
