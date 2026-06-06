# Runly API - MVP inicial

Esse back-end já vem com a base do MVP do Runly:

* Cadastro de usuário
* Login com token JWT
* Senha criptografada com BCrypt
* Rotas protegidas por token
* Perfil do usuário logado
* Cadastro/listagem de corridas
* Estatísticas básicas de corrida
* Criação/listagem de posts no feed
* Criação e gerenciamento de grupos
* Sistema de membros, administradores e fundador de grupo

## 1. Criar o banco

No PostgreSQL, crie o banco:

```sql
CREATE DATABASE runly;
```

Depois ajuste o arquivo:

```txt
src/main/resources/application.properties
```

Principalmente:

```properties
spring.datasource.username=postgres
spring.datasource.password=postgres
```

Troque a senha para a senha do seu PostgreSQL.

## 2. Rodar o projeto

No terminal, dentro da pasta do projeto:

```bash
./mvnw spring-boot:run
```

No Windows, se precisar:

```bash
mvnw.cmd spring-boot:run
```

## 3. Testar se a API está online

GET:

```txt
http://localhost:8080/health
```

Resposta esperada:

```json
{
  "status": "Runly API online"
}
```

## 4. Cadastro

POST:

```txt
http://localhost:8080/auth/register
```

Body JSON:

```json
{
  "nome": "Vitor",
  "email": "vitor@email.com",
  "senha": "123456"
}
```

A resposta já traz o token:

```json
{
  "token": "eyJ...",
  "usuario": {
    "id": 1,
    "nome": "Vitor",
    "email": "vitor@email.com"
  }
}
```

## 5. Login

POST:

```txt
http://localhost:8080/auth/login
```

Body JSON:

```json
{
  "email": "vitor@email.com",
  "senha": "123456"
}
```

## 6. Como usar rotas protegidas no Postman

Depois de copiar o token, vá em:

```txt
Authorization -> Type: Bearer Token
```

Cole o token.

Ou mande no header:

```txt
Authorization: Bearer SEU_TOKEN_AQUI
```

## 7. Meu perfil

GET:

```txt
http://localhost:8080/usuarios/me
```

PATCH:

```txt
http://localhost:8080/usuarios/me
```

Body JSON:

```json
{
  "nome": "Vitor Gagliani",
  "bio": "Corredor iniciante e estudante de Sistemas de Informação",
  "fotoPerfil": "https://exemplo.com/foto.png"
}
```

## 8. Criar corrida

POST:

```txt
http://localhost:8080/corridas
```

Body JSON:

```json
{
  "distanciaKm": 5.0,
  "tempoSegundos": 1800,
  "dataCorrida": "2026-05-30T18:30:00",
  "rotaJson": null
}
```

O pace é calculado automaticamente. Nesse exemplo, 1800 segundos = 30 minutos. 30 / 5 = pace 6.0.

## 9. Listar minhas corridas

GET:

```txt
http://localhost:8080/corridas/minhas
```

## 10. Estatísticas

GET:

```txt
http://localhost:8080/corridas/estatisticas
```

## 11. Criar post

POST:

```txt
http://localhost:8080/posts
```

Body JSON:

```json
{
  "texto": "Primeira corrida registrada no Runly!",
  "imagemUrl": null
}
```

## 12. Feed

GET:

```txt
http://localhost:8080/posts/feed
```

## 13. Meus posts

GET:

```txt
http://localhost:8080/posts/meus
```

## 14. Criar grupo

POST:

```txt
http://localhost:8080/grupos
```

Body JSON:

```json
{
  "nome": "Corredores Uniara",
  "descricao": "Grupo para correr depois da aula",
  "fotoPerfil": "https://exemplo.com/grupo.png"
}
```

Ao criar um grupo, o usuário logado vira automaticamente:

* fundador do grupo;
* membro do grupo;
* administrador do grupo.

Resposta esperada:

```json
{
  "id": 1,
  "nome": "Corredores Uniara",
  "descricao": "Grupo para correr depois da aula",
  "fotoPerfil": "https://exemplo.com/grupo.png",
  "fundador": {
    "id": 1,
    "nome": "Vitor",
    "email": "vitor@email.com"
  },
  "totalMembros": 1,
  "totalAdministradores": 1
}
```

## 15. Editar grupo

PUT:

