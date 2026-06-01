# Runly API - MVP inicial

Esse back-end já vem com a base do MVP do Runly:

- Cadastro de usuário
- Login com token JWT
- Senha criptografada com BCrypt
- Rotas protegidas por token
- Perfil do usuário logado
- Cadastro/listagem de corridas
- Estatísticas básicas de corrida
- Criação/listagem de posts no feed

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

## Próximos passos recomendados

1. Testar cadastro e login no Postman.
2. Conectar o Angular na rota `/auth/login`.
3. Salvar o token no `localStorage`.
4. Criar um interceptor no Angular para mandar `Authorization: Bearer token`.
5. Depois adicionar curtidas, comentários, seguidores, grupos e chat.
