/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.dto.v2_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "product binding that restricts a price modifier to a specific product. Backed by price modifier link with the product class name.",
	value = "PriceModifierProduct"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "product binding that restricts a price modifier to a specific product. Backed by price modifier link with the product class name."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PriceModifierProduct")
public class PriceModifierProduct implements Serializable {

	public static PriceModifierProduct toDTO(String json) {
		return ObjectMapperUtil.readValue(PriceModifierProduct.class, json);
	}

	public static PriceModifierProduct unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			PriceModifierProduct.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Map of HATEOAS actions available to the current user, keyed by action name. Each value carries the href template and HTTP method, computed dynamically from user permissions. Read-only."
	)
	@Valid
	public Map<String, Map<String, String>> getActions() {
		if (_actionsSupplier != null) {
			actions = _actionsSupplier.get();

			_actionsSupplier = null;
		}

		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;

		_actionsSupplier = null;
	}

	@JsonIgnore
	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		_actionsSupplier = () -> {
			try {
				return actionsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Map of HATEOAS actions available to the current user, keyed by action name. Each value carries the href template and HTTP method, computed dynamically from user permissions. Read-only."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Currency code of the catalog the referenced product belongs to. On read it mirrors that catalog's currency code; on write it is used only when the catalog does not yet exist, to create it with the right currency.",
		example = "USD"
	)
	public String getCatalogCurrencyCode() {
		if (_catalogCurrencyCodeSupplier != null) {
			catalogCurrencyCode = _catalogCurrencyCodeSupplier.get();

			_catalogCurrencyCodeSupplier = null;
		}

		return catalogCurrencyCode;
	}

	public void setCatalogCurrencyCode(String catalogCurrencyCode) {
		this.catalogCurrencyCode = catalogCurrencyCode;

		_catalogCurrencyCodeSupplier = null;
	}

	@JsonIgnore
	public void setCatalogCurrencyCode(
		UnsafeSupplier<String, Exception> catalogCurrencyCodeUnsafeSupplier) {

		_catalogCurrencyCodeSupplier = () -> {
			try {
				return catalogCurrencyCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Currency code of the catalog the referenced product belongs to. On read it mirrors that catalog's currency code; on write it is used only when the catalog does not yet exist, to create it with the right currency."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String catalogCurrencyCode;

	@JsonIgnore
	private Supplier<String> _catalogCurrencyCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the catalog currency. On read it mirrors that currency's external reference code; on write it is resolved after `catalogCurrencyCode` and, when the currency has not been imported yet, used to create it as an empty stub.",
		example = "US_DOLLAR"
	)
	public String getCatalogCurrencyExternalReferenceCode() {
		if (_catalogCurrencyExternalReferenceCodeSupplier != null) {
			catalogCurrencyExternalReferenceCode =
				_catalogCurrencyExternalReferenceCodeSupplier.get();

			_catalogCurrencyExternalReferenceCodeSupplier = null;
		}

		return catalogCurrencyExternalReferenceCode;
	}

	public void setCatalogCurrencyExternalReferenceCode(
		String catalogCurrencyExternalReferenceCode) {

		this.catalogCurrencyExternalReferenceCode =
			catalogCurrencyExternalReferenceCode;

		_catalogCurrencyExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setCatalogCurrencyExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			catalogCurrencyExternalReferenceCodeUnsafeSupplier) {

		_catalogCurrencyExternalReferenceCodeSupplier = () -> {
			try {
				return catalogCurrencyExternalReferenceCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "External reference code of the catalog currency. On read it mirrors that currency's external reference code; on write it is resolved after `catalogCurrencyCode` and, when the currency has not been imported yet, used to create it as an empty stub."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String catalogCurrencyExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _catalogCurrencyExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the catalog the referenced product belongs to. On read it mirrors that catalog's external reference code; on write it is used only when the product does not yet exist, to create it in the right catalog.",
		example = "CAT0111"
	)
	public String getCatalogExternalReferenceCode() {
		if (_catalogExternalReferenceCodeSupplier != null) {
			catalogExternalReferenceCode =
				_catalogExternalReferenceCodeSupplier.get();

			_catalogExternalReferenceCodeSupplier = null;
		}

		return catalogExternalReferenceCode;
	}

	public void setCatalogExternalReferenceCode(
		String catalogExternalReferenceCode) {

		this.catalogExternalReferenceCode = catalogExternalReferenceCode;

		_catalogExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setCatalogExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			catalogExternalReferenceCodeUnsafeSupplier) {

		_catalogExternalReferenceCodeSupplier = () -> {
			try {
				return catalogExternalReferenceCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "External reference code of the catalog the referenced product belongs to. On read it mirrors that catalog's external reference code; on write it is used only when the product does not yet exist, to create it in the right catalog."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String catalogExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _catalogExternalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the bound priceModifier; alternative to `priceModifierId` for lookup.",
		example = "DAB-34098-789-N"
	)
	public String getPriceModifierExternalReferenceCode() {
		if (_priceModifierExternalReferenceCodeSupplier != null) {
			priceModifierExternalReferenceCode =
				_priceModifierExternalReferenceCodeSupplier.get();

			_priceModifierExternalReferenceCodeSupplier = null;
		}

		return priceModifierExternalReferenceCode;
	}

	public void setPriceModifierExternalReferenceCode(
		String priceModifierExternalReferenceCode) {

		this.priceModifierExternalReferenceCode =
			priceModifierExternalReferenceCode;

		_priceModifierExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setPriceModifierExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			priceModifierExternalReferenceCodeUnsafeSupplier) {

		_priceModifierExternalReferenceCodeSupplier = () -> {
			try {
				return priceModifierExternalReferenceCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "External reference code of the bound priceModifier; alternative to `priceModifierId` for lookup."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String priceModifierExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _priceModifierExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the priceModifier entity (FK identifier).",
		example = "30324"
	)
	public Long getPriceModifierId() {
		if (_priceModifierIdSupplier != null) {
			priceModifierId = _priceModifierIdSupplier.get();

			_priceModifierIdSupplier = null;
		}

		return priceModifierId;
	}

	public void setPriceModifierId(Long priceModifierId) {
		this.priceModifierId = priceModifierId;

		_priceModifierIdSupplier = null;
	}

	@JsonIgnore
	public void setPriceModifierId(
		UnsafeSupplier<Long, Exception> priceModifierIdUnsafeSupplier) {

		_priceModifierIdSupplier = () -> {
			try {
				return priceModifierIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Reference to the priceModifier entity (FK identifier)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long priceModifierId;

	@JsonIgnore
	private Supplier<Long> _priceModifierIdSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the priceModifierProduct entity (FK identifier).",
		example = "30643"
	)
	public Long getPriceModifierProductId() {
		if (_priceModifierProductIdSupplier != null) {
			priceModifierProductId = _priceModifierProductIdSupplier.get();

			_priceModifierProductIdSupplier = null;
		}

		return priceModifierProductId;
	}

	public void setPriceModifierProductId(Long priceModifierProductId) {
		this.priceModifierProductId = priceModifierProductId;

		_priceModifierProductIdSupplier = null;
	}

	@JsonIgnore
	public void setPriceModifierProductId(
		UnsafeSupplier<Long, Exception> priceModifierProductIdUnsafeSupplier) {

		_priceModifierProductIdSupplier = () -> {
			try {
				return priceModifierProductIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Reference to the priceModifierProduct entity (FK identifier)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long priceModifierProductId;

	@JsonIgnore
	private Supplier<Long> _priceModifierProductIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Product getProduct() {
		if (_productSupplier != null) {
			product = _productSupplier.get();

			_productSupplier = null;
		}

		return product;
	}

	public void setProduct(Product product) {
		this.product = product;

		_productSupplier = null;
	}

	@JsonIgnore
	public void setProduct(
		UnsafeSupplier<Product, Exception> productUnsafeSupplier) {

		_productSupplier = () -> {
			try {
				return productUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Product product;

	@JsonIgnore
	private Supplier<Product> _productSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "External reference code of the bound product; alternative to `productId` for lookup.",
		example = "PAB-34098-789-N"
	)
	public String getProductExternalReferenceCode() {
		if (_productExternalReferenceCodeSupplier != null) {
			productExternalReferenceCode =
				_productExternalReferenceCodeSupplier.get();

			_productExternalReferenceCodeSupplier = null;
		}

		return productExternalReferenceCode;
	}

	public void setProductExternalReferenceCode(
		String productExternalReferenceCode) {

		this.productExternalReferenceCode = productExternalReferenceCode;

		_productExternalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setProductExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			productExternalReferenceCodeUnsafeSupplier) {

		_productExternalReferenceCodeSupplier = () -> {
			try {
				return productExternalReferenceCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "External reference code of the bound product; alternative to `productId` for lookup."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String productExternalReferenceCode;

	@JsonIgnore
	private Supplier<String> _productExternalReferenceCodeSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Reference to the product entity (FK identifier).",
		example = "30130"
	)
	public Long getProductId() {
		if (_productIdSupplier != null) {
			productId = _productIdSupplier.get();

			_productIdSupplier = null;
		}

		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;

		_productIdSupplier = null;
	}

	@JsonIgnore
	public void setProductId(
		UnsafeSupplier<Long, Exception> productIdUnsafeSupplier) {

		_productIdSupplier = () -> {
			try {
				return productIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Reference to the product entity (FK identifier)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long productId;

	@JsonIgnore
	private Supplier<Long> _productIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Product type name of the bound product. On read it mirrors that product's type; on write it is used only when the product does not yet exist, to create it with the right type, and is then required.",
		example = "simple"
	)
	public String getProductType() {
		if (_productTypeSupplier != null) {
			productType = _productTypeSupplier.get();

			_productTypeSupplier = null;
		}

		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;

		_productTypeSupplier = null;
	}

	@JsonIgnore
	public void setProductType(
		UnsafeSupplier<String, Exception> productTypeUnsafeSupplier) {

		_productTypeSupplier = () -> {
			try {
				return productTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Product type name of the bound product. On read it mirrors that product's type; on write it is used only when the product does not yet exist, to create it with the right type, and is then required."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String productType;

	@JsonIgnore
	private Supplier<String> _productTypeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PriceModifierProduct)) {
			return false;
		}

		PriceModifierProduct priceModifierProduct =
			(PriceModifierProduct)object;

		return Objects.equals(toString(), priceModifierProduct.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Map<String, Map<String, String>> actions = getActions();

		if (actions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(actions));
		}

		String catalogCurrencyCode = getCatalogCurrencyCode();

		if (catalogCurrencyCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"catalogCurrencyCode\": ");

			sb.append("\"");

			sb.append(_escape(catalogCurrencyCode));

			sb.append("\"");
		}

		String catalogCurrencyExternalReferenceCode =
			getCatalogCurrencyExternalReferenceCode();

		if (catalogCurrencyExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"catalogCurrencyExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(catalogCurrencyExternalReferenceCode));

			sb.append("\"");
		}

		String catalogExternalReferenceCode = getCatalogExternalReferenceCode();

		if (catalogExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"catalogExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(catalogExternalReferenceCode));

			sb.append("\"");
		}

		String priceModifierExternalReferenceCode =
			getPriceModifierExternalReferenceCode();

		if (priceModifierExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceModifierExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(priceModifierExternalReferenceCode));

			sb.append("\"");
		}

		Long priceModifierId = getPriceModifierId();

		if (priceModifierId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceModifierId\": ");

			sb.append(priceModifierId);
		}

		Long priceModifierProductId = getPriceModifierProductId();

		if (priceModifierProductId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceModifierProductId\": ");

			sb.append(priceModifierProductId);
		}

		Product product = getProduct();

		if (product != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"product\": ");

			sb.append(String.valueOf(product));
		}

		String productExternalReferenceCode = getProductExternalReferenceCode();

		if (productExternalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(productExternalReferenceCode));

			sb.append("\"");
		}

		Long productId = getProductId();

		if (productId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productId\": ");

			sb.append(productId);
		}

		String productType = getProductType();

		if (productType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productType\": ");

			sb.append("\"");

			sb.append(_escape(productType));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceModifierProduct",
		name = "x-class-name"
	)
	public String xClassName;

	private static String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[])value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof Map) {
						sb.append(_toJSON((Map<String, ?>)valueArray[i]));
					}
					else if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>)value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static String _toJSON(Object value) {
		if (value instanceof Collection) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray((Collection<?>)value));
		}
		else if (value instanceof Map) {
			return String.valueOf(
				JSONFactoryUtil.createJSONObject((Map<?, ?>)value));
		}
		else if (value instanceof Object[]) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray(
					Arrays.asList((Object[])value)));
		}
		else if (value instanceof String) {
			return StringBundler.concat("\"", _escape(value), "\"");
		}

		return String.valueOf(value);
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
// LIFERAY-REST-BUILDER-HASH:-7465539