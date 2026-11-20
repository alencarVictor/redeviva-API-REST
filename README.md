# Rede Viva API

🔗 **Deploy da API:** https://redeviva-api-rest.onrender.com
🔗 **FrontEnd:** https://rede-viva-front.vercel.app/
🔗 **Vídeo (YouTube):** https://youtu.be/6zO7-8arh8c
🔗 **GitHub:** https://github.com/alencarVictor/redeviva-API-REST

📄 **Documentação (PDF):** *[doc/RedeViva-Documentacao.pdf]*

API RESTful em Java com Spring Boot e banco Oracle, desenvolvida para a Global Solution 2026/2 (FIAP, CitizenTech). A Rede Viva conecta lojas parceiras a famílias cadastradas: a loja publica ofertas de produtos próximos do vencimento com um preço especial da rede, o cliente validado reserva por até 24 horas e retira no balcão, onde a confirmação registra o desconto aplicado. A API gerencia lojas, produtos, ofertas, clientes, reservas, itens de reserva e confirmações de retirada, e é consumida por um front-end em React.

## Tecnologias

- Java 21
- Spring Boot 4.1.2 (Web MVC, Bean Validation e JDBC)
- Oracle Database (JDBC / ojdbc17)
- ModelMapper (conversão entre model e DTO)
- Maven
- Docker (build multi-stage)
- Render (deploy)

## Arquitetura

Organização em camadas, no padrão **Controller → BO → DAO → Model**, com DTOs na borda da API:

| Camada | Responsabilidade |
|---|---|
| `controller` | Endpoints REST, status HTTP e headers |
| `bo` | Regras de negócio |
| `dao` | Acesso ao banco via JDBC |
| `model` | Entidades que espelham as tabelas |
| `dto` | Objetos de transferência e validações (Bean Validation) |
| `exception` | Exceções próprias (`RecursoNaoEncontradoException`, `RegraDeNegocioException`, `BancoDeDadosException`) |
| `handler` | `GlobalExceptionHandler`: respostas de erro em JSON padronizado |
| `config` | CORS |

## Documentação

Toda a documentação do projeto está na pasta [`doc/`](doc/): PDF final com endpoints, diagramas, regras e passo a passo do deploy.

## Estrutura do projeto

