/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
@GraphQLName("TierPrice")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "TierPrice")
public class TierPrice implements Serializable {

	public static TierPrice toDTO(String json) {
		return ObjectMapperUtil.readValue(TierPrice.class, json);
	}

	public static TierPrice unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(TierPrice.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getCurrency() {
		if (_currencySupplier != null) {
			currency = _currencySupplier.get();

			_currencySupplier = null;
		}

		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;

		_currencySupplier = null;
	}

	@JsonIgnore
	public void setCurrency(
		UnsafeSupplier<String, Exception> currencyUnsafeSupplier) {

		_currencySupplier = () -> {
			try {
				return currencyUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String currency;

	@JsonIgnore
	private Supplier<String> _currencySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getPrice() {
		if (_priceSupplier != null) {
			price = _priceSupplier.get();

			_priceSupplier = null;
		}

		return price;
	}

	public void setPrice(Double price) {
		this.price = price;

		_priceSupplier = null;
	}

	@JsonIgnore
	public void setPrice(
		UnsafeSupplier<Double, Exception> priceUnsafeSupplier) {

		_priceSupplier = () -> {
			try {
				return priceUnsafeSupplier.get();
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
	protected Double price;

	@JsonIgnore
	private Supplier<Double> _priceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getPriceFormatted() {
		if (_priceFormattedSupplier != null) {
			priceFormatted = _priceFormattedSupplier.get();

			_priceFormattedSupplier = null;
		}

		return priceFormatted;
	}

	public void setPriceFormatted(String priceFormatted) {
		this.priceFormatted = priceFormatted;

		_priceFormattedSupplier = null;
	}

	@JsonIgnore
	public void setPriceFormatted(
		UnsafeSupplier<String, Exception> priceFormattedUnsafeSupplier) {

		_priceFormattedSupplier = () -> {
			try {
				return priceFormattedUnsafeSupplier.get();
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
	protected String priceFormatted;

	@JsonIgnore
	private Supplier<String> _priceFormattedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getPricingQuantityPrice() {
		if (_pricingQuantityPriceSupplier != null) {
			pricingQuantityPrice = _pricingQuantityPriceSupplier.get();

			_pricingQuantityPriceSupplier = null;
		}

		return pricingQuantityPrice;
	}

	public void setPricingQuantityPrice(Double pricingQuantityPrice) {
		this.pricingQuantityPrice = pricingQuantityPrice;

		_pricingQuantityPriceSupplier = null;
	}

	@JsonIgnore
	public void setPricingQuantityPrice(
		UnsafeSupplier<Double, Exception> pricingQuantityPriceUnsafeSupplier) {

		_pricingQuantityPriceSupplier = () -> {
			try {
				return pricingQuantityPriceUnsafeSupplier.get();
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
	protected Double pricingQuantityPrice;

	@JsonIgnore
	private Supplier<Double> _pricingQuantityPriceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getPricingQuantityPriceFormatted() {
		if (_pricingQuantityPriceFormattedSupplier != null) {
			pricingQuantityPriceFormatted =
				_pricingQuantityPriceFormattedSupplier.get();

			_pricingQuantityPriceFormattedSupplier = null;
		}

		return pricingQuantityPriceFormatted;
	}

	public void setPricingQuantityPriceFormatted(
		String pricingQuantityPriceFormatted) {

		this.pricingQuantityPriceFormatted = pricingQuantityPriceFormatted;

		_pricingQuantityPriceFormattedSupplier = null;
	}

	@JsonIgnore
	public void setPricingQuantityPriceFormatted(
		UnsafeSupplier<String, Exception>
			pricingQuantityPriceFormattedUnsafeSupplier) {

		_pricingQuantityPriceFormattedSupplier = () -> {
			try {
				return pricingQuantityPriceFormattedUnsafeSupplier.get();
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
	protected String pricingQuantityPriceFormatted;

	@JsonIgnore
	private Supplier<String> _pricingQuantityPriceFormattedSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "202.1")
	@Valid
	public BigDecimal getQuantity() {
		if (_quantitySupplier != null) {
			quantity = _quantitySupplier.get();

			_quantitySupplier = null;
		}

		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;

		_quantitySupplier = null;
	}

	@JsonIgnore
	public void setQuantity(
		UnsafeSupplier<BigDecimal, Exception> quantityUnsafeSupplier) {

		_quantitySupplier = () -> {
			try {
				return quantityUnsafeSupplier.get();
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
	protected BigDecimal quantity;

	@JsonIgnore
	private Supplier<BigDecimal> _quantitySupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TierPrice)) {
			return false;
		}

		TierPrice tierPrice = (TierPrice)object;

		return Objects.equals(toString(), tierPrice.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String currency = getCurrency();

		if (currency != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currency\": ");

			sb.append("\"");

			sb.append(_escape(currency));

			sb.append("\"");
		}

		Double price = getPrice();

		if (price != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"price\": ");

			sb.append(price);
		}

		String priceFormatted = getPriceFormatted();

		if (priceFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceFormatted\": ");

			sb.append("\"");

			sb.append(_escape(priceFormatted));

			sb.append("\"");
		}

		Double pricingQuantityPrice = getPricingQuantityPrice();

		if (pricingQuantityPrice != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pricingQuantityPrice\": ");

			sb.append(pricingQuantityPrice);
		}

		String pricingQuantityPriceFormatted =
			getPricingQuantityPriceFormatted();

		if (pricingQuantityPriceFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pricingQuantityPriceFormatted\": ");

			sb.append("\"");

			sb.append(_escape(pricingQuantityPriceFormatted));

			sb.append("\"");
		}

		BigDecimal quantity = getQuantity();

		if (quantity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"quantity\": ");

			sb.append(quantity);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.delivery.catalog.dto.v1_0.TierPrice",
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

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}