package com.formations.spring_products_api.validation;

import com.formations.spring_products_api.model.Order;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CustomValidationTest {

	private static Validator validator;

	@BeforeAll
	static void setUpValidator() {
		try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
			validator = factory.getValidator();
		}
	}

	@Nested
	@DisplayName("Tests @ValidSKU")
	class ValidSKUTests {

		@Test
		@DisplayName("SKU valide : ABC123")
		void validSKU_ABC123() {
			Set<ConstraintViolation<SKUTestWrapper>> violations = 
				validator.validate(new SKUTestWrapper("ABC123"));
			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("SKU valide : XYZ789")
		void validSKU_XYZ789() {
			Set<ConstraintViolation<SKUTestWrapper>> violations = 
				validator.validate(new SKUTestWrapper("XYZ789"));
			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("SKU null est valide (utiliser @NotNull si nécessaire)")
		void validSKU_null() {
			Set<ConstraintViolation<SKUTestWrapper>> violations = 
				validator.validate(new SKUTestWrapper(null));
			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("SKU invalide : AB123 (seulement 2 lettres)")
		void invalidSKU_twoLetters() {
			Set<ConstraintViolation<SKUTestWrapper>> violations = 
				validator.validate(new SKUTestWrapper("AB123"));
			assertThat(violations).hasSize(1);
			assertThat(violations.iterator().next().getMessage())
				.contains("SKU");
		}

		@Test
		@DisplayName("SKU invalide : ABCD123 (4 lettres)")
		void invalidSKU_fourLetters() {
			Set<ConstraintViolation<SKUTestWrapper>> violations = 
				validator.validate(new SKUTestWrapper("ABCD123"));
			assertThat(violations).hasSize(1);
		}

		@Test
		@DisplayName("SKU invalide : abc123 (lettres minuscules)")
		void invalidSKU_lowercase() {
			Set<ConstraintViolation<SKUTestWrapper>> violations = 
				validator.validate(new SKUTestWrapper("abc123"));
			assertThat(violations).hasSize(1);
		}

		// Wrapper class pour tester @ValidSKU
		static class SKUTestWrapper {
			@ValidSKU
			private String sku;

			SKUTestWrapper(String sku) {
				this.sku = sku;
			}
		}
	}

	@Nested
	@DisplayName("Tests @ValidPrice")
	class ValidPriceTests {

		@Test
		@DisplayName("Prix valide : 99.99")
		void validPrice_twoDecimals() {
			Set<ConstraintViolation<PriceTestWrapper>> violations = 
				validator.validate(new PriceTestWrapper(new BigDecimal("99.99")));
			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("Prix valide : 100 (pas de décimales)")
		void validPrice_noDecimals() {
			Set<ConstraintViolation<PriceTestWrapper>> violations = 
				validator.validate(new PriceTestWrapper(new BigDecimal("100")));
			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("Prix valide : 10.5 (une décimale)")
		void validPrice_oneDecimal() {
			Set<ConstraintViolation<PriceTestWrapper>> violations = 
				validator.validate(new PriceTestWrapper(new BigDecimal("10.5")));
			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("Prix null est valide (utiliser @NotNull si nécessaire)")
		void validPrice_null() {
			Set<ConstraintViolation<PriceTestWrapper>> violations = 
				validator.validate(new PriceTestWrapper(null));
			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("Prix invalide : 99.999 (3 décimales)")
		void invalidPrice_threeDecimals() {
			Set<ConstraintViolation<PriceTestWrapper>> violations = 
				validator.validate(new PriceTestWrapper(new BigDecimal("99.999")));
			assertThat(violations).hasSize(1);
			assertThat(violations.iterator().next().getMessage())
				.contains("décimales");
		}

		@Test
		@DisplayName("Prix invalide : 10.001 (fraction de centime)")
		void invalidPrice_fractionOfCent() {
			Set<ConstraintViolation<PriceTestWrapper>> violations = 
				validator.validate(new PriceTestWrapper(new BigDecimal("10.001")));
			assertThat(violations).hasSize(1);
		}

		// Wrapper class pour tester @ValidPrice
		static class PriceTestWrapper {
			@ValidPrice
			private BigDecimal price;

			PriceTestWrapper(BigDecimal price) {
				this.price = price;
			}
		}
	}

	@Nested
	@DisplayName("Tests @ValidDateRange")
	class ValidDateRangeTests {

		@Test
		@DisplayName("Date range valide : deliveryDate après orderDate")
		void validDateRange_deliveryAfterOrder() {
			Order order = createValidOrder();
			order.setOrderDate(LocalDateTime.of(2025, 1, 1, 10, 0));
			order.setDeliveryDate(LocalDateTime.of(2025, 1, 5, 10, 0));

			Set<ConstraintViolation<Order>> violations = validator.validate(order);
			// Filtrer uniquement les violations de ValidDateRange
			long dateRangeViolations = violations.stream()
				.filter(v -> v.getConstraintDescriptor().getAnnotation().annotationType() == ValidDateRange.class)
				.count();
			assertThat(dateRangeViolations).isZero();
		}

		@Test
		@DisplayName("Date range valide : deliveryDate égale à orderDate")
		void validDateRange_sameDay() {
			Order order = createValidOrder();
			LocalDateTime sameDate = LocalDateTime.of(2025, 1, 1, 10, 0);
			order.setOrderDate(sameDate);
			order.setDeliveryDate(sameDate);

			Set<ConstraintViolation<Order>> violations = validator.validate(order);
			long dateRangeViolations = violations.stream()
				.filter(v -> v.getConstraintDescriptor().getAnnotation().annotationType() == ValidDateRange.class)
				.count();
			assertThat(dateRangeViolations).isZero();
		}

		@Test
		@DisplayName("Date range valide : deliveryDate null (optionnel)")
		void validDateRange_deliveryNull() {
			Order order = createValidOrder();
			order.setOrderDate(LocalDateTime.of(2025, 1, 1, 10, 0));
			order.setDeliveryDate(null);

			Set<ConstraintViolation<Order>> violations = validator.validate(order);
			long dateRangeViolations = violations.stream()
				.filter(v -> v.getConstraintDescriptor().getAnnotation().annotationType() == ValidDateRange.class)
				.count();
			assertThat(dateRangeViolations).isZero();
		}

		@Test
		@DisplayName("Date range invalide : deliveryDate avant orderDate")
		void invalidDateRange_deliveryBeforeOrder() {
			Order order = createValidOrder();
			order.setOrderDate(LocalDateTime.of(2025, 1, 5, 10, 0));
			order.setDeliveryDate(LocalDateTime.of(2025, 1, 1, 10, 0));

			Set<ConstraintViolation<Order>> violations = validator.validate(order);
			long dateRangeViolations = violations.stream()
				.filter(v -> v.getConstraintDescriptor().getAnnotation().annotationType() == ValidDateRange.class)
				.count();
			assertThat(dateRangeViolations).isEqualTo(1);
		}

		private Order createValidOrder() {
			Order order = new Order("Test Client", "test@example.com");
			order.setStatus(com.formations.spring_products_api.model.OrderStatus.PENDING);
			order.setTotalAmount(new BigDecimal("100.00"));
			// Ne pas ajouter d'items pour éviter les erreurs @NotEmpty dans ces tests
			return order;
		}
	}
}
