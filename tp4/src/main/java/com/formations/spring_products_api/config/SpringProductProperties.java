package com.formations.spring_products_api.config;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "products")
public class SpringProductProperties {

	private Security security;

	public Security getSecurity() {
		return security;
	}

	public void setSecurity(Security security) {
		this.security = security;
	}

	public static class Security {

		private String keyId;
		private RSAPublicKey publicKey;
		private RSAPrivateKey privateKey;

		public String getKeyId() {
			return keyId;
		}

		public void setKeyId(String keyId) {
			this.keyId = keyId;
		}

		public RSAPublicKey getPublicKey() {
			return publicKey;
		}

		public void setPublicKey(RSAPublicKey publicKey) {
			this.publicKey = publicKey;
		}

		public RSAPrivateKey getPrivateKey() {
			return privateKey;
		}

		public void setPrivateKey(RSAPrivateKey privateKey) {
			this.privateKey = privateKey;
		}
	}
}
