package com.formations.products.resource;

import com.formations.products.model.Product;
import com.formations.products.resource.dto.ErrorMessage;
import com.formations.products.resource.dto.StockAdjustment;
import com.formations.products.service.ProductService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

	@Inject
	private ProductService productService;

	@Context
	private UriInfo uriInfo;

	@GET
	public List<Product> getAllProducts(
		@QueryParam("category") String category
	) {
		if (category != null && !category.isBlank()) {
			return productService.getProductsByCategory(category);
		}
		return productService.getAllProducts();
	}

	@GET
	@Path("/{id}")
	public Response getProduct(@PathParam("id") String id) {
		return productService
			.getProduct(id)
			.map(product -> Response.ok(product).build())
			.orElse(
				Response.status(Response.Status.NOT_FOUND)
					.entity(new ErrorMessage("Produit non trouvé"))
					.build()
			);
	}

	@POST
	public Response createProduct(Product product) {
		try {
			Product created = productService.createProduct(product);
			URI location = uriInfo
				.getAbsolutePathBuilder()
				.path(created.getId())
				.build();
			return Response.created(location).entity(created).build();
		} catch (IllegalArgumentException e) {
			return Response.status(Response.Status.BAD_REQUEST)
				.entity(new ErrorMessage(e.getMessage()))
				.build();
		}
	}

	@PUT
	@Path("/{id}")
	public Response updateProduct(@PathParam("id") String id, Product product) {
		try {
			Product updated = productService.updateProduct(id, product);
			return Response.ok(updated).build();
		} catch (IllegalArgumentException e) {
			return Response.status(Response.Status.NOT_FOUND)
				.entity(new ErrorMessage("Produit non trouvé"))
				.build();
		}
	}

	@PATCH
	@Path("/{id}/stock")
	public Response adjustStock(
		@PathParam("id") String id,
		StockAdjustment adjustment
	) {
		try {
			productService.updateStock(id, adjustment.quantity());
			return productService
				.getProduct(id)
				.map(product -> Response.ok(product).build())
				.orElse(
					Response.status(Response.Status.NOT_FOUND)
						.entity(new ErrorMessage("Produit non trouvé"))
						.build()
				);
		} catch (IllegalArgumentException e) {
			return Response.status(Response.Status.NOT_FOUND)
				.entity(new ErrorMessage("Produit non trouvé"))
				.build();
		}
	}

	@DELETE
	@Path("/{id}")
	public Response deleteProduct(@PathParam("id") String id) {
		try {
			productService.deleteProduct(id);
			return Response.noContent().build();
		} catch (IllegalArgumentException e) {
			return Response.status(Response.Status.NOT_FOUND)
				.entity(new ErrorMessage("Produit non trouvé"))
				.build();
		}
	}
}
