# FootTracker

API REST Spring Boot qui récupère les données de [football-data.org](https://www.football-data.org), les stocke en base et calcule des statistiques (bilans d'équipes, buteurs, évolution du classement, face-à-face, bilan de journée, équipes favorites).

Le rapport complet (endpoints, valeur ajoutée, compétences) est dans [docs/FootTracker-rapport.pdf](docs/FootTracker-rapport.pdf).

## Prérequis

- Java 21 (Maven est fourni via `mvnw`)
- Une clé gratuite football-data.org : https://www.football-data.org/client/register
- Docker, uniquement pour PostgreSQL

## Lancer le projet

### Option A : base H2 (aucune installation)

```powershell
$env:FOOTBALL_DATA_TOKEN="votre_cle"
.\mvnw spring-boot:run
```

Dans IntelliJ : ajouter `FOOTBALL_DATA_TOKEN=votre_cle` dans *Run > Edit Configurations > Environment variables*.

### Option B : PostgreSQL

```powershell
docker compose up -d
$env:FOOTBALL_DATA_TOKEN="votre_cle"
.\mvnw spring-boot:run "-Dspring-boot.run.profiles=postgres"
```

Dans IntelliJ : renseigner `postgres` dans le champ *Active profiles*.

## Initialisation de la base

Rien à faire à la main :

- les tables sont créées automatiquement par Hibernate au démarrage ;
- les données (Premier League et Ligue 1) sont importées automatiquement **10 secondes après le démarrage** (environ 25 secondes), puis toutes les 30 minutes ;
- la synchronisation peut aussi être lancée avec le bouton « Synchroniser » de l'interface ou `POST /api/sync`.

## URLs

| URL | Contenu |
|---|---|
| http://localhost:8080 | Interface web |
| http://localhost:8080/swagger-ui.html | Documentation Swagger |
| http://localhost:8080/h2-console | Console H2 (`jdbc:h2:file:./data/foottracker`, user `sa`, mot de passe vide) |
| http://localhost:8081 | Adminer pour PostgreSQL (système `PostgreSQL`, serveur `postgres`, user / mot de passe / base : `foottracker`) |

## Tests

```powershell
.\mvnw test
```

Les tests utilisent une base H2 en mémoire : ni clé API ni Docker nécessaires.
