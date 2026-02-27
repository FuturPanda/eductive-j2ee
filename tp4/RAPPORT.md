# Rapport de Projet - API Gestion de Produits

## 1. Présentation

Ce projet est une API REST complète développée avec **Spring Boot** permettant la gestion d'un catalogue de produits, de catégories, de fournisseurs et de commandes. L'application suit les principes SOLID et une architecture en couches stricte. Elle expose des endpoints CRUD complets avec validation des données entrantes, gestion centralisée des erreurs, et une documentation interactive via Swagger/OpenAPI.

L'API permet de gérer un inventaire de produits avec des fonctionnalités avancées comme la gestion du stock, le suivi des commandes avec leur statut, et la résolution automatique des catégories/fournisseurs par nom ou ID. Le système d'authentification JWT sécurise l'accès à l'ensemble des endpoints.

## 2. Architecture Technique

### 2.1 Framework Utilisé

**Spring Boot (Spring MVC)** a été choisi pour ce projet en raison de sa maturité, de sa documentation exhaustive et de son écosystème riche. Spring Boot simplifie la configuration via autoconfiguration et annotations, permettant un développement rapide tout en gardant une structure professionnelle. L'alternative Jakarta EE (JAX-RS) aurait été viable mais Spring Boot offre une intégration plus fluide avec Spring Security et Spring Data JPA.

### 2.2 Couches de l'Application

L'application suit une architecture en **4 couches distinctes** :

- **Presentation (Controller)** : Gère les requêtes HTTP, la validation initiale avec `@Valid`, et les réponses. Chaque ressource (Product, Category, Supplier, Order) possède son propre controller.
- **Application (Service)** : Contient la logique métier, les transactions (`@Transactional`), et orchestre les opérations. Les services manipulent les DTOs et délèguent aux repositories.
- **Domain (Model)** : Les entités JPA (`Product`, `Category`, `Supplier`, `Order`, `OrderItem`, `User`, `Role`) représentent le modèle de données avec leurs relations et validations Bean Validation.
- **Infrastructure (Repository)** : Interfaces héritant de `JpaRepository` pour l'accès à la base de données. Utilisation de `@Query` JPQL avec `JOIN FETCH` et `@EntityGraph` pour éviter les problèmes N+1.

### 2.3 Technologies Utilisées

- **Spring Boot 3.x** avec Spring MVC, Spring Data JPA, Spring Security
- **PostgreSQL** comme base de données relationnelle
- **Hibernate** comme ORM (avec Hibernate Validator pour la validation)
- **JWT** pour l'authentification (avec clés RSA)
- **OpenAPI/Swagger** pour la documentation interactive
- **Lombok** pour réduire le boilerplate
- **Maven** comme outil de build

### 2.4 Modèle de Données

```
┌─────────────┐       ┌─────────────┐
│  Category   │       │  Supplier   │
├─────────────┤       ├─────────────┤
│ id (PK)     │       │ id (PK)     │
│ name        │       │ name        │
│ description │       │ contactEmail│
└──────┬──────┘       │ phone       │
       │              └─────────────┘
       │ 1:N
       ▼
┌─────────────┐       ┌─────────────┐
│  Product    │       │    User     │
├─────────────┤       ├─────────────┤
│ id (PK)     │       │ id (PK)     │
│ name        │       │ email       │
│ description │       │ password    │
│ price       │       │ roles []    │
│ stock       │       └─────────────┘
│ sku         │
│ category_id │       ┌─────────────┐
│ supplier_id │       │    Role     │
└──────┬──────┘       ├─────────────┤
       │              │ id (PK)     │
       │ N:M          │ name        │
       ▼              └─────────────┘
┌─────────────┐       ┌─────────────┐
│ OrderItem   │       │    Order    │
├─────────────┤       ├─────────────┤
│ id (PK)     │       │ id (PK)     │
│ quantity    │       │ customerName│
│ price       │       │ customerEmail│
│ product_id  │       │ status      │
│ order_id    │       │ total       │
└─────────────┘       │ createdAt   │
                     └─────────────┘
```

## 3. Fonctionnalités Implémentées

### 3.1 CRUD

| Ressource | Endpoints |
|-----------|-----------|
| **Products** | `GET /products`, `GET /products/{id}`, `POST /products`, `PUT /products/{id}`, `PATCH /products/{id}/stock`, `DELETE /products/{id}` |
| **Categories** | `GET /categories`, `GET /categories/{id}`, `POST /categories`, `PUT /categories/{id}`, `DELETE /categories/{id}` |
| **Suppliers** | `GET /suppliers`, `GET /suppliers/{id}`, `GET /suppliers/count`, `POST /suppliers`, `PUT /suppliers/{id}`, `DELETE /suppliers/{id}` |
| **Orders** | `GET /orders`, `GET /orders/{id}`, `POST /orders`, `PATCH /orders/{id}/status`, `POST /orders/{id}/items`, `PATCH /orders/{id}/items/{itemId}`, `DELETE /orders/{id}/items/{itemId}`, `DELETE /orders/{id}` |

**Exemple de réponse 201 Created :**
```json
{
  "id": 42,
  "name": "Mon Produit",
  "price": 29.99,
  "stock": 100,
  "category": { "id": 1, "name": "Electronics" },
  "createdAt": "2026-02-26T10:30:00"
}
```

