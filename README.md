# API de Localização de Filmes em São Francisco

## Visão Geral

Esta API foi desenvolvida como parte de um desafio técnico e tem como objetivo mostrar em um mapa onde os filmes foram filmados em São Francisco. A aplicação utiliza tecnologias modernas como Java, Spring Boot, JSON, WebClient e Lombok para fornecer uma interface simples e eficiente para consulta de localizações de filmagens.

A API permite buscar informações sobre filmes filmados em São Francisco, incluindo seus títulos, anos de lançamento, localizações de filmagem e atores principais. Além disso, oferece funcionalidade de autocompletar para facilitar a busca de títulos de filmes.

## Tecnologias Utilizadas

O projeto foi construído utilizando as seguintes tecnologias e frameworks:

- Java 17
- Spring Boot 3.4.5
- Spring WebFlux (WebClient)
- Lombok
- Maven

## Estrutura do Projeto

O projeto segue uma arquitetura em camadas típica de aplicações Spring Boot:

```
src/
├── main/
│   ├── java/
│   │   └── br/
│   │       └── com/
│   │           └── wm/
│   │               └── ubersfmovies/
│   │                   ├── controller/
│   │                   │   └── MovieLocationController.java
│   │                   ├── model/
│   │                   │   └── MovieLocation.java
│   │                   ├── service/
│   │                   │   └── MovieLocationService.java
│   │                   └── UberSfmoviesApplication.java
│   └── resources/
│       └── application.properties

```

## Modelo de Dados

A API utiliza o modelo `MovieLocation` que representa as informações de um filme e suas localizações de filmagem em São Francisco. O modelo inclui os seguintes atributos:

- `title`: Título do filme
- `release_year`: Ano de lançamento
- `locations`: Localizações onde o filme foi filmado
- `actor_1`: Primeiro ator principal
- `actor_2`: Segundo ator principal
- `actor_3`: Terceiro ator principal

## Endpoints da API

A API disponibiliza os seguintes endpoints:

### 1. Buscar Filmes

**Endpoint:** `GET /movies`

**Descrição:** Retorna uma lista de filmes filmados em São Francisco. É possível filtrar os resultados por título.

**Parâmetros:**
- `title` (opcional): Filtra os filmes pelo título

**Exemplo de Requisição:**
```
GET /movies
GET /movies?title=The Matrix
```

**Exemplo de Resposta:**
```json
[
  {
    "title": "The Matrix",
    "release_year": "1999",
    "locations": "Embarcadero, San Francisco",
    "actor_1": "Keanu Reeves",
    "actor_2": "Laurence Fishburne",
    "actor_3": "Carrie-Anne Moss"
  }
]
```

### 2. Autocompletar Títulos de Filmes

**Endpoint:** `GET /movies/autocomplete`

**Descrição:** Fornece sugestões de títulos de filmes com base em um prefixo fornecido.

**Parâmetros:**
- `q`: Prefixo para busca de títulos (obrigatório)

**Exemplo de Requisição:**
```
GET /movies/autocomplete?q=mat
```

**Exemplo de Resposta:**
```json
[
  "The Matrix",
  "Matrix Reloaded",
  "Matrix Revolutions"
]
```

## Implementação

### Controller

O `MovieLocationController` é responsável por expor os endpoints da API e gerenciar as requisições HTTP. Ele utiliza o `MovieLocationService` para processar as solicitações e retornar os resultados.

```java
@RestController
@RequestMapping("/movies")
public class MovieLocationController {

    @Autowired
    private MovieLocationService service;

    @GetMapping
    public List<MovieLocation> getMovies(@RequestParam Optional<String> title) {
        return title.map(service::filterByTitle)
                   .orElseGet(service::getAllMovies);
    }

    @GetMapping("/autocomplete")
    public List<String> autocomplete(@RequestParam("q") String prefix) {
        return service.autocomplete(prefix);
    }
}
```

### Service

O `MovieLocationService` contém a lógica de negócio da aplicação. Ele utiliza o WebClient para fazer requisições a uma API externa que fornece dados sobre filmes filmados em São Francisco.

```java
@Service
public class MovieLocationService {

    private final WebClient webClient;
    private List<MovieLocation> cachedMovies;

    public MovieLocationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://data.sfgov.org/resource/").build();
        this.cachedMovies = new ArrayList<>();
        fetchAndCacheMovies();
    }

    public List<MovieLocation> getAllMovies() {
        return cachedMovies;
    }

    public List<MovieLocation> filterByTitle(String title) {
        return cachedMovies.stream()
                .filter(movie -> movie.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<String> autocomplete(String prefix) {
        return cachedMovies.stream()
                .map(MovieLocation::getTitle)
                .filter(title -> title.toLowerCase().startsWith(prefix.toLowerCase()))
                .distinct()
                .sorted()
                .limit(10)
                .collect(Collectors.toList());
    }

    private void fetchAndCacheMovies() {
        cachedMovies = webClient.get()
                .uri("yitu-d5am.json")
                .retrieve()
                .bodyToFlux(MovieLocation.class)
                .collectList()
                .block();
    }
}
```

## Configuração

A configuração da aplicação é feita através do arquivo `application.properties`:

```properties
spring.application.name=uber-sfmovies
```

## Como Executar

### Pré-requisitos

- Java 17 ou superior
- Maven 3.6 ou superior

### Passos para Execução

1. Clone o repositório:
```bash
git clone https://github.com/williammian/desafio-uber-backend.git
cd desafio-uber-backend
```

2. Compile o projeto:
```bash
mvn clean package
```

3. Execute a aplicação:
```bash
java -jar target/uber-sfmovies-0.0.1-SNAPSHOT.jar
```

Alternativamente, você pode executar diretamente com o Maven:
```bash
mvn spring-boot:run
```

4. A API estará disponível em `http://localhost:8080`


## Exemplos de Uso

### Exemplo 1: Buscar todos os filmes
```bash
curl http://localhost:8080/movies
```

### Exemplo 2: Buscar filmes com título específico
```bash
curl http://localhost:8080/movies?title=Matrix
```

### Exemplo 3: Usar o autocomplete para buscar títulos
```bash
curl http://localhost:8080/movies/autocomplete?q=mat
```

## Considerações sobre Desenvolvimento

A API consome dados de uma fonte externa (data.sfgov.org) e os disponibiliza em um formato mais amigável para aplicações cliente, como interfaces web ou aplicativos móveis que desejam mostrar localizações de filmagens em um mapa.

## Licença

Este projeto está licenciado sob a licença MIT - veja o arquivo LICENSE para mais detalhes.