```txt
http://localhost:8080/grupos/1
```

Body JSON:

```json
{
  "nome": "Runly Uniara",
  "descricao": "Grupo oficial dos corredores da Uniara",
  "fotoPerfil": "https://exemplo.com/nova-foto.png"
}
```

Apenas o fundador ou um administrador do grupo pode editar.

## 16. Excluir grupo

DELETE:

```txt
http://localhost:8080/grupos/1
```

Apenas o fundador pode excluir o grupo.

## 17. Entrar em um grupo

POST:

```txt
http://localhost:8080/grupos/1/entrar
```

O usuário logado entra como membro do grupo.

## 18. Sair de um grupo

POST:

```txt
http://localhost:8080/grupos/1/sair
```

O usuário logado sai do grupo.

Observação: o fundador não pode sair do próprio grupo. Ele precisa excluir o grupo ou transferir a liderança futuramente.

## 19. Adicionar membro ao grupo

POST:

```txt
http://localhost:8080/grupos/1/membros/2
```

Nesse exemplo:

* `1` é o ID do grupo;
* `2` é o ID do usuário que será adicionado.

Apenas o fundador ou um administrador pode adicionar membros.

## 20. Remover membro do grupo

DELETE:

```txt
http://localhost:8080/grupos/1/membros/2
```

Nesse exemplo:

* `1` é o ID do grupo;
* `2` é o ID do usuário que será removido.

Apenas o fundador ou um administrador pode remover membros.

O fundador não pode ser removido do grupo.

## 21. Promover administrador

POST:

```txt
http://localhost:8080/grupos/1/administradores/2
```

Nesse exemplo:

* `1` é o ID do grupo;
* `2` é o ID do usuário que será promovido para administrador.

Apenas o fundador pode promover administradores.

O usuário precisa ser membro do grupo antes de virar administrador.

## 22. Remover administrador

DELETE:

```txt
http://localhost:8080/grupos/1/administradores/2
```

Nesse exemplo:

* `1` é o ID do grupo;
* `2` é o ID do usuário que deixará de ser administrador.

Apenas o fundador pode remover administradores.

O fundador não pode ser removido da lista de administradores.

## Regras dos grupos

| Ação                   | Permissão                 |
| ---------------------- | ------------------------- |
| Criar grupo            | Usuário logado            |
| Editar grupo           | Fundador ou administrador |
| Excluir grupo          | Apenas fundador           |
| Entrar no grupo        | Usuário logado            |
| Sair do grupo          | Membro, exceto fundador   |
| Adicionar membro       | Fundador ou administrador |
| Remover membro         | Fundador ou administrador |
| Promover administrador | Apenas fundador           |
| Remover administrador  | Apenas fundador           |

## Modelagem dos grupos

A entidade `Grupo` possui:

* `id`
* `nome`
* `descricao`
* `fotoPerfil`
* `fundador`
* `membros`
* `administradores`

Relacionamentos:

```txt
Grupo N:N Usuario -> membros
Grupo N:N Usuario -> administradores
Grupo N:1 Usuario -> fundador
```

Tabelas geradas para os relacionamentos:

```txt
grupo_membros
- grupo_id
- usuario_id

grupo_administradores
- grupo_id
- usuario_id
```

A tabela principal de grupos possui:

```txt
grupos
- id
- nome
- descricao
- foto_perfil
- fundador_id
```

## Próximos passos recomendados

1. Testar cadastro e login no Postman.
2. Testar a criação de grupos com token JWT.
3. Testar entrada e saída de grupos.
4. Testar permissões de fundador e administrador.
5. Conectar o Angular na rota `/auth/login`.
6. Salvar o token no `localStorage`.
7. Criar um interceptor no Angular para mandar `Authorization: Bearer token`.
8. Criar tela de listagem de grupos.
9. Criar tela de detalhes do grupo.
10. Depois adicionar curtidas, comentários, seguidores, posts dentro de grupos e chat.

## Ideias futuras

* Listar todos os grupos.
* Buscar grupo por ID.
* Criar posts dentro de grupos.
* Feed específico para cada grupo.
* Ranking de distância por grupo.
* Distância total acumulada do grupo.
* Transferência de fundador.
* Convite para entrar em grupo.
* Sistema de grupos privados e públicos.
* Chat dentro do grupo.

```
```
