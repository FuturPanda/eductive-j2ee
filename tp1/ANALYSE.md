# Analyse 

## Vue d'ensemble

| Critère | **tp1-jakarta** | **tp1-springboot** |
|---------|-----------------|-------------------|
| **Framework** | Jakarta EE 11.0.0-RC1 | Spring Boot 4.0.3 |
| **Java** | 21 | 17 |
| **Packaging** | WAR (WildFly) | JAR (embedded Tomcat) |
| **Persistance** | In-Memory (ConcurrentHashMap) | MongoDB + fallback In-Memory |
| **Port** | 8080 (WildFly default) | 8081 |

## Endpoints REST

**Identiques sur les deux projets :**

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/products` | Liste tous les produits (filtre `?category=`) |
| `GET` | `/api/products/{id}` | Récupère un produit |
| `POST` | `/api/products` | Crée un produit |
| `PUT` | `/api/products/{id}` | Met à jour un produit |
| `PATCH` | `/api/products/{id}/stock` | Ajuste le stock |
| `DELETE` | `/api/products/{id}` | Supprime un produit |

## Architecture 
La terminologie change un peu mais globallement, les deux app ont la même structure en couche. Cette structure a été inventée bien avant Jakarta par Dijkstra : c'est le THE System, et qui a commencé a être implémenté dans les années 70. On peut dire que ce sont Jakarta et Springboot qui ont mis en place les abstractions pour permettre de facilement suivre l'architecture en couche. 

### Jakarta EE
```
tp1-jakarta/
├── src/main/java/com/formations/products/
│   ├── ApplicationConfig.java          # Config JAX-RS → /api
│   ├── model/Product.java              # Entité produit
│   ├── interfaces/IProductRepository.java
│   ├── repository/InMemoryProductRepository.java
│   ├── service/ProductService.java
│   └── resource/
│       ├── ProductResource.java        # Endpoints REST
│       └── dto/
│           ├── ErrorMessage.java
│           └── StockAdjustment.java
└── pom.xml
```

### Spring Boot
```
tp1-springboot/
├── src/main/java/com/formations/spring_products_api/
│   ├── SpringProductsApiApplication.java   # Entry point
│   ├── model/Product.java                  # Entité MongoDB
│   ├── repository/
│   │   ├── IProductRepository.java
│   │   ├── InMemoryProductRepository.java  # Profile: default
│   │   ├── SpringMongoProductRepository.java
│   │   └── MongoProductRepositoryAdapter.java  # Profile: prod
│   ├── service/ProductService.java
│   ├── controller/ProductController.java
│   ├── dto/
│   │   ├── ErrorMessage.java
│   │   └── StockAdjustment.java
│   └── exception/
│       ├── GlobalExceptionHandler.java     # @RestControllerAdvice
│       ├── ProductNotFoundException.java
│       └── InvalidProductException.java
├── src/main/resources/application.properties
└── pom.xml
```

## Injection de dépendances

On injecte directement l'interface dans le service pour le découpler de l'implémentation. Dans spring, le framework gère automatiquement l'injection de l'implémentation, donc si plusieurs implémentations vivent ensemble il faut :
- soit avec le décorateur @Primary dire quelle implémentation favoriser
- soit attribuer chaque implémentation a un profile spring. (ici on a dev ou prod)

| Jakarta EE | Spring Boot |
|-----------|-------------|
| `@Inject` (CDI) | Constructor Injection |
| `@ApplicationScoped` | `@Service`, `@Repository` |
| Field injection | Constructor injection (meilleur) |

```java
// Jakarta EE
@Inject
private ProductService productService;

