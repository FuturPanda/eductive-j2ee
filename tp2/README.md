# TP2 - Persistence avec JPA

## Auteur
Steevy Hoareau

## Description
API REST avec persistence JPA complète incluant :
- 5 entités JPA (Product, Category, Supplier, Order, OrderItem)
- Relations complexes (@ManyToOne, @OneToMany, @ManyToMany)
- Requêtes JPQL optimisées
- Transactions gérées
- Tests complets

## Modèle de Données

[Schéma des tables et relations](./screenshots/erdiagram)

## Lancement

```task run```

## Endpoints

### Products /api/products

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/products | List all products (optimized with JOIN FETCH) |
| GET | /api/products?keyword={keyword} | Search products by name |
| GET | /api/products/{id} | Get product by ID |
| GET | /api/products/slow | List products (N+1 problem demo) |
| GET | /api/products/fast | List products (EntityGraph optimized) |
| GET | /api/products/fast?graph=category | List products (category-only graph) |
| POST | /api/products | Create product |
| POST | /api/products/with-category?categoryName={name} | Create product with category |
| POST | /api/products/test-rollback?name={name}&price={price} | Test transaction rollback |
| POST | /api/products/transfer?fromCategoryId={id}&toCategoryId={id} | Transfer products between categories |
| PUT | /api/products/{id} | Update product |
| PATCH | /api/products/{id}/stock | Adjust product stock |
| DELETE | /api/products/{id} | Delete product |

### Categories /api/categories

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/categories | List all categories |
| GET | /api/categories/{id} | Get category with products |
| POST | /api/categories?name={name}&description={desc} | Create category |
| PUT | /api/categories/{id} | Update category |
| DELETE | /api/categories/{id} | Delete category |

### Orders /api/orders

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/orders | List all orders with items |
| GET | /api/orders/{id} | Get order by ID |
| GET | /api/orders/by-email?email={email} | Get orders by customer email |
| GET | /api/orders/by-status?status={status} | Get orders by status (PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED) |
| POST | /api/orders?customerName={name}&customerEmail={email} | Create order (body: {productId: quantity}) |
| POST | /api/orders/{orderId}/items?productId={id}&quantity={qty} | Add item to order |
| PATCH | /api/orders/{id}/status?status={status} | Update order status |
| PATCH | /api/orders/{orderId}/items/{itemId}?quantity={qty} | Update item quantity |
| DELETE | /api/orders/{orderId}/items/{itemId} | Remove item from order |
| DELETE | /api/orders/{id} | Delete order |

### Stats /api/stats

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/stats/products/count-by-category | Product count by category |
| GET | /api/stats/products/average-price-by-category | Average price by category |
| GET | /api/stats/products/top-expensive?limit={n} | Top N expensive products |
| GET | /api/stats/products/never-ordered | Products never ordered |
| GET | /api/stats/categories/stats | Category stats (DTO projection) |
| GET | /api/stats/categories/with-min-products?minProducts={n} | Categories with min N products |
| GET | /api/stats/orders/total-revenue | Total revenue (delivered orders) |
| GET | /api/stats/orders/count-by-status | Order count by status |
| GET | /api/stats/orders/status-counts | Order status counts (DTO) |
| GET | /api/stats/orders/most-ordered-products?limit={n} | Most ordered products |
| GET | /api/stats/orders/product-stats?limit={n} | Product order stats (DTO) |

## Tests Effectués

- [X] CRUD complet sur toutes les entités
- [X] Relations bidirectionnelles fonctionnelles
- [X] Transactions avec rollback
- [X] Requêtes d'agrégation
- [X] Optimisation N+1
- [X] DTOs pour projections

## Points Clés Appris

### @Transactional 
	Placer @Transactional au niveau service (pas controller), avec readOnly = true pour les requêtes de lecture pour optimiser les performances.
	Chaque opération sur la db est placé dans un BEGIN ... COMMIT. 
	Si il y a un problème quelque part alors, rien n'est commit.
	
	Pourquoi readOnly - true optimise les performances ? 
	Avec transactionnal, si il y a update alors pas besoin de save(). L'état initial est gardé en mémoire, les updates sont des copies, et au commit hibernate fait le delta des 
	deux et save automatiquement. (dirty checking)
	Mais si on sait qu'il n'y aura pas de modifications, alors il n'y aura pas tout ce processus. 
	
### @EntityGraph vs JOIN FETCH 
	@EntityGraph est plus flexible (déclaratif sur le repository), JOIN FETCH est plus explicite dans la requête JPQL.
	Le problème inhérent au Lazy loading, c'est que si on a besoin règulèrement d'avoir accès à la relation alors on va devoir faire des requetes en plus. 
	JOIN FETCH règle le problème au niveau de la requête, puisque bien que lazy loadée, on peut dire que dans le cadre de la requete on va forcer le join de la relation. 
	Mais ce n'est pas réutilisable, et donc on va devoir le rajouter à tous les endroits où on en a besoin. 
	EntityGraph permet de faire créer des modalité de JOIN avec un nom, un peu comme un stratégie, et au niveau de la methode on peut déclarer si on choisi d'utiliser une stratégie particulière. 
	
### Cascade et orphanRemoval 
	Cascade = ALL et orphanRemoval = true permettent de gérer automatiquement le cycle de vie des entités enfants.
	Sans cascade, chaque entité doit être sauvegardée/supprimée manuellement. save(order) ne sauvegarde pas les items.
	Avec cascade = ALL, les opérations sur le parent se propagent aux enfants. save(order) sauvegarde aussi les items, delete(order) supprime aussi les items.
	Mais si on retire un item de la liste (order.getItems().remove(item)), l'item reste en base comme orphelin.
	Avec orphanRemoval = true, quand un enfant est retiré de la collection du parent, il est automatiquement supprimé de la base.
	Utiliser cascade = ALL + orphanRemoval = true quand l'enfant n'a pas de sens sans le parent (OrderItem n'existe pas sans Order). Ne pas l'utiliser si l'enfant peut exister seul (Product peut exister sans Category).
