# VulnerableLab - Hardening de Segurança

Este projeto foi refatorado para solucionar diversas vulnerabilidades de segurança e adotar as melhores práticas de arquitetura em aplicações Spring Boot 3.

---

## 🛠 Alterações Realizadas

### 1. Interrupção do Retorno de Senhas/Entidades Nativas
- **Problema**: Endpoints expunham a entidade JPA `User` diretamente na resposta HTTP, incluindo a senha do usuário em texto plano e expondo a estrutura interna do banco.
- **Solução**: Implementamos DTOs (Data Transfer Objects) de resposta para todas as entidades principais. Os controladores agora encapsulam as entidades nativas em DTOs antes de retorná-los:
  - `UserResponse`: Retorna apenas `id`, `name`, `email` e `role` (oculta a senha).
  - `ProductResponse`: Retorna dados de produtos limpos.
  - `CommentResponse`: Retorna apenas os campos necessários do comentário e o ID do produto.

### 2. Blindagem de Rotas Administrativas
- **Problema**: A rota `/api/admin/**` (usada para excluir e listar usuários) estava exposta e dependia apenas de verificações de interface do usuário (CSS `display: none`).
- **Solução**: Adicionamos o **Spring Security** ao projeto. Configuramos a classe `SecurityConfig` para exigir autenticação em todas as rotas `/api/**` (exceto `/api/auth/**`), e especificamente blindamos as rotas sob `/api/admin/**` exigindo a role `ADMIN` (`.requestMatchers("/api/admin/**").hasRole("ADMIN")`).

### 3. Implementação de Hashing de Senhas (BCrypt)
- **Problema**: As senhas dos usuários eram salvas e comparadas em texto puro (plaintext), permitindo que um vazamento de banco de dados comprometesse todas as contas.
- **Solução**: Registramos o bean `BCryptPasswordEncoder` no Spring Security. Atualizamos a inicialização de dados (`seedData` em `VulnerableLabApplication`) e a rota de registro (`/api/auth/register`) para criptografar as senhas usando BCrypt antes de salvar no banco de dados. Os logins agora usam `passwordEncoder.matches()` para comparar hashes de forma segura.

### 4. Autenticação Real com JWT (JSON Web Token)
- **Problema**: O sistema gerava tokens falsos estáticos baseados no ID e Role em texto puro (`TOKEN-id-role`).
- **Solução**: Substituímos os tokens falsos por tokens JWT gerados de forma criptografada usando a biblioteca `java-jwt`. O token contém as claims de identificação (e-mail) e privilégios (role) e é verificado de forma stateless em cada requisição através do filtro customizado `SecurityFilter`.

### 5. Eliminação de Concatenações de SQL/JPQL
- **Problema**: O endpoint `/api/users/search-unsafe` sofria de JPQL/SQL Injection por concatenar diretamente o parâmetro do usuário em uma string de consulta JPQL executada pelo `EntityManager`.
- **Solução**: Modificamos a consulta em `UserController` para utilizar parameter binding (:term) através do método `.setParameter()`, eliminando qualquer risco de SQL ou JPQL Injection.

### 6. Validação do Input de Dados com `@Valid` no Backend
- **Problema**: DTOs e entidades aceitavam entradas nulas, vazias, ou com formato incorreto diretamente no banco de dados sem validação.
- **Solução**: Adicionamos validações anotadas (`jakarta.validation.constraints`) nos DTOs de entrada:
  - `RegisterRequest`: Valida e-mail (`@Email`), obrigatoriedade de campos (`@NotBlank`) e tamanho mínimo de senha (`@Size(min = 6)`).
  - `LoginRequest`: Valida formato e presença de e-mail e senha.
  - `ProductRequest`: Valida preço positivo ou zero (`@PositiveOrZero`) e campos obrigatórios.
  - `CommentRequest`: Valida corpo do comentário e associação de produto.
- Todos os controladores que recebem corpos de requisições foram alterados para incluir `@Valid` na anotação `@RequestBody`.
- O `GlobalExceptionHandler` foi atualizado para capturar exceções `MethodArgumentNotValidException` e retornar mensagens amigáveis em JSON com o status `400 Bad Request` indicando os erros exatos em cada campo.

---

## 📂 Arquivos Modificados e Criados

