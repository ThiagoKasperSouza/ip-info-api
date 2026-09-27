# IP Info API

API em **Java puro (JDK 17+)**, arquitetura enxuta — sem Spring Boot, sem
framework e **sem nenhuma dependência de runtime**. Usa apenas classes nativas
do JDK (`com.sun.net.httpserver` para o servidor HTTP e `java.net.http` para
as chamadas às APIs externas de IP).

Dado um endereço IP, devolve as informações públicas disponíveis sobre ele
(país, cidade, ISP, coordenadas, fuso horário, etc.) usando a API gratuita
[ip-api.com](https://ip-api.com) — sem necessidade de chave/API key.

## Endpoints

| Método | Rota              | Descrição                                             |
|--------|-------------------|-------------------------------------------------------|
| GET    | `/api/ip/{ip}`     | Informações do IP informado.                          |
| GET    | `/api/ip`          | Informações do IP público de quem faz a requisição.   |

### Exemplo

```bash
# IP específico
curl http://localhost:8080/api/ip/8.8.8.8

# Seu próprio IP público
curl http://localhost:8080/api/ip
```

Resposta (200):

```json
{
  "ip": "8.8.8.8",
  "status": "success",
  "country": "United States",
  "countryCode": "US",
  "region": "VA",
  "regionName": "Virginia",
  "city": "Ashburn",
  "postalCode": "20149",
  "latitude": 39.03,
  "longitude": -77.5,
  "timezone": "America/New_York",
  "isp": "Google LLC",
  "organization": "Google LLC",
  "as": "AS15169 Google LLC"
}
```

- IP inválido / não resolvido → `404` com `{"status":"fail","message":"..."}`.
- Falha na chamada à API externa → `502`.
- Método não permitido → `405`.

## Build e execução

Requer JDK 17+ e (opcional) Maven 3.6+.

```bash
# compilar e testar (sem dependências)
mvn -q package

# executar (usa o .class gerado; porta padrão 8080)
java -cp target/classes com.example.ipinfo.Main
```

Ou rode direto sem Maven (apenas `javac`):

```bash
javac -d out src/main/java/com/example/ipinfo/*.java
java -cp out com.example.ipinfo.Main
```

### Porta

A porta é definida pela variável de ambiente `PORT` (padrão: `8080`):

```bash
PORT=9000 java -cp target/classes com.example.ipinfo.Main
```

## Estrutura

```
src/main/java/com/example/ipinfo/
├── Main.java       # entry point / sobe o servidor
├── IpHandler.java  # roteamento e resposta JSON
├── IpService.java  # chamadas às APIs externas
├── IpInfo.java     # modelo (record) da resposta
└── Json.java       # mini parser/writer de JSON (zero deps)
```

## Observações

- **Sem persistência** de dados — cada requisição consulta a fonte externa.
- o plano gratuito do `ip-api.com` é limitado a ~45 requisições/minuto e uso
  não comercial. Para volumes maiores, troque a URL em `IpService` por uma
  conta paga ou por outra provedora (ex.: `ipwho.is`, `ipinfo.io`).