### 3.2 Validation

**Bean Validation sur les entités :**
- `@NotBlank`, `@Size`, `@NotNull`, `@Min`, `@DecimalMin`, `@Digits`, `@Email`
- Messages d'erreur en français

**Contraintes personnalisées :**
- `@ValidSKU` : Validation du format SKU (3 lettres + 3 chiffres, ex: `ABC123`)
- `@ValidPrice` : Validation de prix (doit être un multiple de 0.01)
- `@ValidDateRange` : Validation de plage de dates

**Validation des DTOs :**
Tous les endpoints POST/PUT/PATCH utilisent des DTOs avec `@Valid` pour déclencher la validation.

### 3.3 Gestion des Erreurs

`GlobalExceptionHandler` avec `@RestControllerAdvice` capture toutes les exceptions et retourne des réponses JSON structurées :

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Erreurs de validation",
  "path": "/api/v1/products",
  "fieldErrors": [
    { "field": "name", "message": "Le nom du produit est obligatoire" }
  ]
```

**Exceptions gérées :**
- `ProductNotFoundException` → 404
- `CategoryNotFoundException` → 404
- `OrderNotFoundException` → 404
- `DuplicateSkuException` → 409 Conflict
- `InsufficientStockException` → 422
- `CategoryHasProductsException` → 409
- `ConstraintViolationException` → 400
- `ObjectOptimisticLockingFailureException` → 409 (version pour concurrence)

### 3.4 Fonctionnalités Avancées

- **Pagination** : `GET /products?page=0&size=10` avec `Pageable`
- **Documentation OpenAPI** : Accessible via `/api/v1/swagger-ui.html`
- **Authentification JWT** : Login/register avec tokens RSA
- **Relations optimisées** : `@EntityGraph` et `JOIN FETCH` pour éviter N+1
- **Transfert de produits** : `POST /products/transfer?fromCategoryId=X&toCategoryId=Y`
- **Statistiques** : Endpoint de comptage et moyennes par catégorie

## 4. Difficultés Rencontrées et Solutions

### Difficulté 1 : Validation des entités vs Validation des DTOs

**Problème** : Les contraintes Bean Validation (`@NotBlank`, etc.) sur les entités JPA n'étaient pas détectées correctement par le `GlobalExceptionHandler`. Hibernate lève `ConstraintViolationException` au moment du persist, mais celle-ci était captée comme erreur générique et retournait un 500.

**Solution** : Ajout de multiples handlers dans `GlobalExceptionHandler` pour capturer `ConstraintViolationException`, `TransactionSystemException`, `RollbackException`, et `PersistenceException` en descendant dans la chaîne des causes pour extraire les violations de validation.

### Difficulté 2 : Conversion Category en JSON lors des mises à jour

**Problème** : L'endpoint PUT `/products/{id}` recevait un JSON avec `"category": "Toys"` (string), mais Spring essayait de désérialiser directement en objet `Category`, échouant avec une erreur de parsing.

**Solution** : Création de DTOs (`CreateProductRequest`, `UpdateProductRequest`) qui utilisent `String category` au lieu de `Category`. Le service appelle ensuite `resolveCategory()` qui accepte soit un ID numérique soit un nom, et crée la catégorie si elle n'existe pas.

### Difficulté 3 : Gestion des RuntimeException génériques

**Problème** : Certaines méthodes de service lançaient `RuntimeException` avec message "Order not found", qui étaient captées par le handler générique et retournaient 500 au lieu de 404.

**Solution** : Création d'exceptions personnalisées (`OrderNotFoundException`, `ProductNotFoundException`, `CategoryNotFoundException`) avec handlers dédiés retournant 404.

## 5. Points d'Amélioration

- **Pagination manquante** : Ajouter `Pageable` sur les endpoints `GET /categories`, `GET /suppliers`, `GET /orders`
- **Système de cache** : Implémenter le caching HTTP avec ETags ou Spring Cache (Cache-Control)
- **Rôles et permissions** : Système complet avec ROLE_ADMIN vs ROLE_USER
- **Refresh Tokens** : JWT avec access token et refresh token
- **DTOs de réponse spécifiques** : Créer des DTOs de réponse séparés des entités (pour masquer certains champs comme password)
- **Tests unitaires** : Couverture de code avec JUnit et MockMvc
- **Rate Limiting** : Limiter le nombre de requêtes par utilisateur
- **API Versioning** : Support explicite de versions dans les URLs ou headers

## 6. Conclusion

Ce projet m'a permis de maîtriser les fondamentaux d'une API REST avec Spring Boot. Le point clé appris : l'utilisation du status **201 Created** avec l'en-tête `Location` pointant vers l'URL de la ressource créée. Le système de caching avec ETags (non implémenté mais étudié) est un concept qui m'a particulièrement marqué par son elegance.

Le plus gros défi a été la gestion des exceptions et la distinction entre validation au niveau controller (DTOs) et validation au niveau base (entités). Comprendre le flux de `ConstraintViolationException` à travers Hibernate et Spring était crucial.

L'écosystème Java reste complexe avec beaucoup d'abstractions (JPA, Hibernate, Spring Data). Les transactions, les relations N+1, et la sérialisation JSON nécessitent une attention particulière. Avec plus de temps, je concentrerais mes efforts sur les tests unitaires et le système de cache qui sont des piliers d'une application production-ready.
