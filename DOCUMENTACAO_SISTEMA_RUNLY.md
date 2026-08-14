# 🏃‍♂️ RUNLY - Documentação Oficial do Sistema

Este documento descreve a arquitetura, regras de negócio, fluxos de integração e guia de uso das funcionalidades implementadas no **Runly** (Back-end Spring Boot + Front-end Angular Mobile-First).

---

## 📑 Sumário
1. [Visão Geral da Arquitetura](#1-visão-geral-da-arquitetura)
2. [Hospedagem de Imagens na Nuvem (Cloudinary)](#2-hospedagem-de-imagens-na-nuvem-cloudinary)
3. [Módulo de GPS e Gravação de Corridas](#3-módulo-de-gps-e-gravação-de-corridas)
4. [Módulo de Grupos e Gestão de Membros](#4-módulo-de-grupos-e-gestão-de-membros)
5. [Módulo de Chat do Grupo (Conversação em Tempo Real)](#5-módulo-de-chat-do-grupo-conversação-em-tempo-real)
6. [Matriz de Permissões e Regras de Negócio (RBAC)](#6-matriz-de-permissões-e-regras-de-negócio-rbac)
7. [Referência Completa de Endpoints da API REST](#7-referência-completa-de-endpoints-da-api-rest)
8. [Guia de Execução Local](#8-guia-de-execução-local)

---

## 1. Visão Geral da Arquitetura

O ecossistema do **Runly** é composto por:
* **Back-end**: Java 21, Spring Boot 3, Spring Security, JWT (Json Web Token), Hibernate/JPA, BCrypt e PostgreSQL.
* **Front-end**: Angular 19 (Standalone Components, SSR habilitado, RxJS, SCSS Mobile-First e Angular Material).
* **Armazenamento de Mídia**: Cloudinary SDK (Hospedagem em nuvem com CDN global e otimização automática para mobile).
* **Comunicação**: API REST protegida por Bearer Token via HTTP Interceptor.

---

## 2. Hospedagem de Imagens na Nuvem (Cloudinary)

O sistema conta com upload direto de mídia na nuvem para:
* **Fotos de Capa/Perfil de Grupos**: Armazenadas na pasta `runly/grupos/`.
* **Fotos de Perfil de Usuários**: Armazenadas na pasta `runly/usuarios/`.
* **Fotos e Mídias de Postagens (Feed)**: Armazenadas na pasta `runly/posts/`.

### Como Funciona:
1. O Front-end (Angular) envia a imagem selecionada via `FormData` (`multipart/form-data`).
2. O serviço [`UploadService`](file:///C:/Users/sandr/OneDrive/Documentos/Progrma%C3%A7%C3%A3o/Java/Spring-Boot/runly/src/main/java/br/com/runly/service/UploadService.java) intercepta o arquivo, valida o tipo MIME (`image/*`) e o tamanho (máx 10MB).
3. O SDK oficial do Cloudinary realiza o upload assíncrono e retorna uma URL HTTPS pública, rápida e com CDN (ex: `https://res.cloudinary.com/afigevng/image/upload/v12345/runly/grupos/grupo-1.jpg`).
4. Essa URL definitiva é gravada na entidade do banco de dados.

---

## 3. Módulo de GPS e Gravação de Corridas

### 3.1 Como funciona o rastreamento sem custos
O rastreamento de corrida utiliza a **HTML5 Geolocation API** nativa do navegador/dispositivo móvel (`navigator.geolocation.watchPosition` com `enableHighAccuracy: true`).
* **Custo Zero**: Não depende de serviços externos pagos (como Google Maps ou Mapbox).
* **Sensor de Satélite**: Conecta diretamente ao chip de GPS do smartphone do usuário.

### 3.2 Cálculo de Distância (Fórmula de Haversine)
A cada novo ponto geográfico capturado pelo sensor, o sistema calcula a distância real percorrida na superfície da Terra utilizando a **Fórmula de Haversine**:

$$\Delta\sigma = 2 \cdot \arcsin\left(\sqrt{\sin^2\left(\frac{\Delta\text{lat}}{2}\right) + \cos(\text{lat}_1)\cos(\text{lat}_2)\sin^2\left(\frac{\Delta\text{lon}}{2}\right)}\right)$$

$$\text{Distância (km)} = R \cdot \Delta\sigma \quad (\text{onde } R = 6371\text{ km})$$

### 3.3 Filtros de Precisão e Estabilidade
* **Filtro de Ruído**: Pontos com precisão de GPS superior a 30 metros são descartados para evitar "teleportes" ou saltos irreais de distância.
* **Filtro Estático**: Deslocamentos inferiores a 3 metros entre leituras são desconsiderados para não acumular distância quando o corredor estiver parado no sinal.

### 3.4 Armazenamento da Rota (`rotaJson`)
Ao finalizar a corrida, o array de coordenadas geográficas é serializado em JSON e salvo no banco de dados na coluna `rotaJson`:
```json
[
  { "lat": -21.789123, "lng": -48.175432, "timestamp": 1723588900123 },
  { "lat": -21.789456, "lng": -48.175890, "timestamp": 1723588905456 }
]
```

### 3.5 Regras de Negócio de Corrida (Java)
1. **Cálculo do Pace**: O servidor calcula o ritmo médio oficial:
   $$\text{Pace (min/km)} = \frac{\text{tempoSegundos} / 60.0}{\text{distanciaKm}}$$
2. **Tempo Mínimo**: Treinos com menos de 10 segundos são bloqueados.
3. **Alimentação Automática de Estatísticas**: Cada corrida gravada atualiza imediatamente o histórico do usuário, o total de quilômetros acumulados, o pace geral e os gráficos anuais de performance.

---

## 4. Módulo de Grupos e Gestão de Membros

### 4.1 Papéis no Grupo
* **Fundador**: Criador do grupo. Possui autoridade máxima (promover admins, rebaixar admins, adicionar/remover membros e excluir o grupo).
* **Administrador**: Membro com privilégios de moderação (pode adicionar novos membros, remover membros comuns e editar informações do grupo).
* **Membro**: Usuário participante que pode interagir no chat e ver a lista de corredores.
* **Visitante**: Usuário autenticado que ainda não ingressou no grupo.

### 4.2 Inserção e Busca de Membros
* O sistema conta com o endpoint `GET /usuarios/buscar?q={termo}`.
* No Front-end, o fundador ou administrador clica em **"Adicionar Membro"**, pesquisa corredores por nome ou e-mail e adiciona o usuário com 1 clique.

---

## 5. Módulo de Chat do Grupo (Conversação em Tempo Real)

### 5.1 Arquitetura do Chat
* **Entidade**: `MensagemGrupo` vinculada a `Grupo` e `Usuario`.
* **Persistência**: As mensagens são salvas na tabela `grupo_mensagens` com data/hora UTC e autor.
* **Sincronização**: O Front-end utiliza polling periódico otimizado a cada 3,5 segundos enquanto a aba de chat está ativa, garantindo atualização contínua sem sobrecarga no servidor.
* **Interface**: Balões de mensagens dinâmicos com distinção entre mensagens próprias (verde/azul à direita) e de terceiros (à esquerda com foto do autor).

---

## 6. Matriz de Permissões e Regras de Negócio (RBAC)

| Ação | Visitante | Membro Comum | Administrador | Fundador |
| :--- | :---: | :---: | :---: | :---: |
| **Visualizar Informações do Grupo** | ✅ | ✅ | ✅ | ✅ |
| **Entrar no Grupo** | ✅ | ❌ (já é) | ❌ (já é) | ❌ (é o dono) |
| **Sair do Grupo** | ❌ | ✅ | ✅ | ❌ (Regra 1) |
| **Visualizar Chat do Grupo** | ❌ (Regra 2) | ✅ | ✅ | ✅ |
| **Enviar Mensagens no Chat** | ❌ (Regra 2) | ✅ | ✅ | ✅ |
| **Pesquisar e Adicionar Membros** | ❌ | ❌ | ✅ | ✅ |
| **Remover Membro Comum** | ❌ | ❌ | ✅ | ✅ |
| **Remover Administrador** | ❌ | ❌ | ❌ | ✅ (Regra 3) |
| **Promover Membro a Administrador** | ❌ | ❌ | ❌ | ✅ (Regra 4) |
| **Editar Dados e Foto do Grupo** | ❌ | ❌ | ✅ | ✅ |
| **Excluir o Grupo Permanentemente** | ❌ | ❌ | ❌ | ✅ (Apenas Fundador) |

### Regras de Negócio Críticas:
* **Regra 1 (Saída do Fundador)**: O fundador **não pode sair** do próprio grupo. Ele precisa excluir o grupo ou transferir a titularidade.
* **Regra 2 (Privacidade do Chat)**: Usuários que não pertencem ao grupo recebem erro `403 / Forbidden` ao tentar ler ou enviar mensagens.
* **Regra 3 (Imunidade do Fundador)**: O fundador **nunca pode ser removido** do grupo ou rebaixado de administrador por ninguém.
* **Regra 4 (Pré-requisito de Admin)**: Um usuário precisa obrigatoriamente ser membro ativo do grupo antes de poder ser promovido a administrador.

---

## 6. Referência Completa de Endpoints da API REST

### 🔐 Autenticação & Usuários
* `POST /auth/register`: Cadastro de novo usuário.
* `POST /auth/login`: Autenticação e geração de token JWT.
* `GET /usuarios/me`: Obter perfil do usuário logado.
* `PATCH /usuarios/me`: Atualizar nome, biografia e foto de perfil.
* `GET /usuarios/buscar?q={termo}`: Pesquisar usuários por nome ou e-mail.

### 🏃 Corridas & Performance
* `POST /corridas`: Gravar novo treino com distância, tempo, pace calculado e rota GPS.
* `GET /corridas/minhas`: Histórico de corridas do usuário ordenado por data decrescente.
* `GET /corridas/estatisticas`: Resumo de atividades, km totais e ritmo médio geral.
* `GET /corridas/grafico/pace/ultimo-ano`: Dados mensais para gráfico de evolução de ritmo.
* `GET /corridas/grafico/distancia/ultimo-ano`: Dados mensais para gráfico de distância.

### 👥 Grupos & Moderação
* `POST /grupos`: Criar novo grupo (criador vira fundador, membro e admin).
* `GET /grupos`: Listar todos os grupos disponíveis.
* `GET /grupos/{id}`: Obter detalhes completos do grupo (membros, admins e permissões).
* `PUT /grupos/{id}`: Editar nome e descrição do grupo (Admin/Fundador).
* `POST /grupos/{id}/foto`: Upload de imagem de perfil do grupo.
* `DELETE /grupos/{id}`: Excluir grupo permanentemente (Apenas Fundador).
* `POST /grupos/{id}/entrar`: Entrar no grupo.
* `POST /grupos/{id}/sair`: Sair do grupo (Membro, exceto fundador).
* `POST /grupos/{id}/membros/{usuarioId}`: Adicionar membro (Admin/Fundador).
* `DELETE /grupos/{id}/membros/{usuarioId}`: Remover membro (Admin/Fundador).
* `POST /grupos/{id}/administradores/{usuarioId}`: Promover a administrador (Apenas Fundador).
* `DELETE /grupos/{id}/administradores/{usuarioId}`: Remover privilégio de admin (Apenas Fundador).

### 💬 Chat do Grupo
* `GET /grupos/{id}/mensagens`: Listar histórico de mensagens do grupo (Apenas membros).
* `POST /grupos/{id}/mensagens`: Enviar mensagem para o grupo (Apenas membros).
  * **Body**: `{ "texto": "Treino marcado para amanhã às 06:30!" }`

---

## 7. Guia de Execução Local

### 1. Iniciar o Banco de Dados (PostgreSQL)
Certifique-se de que o banco `runly` existe e as credenciais em `application.properties` estão corretas:
```sql
CREATE DATABASE runly;
```

### 2. Iniciar o Back-end (Spring Boot)
Na pasta `Java/Spring-Boot/runly`:
```powershell
.\mvnw.cmd spring-boot:run
```
O servidor iniciará em `http://localhost:8080`.

### 3. Iniciar o Front-end (Angular)
Na pasta `Runly`:
```powershell
npm start
```
Acesse no navegador ou emulador mobile: `http://localhost:4200`.