| Arquivo | Ação | Descrição |
| :--- | :--- | :--- |
| [pom.xml](file:///c:/Users/aliso/OneDrive/Desktop/VulnerableLab-master/pom.xml) | Modificado | Adicionadas dependências do Spring Security e `java-jwt`. |
| [VulnerableLabApplication.java](file:///c:/Users/aliso/OneDrive/Desktop/VulnerableLab-master/src/main/java/br/unipar/frameworks/VulnerableLabApplication.java) | Modificado | Injeta o codificador e cifra as senhas dos usuários de demonstração no banco de dados (H2). |
| [GlobalExceptionHandler.java](file:///c:/Users/aliso/OneDrive/Desktop/VulnerableLab-master/src/main/java/br/unipar/frameworks/exception/GlobalExceptionHandler.java) | Modificado | Retorna respostas formatadas com HTTP 400 em caso de erro de validação de DTO. |
| [AuthController.java](file:///c:/Users/aliso/OneDrive/Desktop/VulnerableLab-master/src/main/java/br/unipar/frameworks/controller/AuthController.java) | Modificado | Registro e login usando DTOs, BCrypt, e geração de tokens JWT reais. |
| [UserController.java](file:///c:/Users/aliso/OneDrive/Desktop/VulnerableLab-master/src/main/java/br/unipar/frameworks/controller/UserController.java) | Modificado | Remove concatenação no JPQL do search-unsafe, retorna DTOs `UserResponse`. |
| [ProductController.java](file:///c:/Users/aliso/OneDrive/Desktop/VulnerableLab-master/src/main/java/br/unipar/frameworks/controller/ProductController.java) | Modificado | Enforce de validação no DTO de produto e retorno de `ProductResponse`. |
| [CommentController.java](file:///c:/Users/aliso/OneDrive/Desktop/VulnerableLab-master/src/main/java/br/unipar/frameworks/controller/CommentController.java) | Modificado | Validação do DTO de comentários e retorno de `CommentResponse`. |
| [AdminController.java](file:///c:/Users/aliso/OneDrive/Desktop/VulnerableLab-master/src/main/java/br/unipar/frameworks/controller/AdminController.java) | Modificado | Protege os retornos com `UserResponse`. |
| [UserResponse.java](file:///c:/Users/aliso/OneDrive/Desktop/VulnerableLab-master/src/main/java/br/unipar/frameworks/dto/UserResponse.java) | **Novo** | DTO de saída para dados de usuários. |
| [ProductRequest.java](file:///c:/Users/aliso/OneDrive/Desktop/VulnerableLab-master/src/main/java/br/unipar/frameworks/dto/ProductRequest.java) | **Novo** | DTO de entrada para produtos com regras de validação. |
| [ProductResponse.java](file:///c:/Users/aliso/OneDrive/Desktop/VulnerableLab-master/src/main/java/br/unipar/frameworks/dto/ProductResponse.java) | **Novo** | DTO de saída para dados de produtos. |
| [CommentResponse.java](file:///c:/Users/aliso/OneDrive/Desktop/VulnerableLab-master/src/main/java/br/unipar/frameworks/dto/CommentResponse.java) | **Novo** | DTO de saída para dados de comentários. |
| [TokenService.java](file:///c:/Users/aliso/OneDrive/Desktop/VulnerableLab-master/src/main/java/br/unipar/frameworks/security/TokenService.java) | **Novo** | Serviço para gerar e decodificar tokens JWT reais. |
| [SecurityFilter.java](file:///c:/Users/aliso/OneDrive/Desktop/VulnerableLab-master/src/main/java/br/unipar/frameworks/security/SecurityFilter.java) | **Novo** | Interceptador HTTP para carregar o contexto de autenticação através do JWT. |
| [SecurityConfig.java](file:///c:/Users/aliso/OneDrive/Desktop/VulnerableLab-master/src/main/java/br/unipar/frameworks/security/SecurityConfig.java) | **Novo** | Configuração do Spring Security com autenticação JWT e BCrypt. |

---

## 🚀 Como testar a aplicação localmente

1. **Compilar e rodar o projeto**:
   ```bash
   mvn spring-boot:run
   ```
2. **Consultar o banco H2**:
   Acesse no navegador `http://localhost:8081/h2-console`
   - JDBC URL: `jdbc:h2:mem:vulnerablelab`
   - User Name: `sa`
   - Password: (vazio)
   - Verifique que as senhas na tabela `users` estão salvas criptografadas (hashes).

3. **Login e Acesso administrativo**:
   - Faça POST para `http://localhost:8081/api/auth/login` com JSON:
     ```json
     {
       "email": "admin@lab.local",
       "password": "admin123"
     }
     ```
   - Obtenha o token JWT gerado.
   - Faça requisição GET para `http://localhost:8081/api/admin/users` incluindo o cabeçalho HTTP `Authorization: Bearer <seu_token_jwt>`.
