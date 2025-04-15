/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.punchout.dto.v1_0;

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

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Jaclyn Ong
 * @generated
 */
@Generated("")
@GraphQLName("Price")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Price")
public class Price implements Serializable {

	public static Price toDTO(String json) {
		return ObjectMapperUtil.readValue(Price.class, json);
	}

	public static Price unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Price.class, json);
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
	public Double getDiscount() {
		if (_discountSupplier != null) {
			discount = _discountSupplier.get();

			_discountSupplier = null;
		}

		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;

		_discountSupplier = null;
	}

	@JsonIgnore
	public void setDiscount(
		UnsafeSupplier<Double, Exception> discountUnsafeSupplier) {

		_discountSupplier = () -> {
			try {
				return discountUnsafeSupplier.get();
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
	protected Double discount;

	@JsonIgnore
	private Supplier<Double> _discountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getDiscountPercentage() {
		if (_discountPercentageSupplier != null) {
			discountPercentage = _discountPercentageSupplier.get();

			_discountPercentageSupplier = null;
		}

		return discountPercentage;
	}

	public void setDiscountPercentage(String discountPercentage) {
		this.discountPercentage = discountPercentage;

		_discountPercentageSupplier = null;
	}

	@JsonIgnore
	public void setDiscountPercentage(
		UnsafeSupplier<String, Exception> discountPercentageUnsafeSupplier) {

		_discountPercentageSupplier = () -> {
			try {
				return discountPercentageUnsafeSupplier.get();
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
	protected String discountPercentage;

	@JsonIgnore
	private Supplier<String> _discountPercentageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getDiscountPercentageLevel1() {
		if (_discountPercentageLevel1Supplier != null) {
			discountPercentageLevel1 = _discountPercentageLevel1Supplier.get();

			_discountPercentageLevel1Supplier = null;
		}

		return discountPercentageLevel1;
	}

	public void setDiscountPercentageLevel1(Double discountPercentageLevel1) {
		this.discountPercentageLevel1 = discountPercentageLevel1;

		_discountPercentageLevel1Supplier = null;
	}

	@JsonIgnore
	public void setDiscountPercentageLevel1(
		UnsafeSupplier<Double, Exception>
			discountPercentageLevel1UnsafeSupplier) {

		_discountPercentageLevel1Supplier = () -> {
			try {
				return discountPercentageLevel1UnsafeSupplier.get();
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
	protected Double discountPercentageLevel1;

	@JsonIgnore
	private Supplier<Double> _discountPercentageLevel1Supplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getDiscountPercentageLevel2() {
		if (_discountPercentageLevel2Supplier != null) {
			discountPercentageLevel2 = _discountPercentageLevel2Supplier.get();

			_discountPercentageLevel2Supplier = null;
		}

		return discountPercentageLevel2;
	}

	public void setDiscountPercentageLevel2(Double discountPercentageLevel2) {
		this.discountPercentageLevel2 = discountPercentageLevel2;

		_discountPercentageLevel2Supplier = null;
	}

	@JsonIgnore
	public void setDiscountPercentageLevel2(
		UnsafeSupplier<Double, Exception>
			discountPercentageLevel2UnsafeSupplier) {

		_discountPercentageLevel2Supplier = () -> {
			try {
				return discountPercentageLevel2UnsafeSupplier.get();
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
	protected Double discountPercentageLevel2;

	@JsonIgnore
	private Supplier<Double> _discountPercentageLevel2Supplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getDiscountPercentageLevel3() {
		if (_discountPercentageLevel3Supplier != null) {
			discountPercentageLevel3 = _discountPercentageLevel3Supplier.get();

			_discountPercentageLevel3Supplier = null;
		}

		return discountPercentageLevel3;
	}

	public void setDiscountPercentageLevel3(Double discountPercentageLevel3) {
		this.discountPercentageLevel3 = discountPercentageLevel3;

		_discountPercentageLevel3Supplier = null;
	}

	@JsonIgnore
	public void setDiscountPercentageLevel3(
		UnsafeSupplier<Double, Exception>
			discountPercentageLevel3UnsafeSupplier) {

		_discountPercentageLevel3Supplier = () -> {
			try {
				return discountPercentageLevel3UnsafeSupplier.get();
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
	protected Double discountPercentageLevel3;

	@JsonIgnore
	private Supplier<Double> _discountPercentageLevel3Supplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getDiscountPercentageLevel4() {
		if (_discountPercentageLevel4Supplier != null) {
			discountPercentageLevel4 = _discountPercentageLevel4Supplier.get();

			_discountPercentageLevel4Supplier = null;
		}

		return discountPercentageLevel4;
	}

	public void setDiscountPercentageLevel4(Double discountPercentageLevel4) {
		this.discountPercentageLevel4 = discountPercentageLevel4;

		_discountPercentageLevel4Supplier = null;
	}

	@JsonIgnore
	public void setDiscountPercentageLevel4(
		UnsafeSupplier<Double, Exception>
			discountPercentageLevel4UnsafeSupplier) {

		_discountPercentageLevel4Supplier = () -> {
			try {
				return discountPercentageLevel4UnsafeSupplier.get();
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
	protected Double discountPercentageLevel4;

	@JsonIgnore
	private Supplier<Double> _discountPercentageLevel4Supplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getFinalPrice() {
		if (_finalPriceSupplier != null) {
			finalPrice = _finalPriceSupplier.get();

			_finalPriceSupplier = null;
		}

		return finalPrice;
	}

	public void setFinalPrice(Double finalPrice) {
		this.finalPrice = finalPrice;

		_finalPriceSupplier = null;
	}

	@JsonIgnore
	public void setFinalPrice(
		UnsafeSupplier<Double, Exception> finalPriceUnsafeSupplier) {

		_finalPriceSupplier = () -> {
			try {
				return finalPriceUnsafeSupplier.get();
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
	protected Double finalPrice;

	@JsonIgnore
	private Supplier<Double> _finalPriceSupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Double price;

	@JsonIgnore
	private Supplier<Double> _priceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getPromoPrice() {
		if (_promoPriceSupplier != null) {
			promoPrice = _promoPriceSupplier.get();

			_promoPriceSupplier = null;
		}

		return promoPrice;
	}

	public void setPromoPrice(Double promoPrice) {
		this.promoPrice = promoPrice;

		_promoPriceSupplier = null;
	}

	@JsonIgnore
	public void setPromoPrice(
		UnsafeSupplier<Double, Exception> promoPriceUnsafeSupplier) {

		_promoPriceSupplier = () -> {
			try {
				return promoPriceUnsafeSupplier.get();
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
	protected Double promoPrice;

	@JsonIgnore
	private Supplier<Double> _promoPriceSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Price)) {
			return false;
		}

		Price price = (Price)object;

		return Objects.equals(toString(), price.toString());
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

		Double discount = getDiscount();

		if (discount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discount\": ");

			sb.append(discount);
		}

		String discountPercentage = getDiscountPercentage();

		if (discountPercentage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountPercentage\": ");

			sb.append("\"");

			sb.append(_escape(discountPercentage));

			sb.append("\"");
		}

		Double discountPercentageLevel1 = getDiscountPercentageLevel1();

		if (discountPercentageLevel1 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountPercentageLevel1\": ");

			sb.append(discountPercentageLevel1);
		}

		Double discountPercentageLevel2 = getDiscountPercentageLevel2();

		if (discountPercentageLevel2 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountPercentageLevel2\": ");

			sb.append(discountPercentageLevel2);
		}

		Double discountPercentageLevel3 = getDiscountPercentageLevel3();

		if (discountPercentageLevel3 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountPercentageLevel3\": ");

			sb.append(discountPercentageLevel3);
		}

		Double discountPercentageLevel4 = getDiscountPercentageLevel4();

		if (discountPercentageLevel4 != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountPercentageLevel4\": ");

			sb.append(discountPercentageLevel4);
		}

		Double finalPrice = getFinalPrice();

		if (finalPrice != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"finalPrice\": ");

			sb.append(finalPrice);
		}

		Double price = getPrice();

		if (price != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"price\": ");

			sb.append(price);
		}

		Double promoPrice = getPromoPrice();

		if (promoPrice != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"promoPrice\": ");

			sb.append(promoPrice);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.punchout.dto.v1_0.Price",
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