// Spring Boot
private final ProductService productService;
public ProductController(ProductService productService) {
    this.productService = productService;
}
```

## Solid 
Voici une reformulation plus structurée :

---

## 3. SOLID

### SRP (Single Responsibility Principle)

**Principe** : Une classe ne doit avoir qu'une seule raison de changer.

| Classe | Responsabilité unique | Raison de changer |
|--------|----------------------|-------------------|
| `Product` | Représenter les données d'un produit | Modification du modèle de données |
| `ProductService` | Logique métier (validation, orchestration) | Évolution des règles métier |
| `ProductController` / `Resource` | Exposition HTTP, mapping requêtes/réponses | Changement du contrat API |
| `IProductRepository` | Contrat de persistance | Ajout/suppression d'opérations CRUD |
| `InMemoryProductRepository` | Implémentation stockage mémoire | Changement technique de stockage |
| `GlobalExceptionHandler` (Spring) | Transformation exceptions → réponses HTTP | Modification du format d'erreurs |

> ⚠️ : Imbriquer des classes dans d'autres classes (inner classes) viole souvent SRP en mélangeant les responsabilités.

---

### OCP (Open/Closed Principle)

**Principe** : Ouvert à l'extension, fermé à la modification.

**Exemple** : L'ajout d'un nouveau repository (ex: `PostgresProductRepository`) ne nécessite aucune modification du code existant — il suffit d'implémenter l'interface `IProductRepository`.

---

### LSP (Liskov Substitution Principle)

**Principe** : Toute classe dérivée doit pouvoir se substituer à sa classe parente sans altérer le comportement attendu.

**Application** : Le contrat défini par l'interface `IProductRepository`.

> ⚠️ :  Le respect de LSP dépend de l'implémentation, pas seulement du contrat. Les interfaces ne déclarent pas les exceptions levées — si une implémentation lance une exception non prévue, elle casse la substituabilité. Le contrat implicite (préconditions, postconditions, invariants) doit être respecté par toutes les implémentations.

---

### ISP (Interface Segregation Principle)

**Principe** : Préférer plusieurs interfaces spécifiques plutôt qu'une interface générale.

**Constat** : L'interface `Repository` actuelle pourrait être trop large.

**Amélioration** : Découper en interfaces à responsabilité unique :

```java
public interface Readable<T, ID> {
    Optional<T> findById(ID id);
    List<T> findAll();
}

public interface Writable<T, ID> {
    T save(T entity);
    void deleteById(ID id);
}

public interface Countable {
    long count();
}

// Composition selon les besoins
public interface ProductRepository 
    extends Readable<Product, Long>, Writable<Product, Long>, Countable {
}
```

---

### DIP (Dependency Inversion Principle)

**Principe** : Les modules de haut niveau ne doivent pas dépendre des modules de bas niveau. Les deux doivent dépendre d'abstractions.

**Dans votre architecture** :

```
┌─────────────────────────────────────────────────────────┐
│                    HAUT NIVEAU                          │
│  ProductService  ──────►  IProductRepository (interface)│
│        │                         ▲                      │
│        │                         │                      │
│   dépend de                implémente                   │
│  l'abstraction                   │                      │
│                                  │                      │
│                    ┌─────────────┴─────────────┐        │
│                    │        BAS NIVEAU         │        │
│            InMemoryRepo    JpaRepo    MongoRepo         │
└─────────────────────────────────────────────────────────┘
```

- ✅ `ProductService` dépend de `IProductRepository` (abstraction), pas d'une implémentation concrète
- ✅ L'injection de dépendances (Spring/Jakarta CDI) permet d'inverser le contrôle
- ✅ Les tests peuvent injecter un mock ou un repository in-memory

## Tests: 

## Persistance
> Évolution : Si demain vous devez ajouter JPA, quelles classes devrez-vous : Modifier ? Créer ? Ne pas toucher ?

Si demain je devais ajouter JPA, je m'attendrais a créer une nouvelle implémentation du Repository, et ne toucher rien d'autre. 
Edit : Au final, on doit bien changer le modèle (ici annotation propre a mongo)

| Jakarta EE | Spring Boot |
|-----------|-------------|
| In-Memory uniquement | MongoDB (prod) + In-Memory (dev) |
| POJO simple | `@Document` + `@Id` annotations |
| Pas de switch d'environnement | Profiles Spring (`@Profile`) |

```java
// Spring Boot - switch automatique via profiles
@Profile("prod")
public class MongoProductRepositoryAdapter implements IProductRepository

// application.properties
spring.profiles.active=prod  // → MongoDB
// Ou sans profile → InMemoryRepository
```