```
redeviva/
├── doc/
│   └── RedeViva-Documentacao.pdf
├── sql/
│   └── RedeVidaBase.sql
├── src/
│   ├── main/
│   │   ├── java/br/com/fiap/redeviva/
│   │   │   ├── RedevivaApplication.java
│   │   │   ├── bo/
│   │   │   │   ├── ClienteBO.java
│   │   │   │   ├── ConfirmacaoRetiradaBO.java
│   │   │   │   ├── ItemReservaBO.java
│   │   │   │   ├── LojaBO.java
│   │   │   │   ├── OfertaBO.java
│   │   │   │   ├── ProdutoBO.java
│   │   │   │   └── ReservaBO.java
│   │   │   ├── config/
│   │   │   │   └── CorsFilter.java
│   │   │   ├── controller/
│   │   │   │   ├── ClienteController.java
│   │   │   │   ├── ConfirmacaoRetiradaController.java
│   │   │   │   ├── ItemReservaController.java
│   │   │   │   ├── LojaController.java
│   │   │   │   ├── OfertaController.java
│   │   │   │   ├── ProdutoController.java
│   │   │   │   └── ReservaController.java
│   │   │   ├── dao/
│   │   │   │   ├── ClienteDAO.java
│   │   │   │   ├── ConfirmacaoRetiradaDAO.java
│   │   │   │   ├── ItemReservaDAO.java
│   │   │   │   ├── LojaDAO.java
│   │   │   │   ├── OfertaDAO.java
│   │   │   │   ├── ProdutoDAO.java
│   │   │   │   └── ReservaDAO.java
│   │   │   ├── dto/
│   │   │   │   ├── ClienteDTO.java
│   │   │   │   ├── ConfirmacaoRetiradaDTO.java
│   │   │   │   ├── ItemReservaDTO.java
│   │   │   │   ├── LojaDTO.java
│   │   │   │   ├── OfertaDTO.java
│   │   │   │   ├── ProdutoDTO.java
│   │   │   │   └── ReservaDTO.java
│   │   │   ├── exception/
│   │   │   │   ├── BancoDeDadosException.java
│   │   │   │   ├── RecursoNaoEncontradoException.java
│   │   │   │   └── RegraDeNegocioException.java
│   │   │   ├── handler/
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   └── model/
│   │   │       ├── Cliente.java
│   │   │       ├── ConfirmacaoRetirada.java
│   │   │       ├── ItemReserva.java
│   │   │       ├── Loja.java
│   │   │       ├── Oferta.java
│   │   │       ├── Produto.java
│   │   │       └── Reserva.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/java/br/com/fiap/redeviva/
│       └── RedevivaApplicationTests.java
├── Dockerfile
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## Configuração do banco

As credenciais do Oracle não ficam no código. Configure as variáveis de ambiente antes de iniciar a API:

| Variável | Descrição |
|---|---|
| `DB_URL` | URL JDBC do banco |
| `DB_USER` | Usuário do banco |
| `DB_PASSWORD` | Senha do banco |

O script de criação das tabelas e sequences está em [`sql/RedeVidaBase.sql`](sql/RedeVidaBase.sql). Execute-o no banco antes de iniciar a API.

## Como rodar

**Pré-requisitos:** Java 21, banco Oracle com o script `sql/RedeVidaBase.sql` já executado e as três variáveis de ambiente acima.

**Localmente (Maven):**

```bash
# Linux / macOS
export DB_URL="jdbc:oracle:thin:@//HOST:1521/SERVICO"
export DB_USER="seu_usuario"
export DB_PASSWORD="sua_senha"
./mvnw spring-boot:run
```

```powershell
# Windows (PowerShell)
$env:DB_URL="jdbc:oracle:thin:@//HOST:1521/SERVICO"
$env:DB_USER="seu_usuario"
$env:DB_PASSWORD="sua_senha"
.\mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`. Para conferir, abra `http://localhost:8080/lojas`.

**Com Docker (build multi-stage):**

```bash
docker build -t redeviva-api .
docker run -p 8080:8080 \
  -e DB_URL="jdbc:oracle:thin:@//HOST:1521/SERVICO" \
  -e DB_USER="seu_usuario" \
  -e DB_PASSWORD="sua_senha" \
  redeviva-api
```

**Deploy:** a API está publicada no Render como Web Service com o runtime Docker. As variáveis `DB_URL`, `DB_USER` e `DB_PASSWORD` são configuradas no painel do Render, e a porta vem da variável `PORT`.

## Endpoints

| Recurso | Rotas | Observações |
|---|---|---|
| Loja | `GET/POST /lojas`, `GET/PUT/DELETE /lojas/{cdLoja}` | `GET /lojas/{cdLoja}/produtos` e `GET /lojas/{cdLoja}/ofertas` listam os filhos |
| Produto | `GET /produtos`, `GET/PUT/DELETE /produtos/{cdProduto}` | Criado por `POST /lojas/{cdLoja}/produtos` |
| Oferta | `GET /ofertas`, `GET/PUT/DELETE /ofertas/{cdOferta}` | Criada por `POST /lojas/{cdLoja}/ofertas` |
| Cliente | `GET/POST /clientes`, `GET/PUT/DELETE /clientes/{cdCliente}` | `PUT /clientes/{cdCliente}/validacao/{status}` (VALIDADO ou RECUSADO); `GET/POST /clientes/{cdCliente}/reservas` |
| Reserva | `GET /reservas`, `GET/PUT/DELETE /reservas/{cdReserva}` | Criada por `POST /clientes/{cdCliente}/reservas`; `PUT /reservas/{cdReserva}/retirada` registra a retirada; `GET/POST /reservas/{cdReserva}/itens` |
| ItemReserva | `GET /itens`, `GET/PUT/DELETE /itens/{cdItem}` | Criado por `POST /reservas/{cdReserva}/itens` |
| ConfirmacaoRetirada | `GET /confirmacoes`, `GET/PUT/DELETE /confirmacoes/{cdConfirmacao}` | Nasce na retirada da reserva, não tem POST |

**Status HTTP:** `200` leitura e atualização, `201` criação (com o header `Location`), `204` exclusão sem corpo, `400` dados inválidos, `404` recurso não encontrado, `405` método não permitido e `409` conflito com regra de negócio ou restrição do banco.

## Regras de negócio

- **Oferta:** o preço da rede deve ser menor que o preço do sistema, e o produto precisa existir e ser da mesma loja da oferta.
- **Cliente:** nasce `PENDENTE`; só um cliente pendente pode ser validado ou recusado, e o cadastro validado vale por 1 ano.
- **Reserva:** só cliente validado e com cadastro em dia reserva, no máximo 2 reservas por semana (janela dos últimos 7 dias, reservas canceladas não contam), com expiração em 24 horas. O código de retirada tem 8 caracteres e é gerado pela API.
- **ItemReserva:** só em reserva ativa, com oferta da mesma loja da reserva, ativa e dentro da validade, e com estoque suficiente. O preço do item é o preço da rede da oferta.
- **ConfirmacaoRetirada:** só retira reserva ativa, dentro das 24 horas e com itens. O desconto é a soma, por item, de (preço do sistema − preço unitário) × quantidade, e precisa ser menor que o total do caixa.

## Tratamento de erros

O `GlobalExceptionHandler` devolve sempre um JSON padronizado, sem stack trace nem SQL:

```json
{
  "status": 404,
  "mensagem": "Loja 99999 não encontrada"
}
```

Nos erros de validação (`400`), o JSON traz também o campo `campos`, com o nome de cada campo inválido e a mensagem. Erros de banco são traduzidos pelo código do Oracle: violação de unicidade ou de chave estrangeira vira `409`, dados inválidos ou valor grande demais viram `400`, e qualquer outro erro vira `500` com mensagem genérica (o detalhe fica só no log do servidor).

## Transações

O acesso ao banco usa JDBC puro, com a conexão vinda do `DataSource` do Spring. Duas operações mexem em mais de uma tabela e por isso são feitas em **transação manual** (`setAutoCommit(false)`, `commit` e `rollback`), para que ou tudo é gravado ou nada é:

- **Item de reserva (criar, alterar e apagar):** o estoque da oferta e o item são atualizados juntos. O abatimento usa um `update` condicional (`qt_disponivel - ? >= 0`), que não deixa o estoque ficar negativo mesmo com duas reservas ao mesmo tempo. O status da oferta é recalculado na mesma transação (`ESGOTADA` quando o estoque zera, `ATIVA` quando volta).
- **Confirmação de retirada (criar e apagar):** a confirmação e o status da reserva (`RETIRADA` ou de volta a `ATIVA`) mudam juntos.

## Testes

Os endpoints foram testados no Insomnia, com uma collection por entidade (sete no total), cobrindo os cenários de sucesso e de erro (400, 404, 405 e 409) de cada rota, primeiro localmente e depois na API publicada no Render. As respostas reais estão na documentação em PDF.

## Limitações e próximos passos

- **Reserva não retirada em 24h:** continua `ATIVA` e mantém o estoque abatido. O prazo bloqueia a retirada, mas o status não muda sozinho, e a reserva vencida ainda aceita alteração de itens. Hoje o estoque só volta se os itens forem apagados.
- **Próximo passo:** cancelamento de reserva (`CANCELADA`) e expiração automática (`EXPIRADA`), ambos com devolução do estoque à oferta na mesma transação. O banco já aceita esses dois status.

## Uso de IA generativa

Foi usado o **Claude (Anthropic)** como apoio ao desenvolvimento da API: geração inicial de classes no padrão já usado pelo grupo, revisão de código, roteiros de teste no Insomnia e rascunho da documentação. Todo o código foi revisado, executado e testado pelo autor, que consegue explicar cada parte.

## Equipe

- Ana Paula Cunha Brum, RM: `571359`
- Gabriella Serni Ponzetta, RM: `566296`
- Julio Cesar Iwata de Oliveira Barros, RM: `573723`
- Rafael Santos Dias, RM: `574105`
- Victor Felipe Silva Alencar, RM: `574057`

## Contato

Victor Alencar: [github.com/alencarVictor](https://github.com/alencarVictor)