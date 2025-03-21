/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
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

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Andrea Sbarra
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
	public String getDiscount() {
		if (_discountSupplier != null) {
			discount = _discountSupplier.get();

			_discountSupplier = null;
		}

		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;

		_discountSupplier = null;
	}

	@JsonIgnore
	public void setDiscount(
		UnsafeSupplier<String, Exception> discountUnsafeSupplier) {

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
	protected String discount;

	@JsonIgnore
	private Supplier<String> _discountSupplier;

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
	public String[] getDiscountPercentages() {
		if (_discountPercentagesSupplier != null) {
			discountPercentages = _discountPercentagesSupplier.get();

			_discountPercentagesSupplier = null;
		}

		return discountPercentages;
	}

	public void setDiscountPercentages(String[] discountPercentages) {
		this.discountPercentages = discountPercentages;

		_discountPercentagesSupplier = null;
	}

	@JsonIgnore
	public void setDiscountPercentages(
		UnsafeSupplier<String[], Exception> discountPercentagesUnsafeSupplier) {

		_discountPercentagesSupplier = () -> {
			try {
				return discountPercentagesUnsafeSupplier.get();
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
	protected String[] discountPercentages;

	@JsonIgnore
	private Supplier<String[]> _discountPercentagesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getFinalPrice() {
		if (_finalPriceSupplier != null) {
			finalPrice = _finalPriceSupplier.get();

			_finalPriceSupplier = null;
		}

		return finalPrice;
	}

	public void setFinalPrice(String finalPrice) {
		this.finalPrice = finalPrice;

		_finalPriceSupplier = null;
	}

	@JsonIgnore
	public void setFinalPrice(
		UnsafeSupplier<String, Exception> finalPriceUnsafeSupplier) {

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
	protected String finalPrice;

	@JsonIgnore
	private Supplier<String> _finalPriceSupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String priceFormatted;

	@JsonIgnore
	private Supplier<String> _priceFormattedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getPriceOnApplication() {
		if (_priceOnApplicationSupplier != null) {
			priceOnApplication = _priceOnApplicationSupplier.get();

			_priceOnApplicationSupplier = null;
		}

		return priceOnApplication;
	}

	public void setPriceOnApplication(Boolean priceOnApplication) {
		this.priceOnApplication = priceOnApplication;

		_priceOnApplicationSupplier = null;
	}

	@JsonIgnore
	public void setPriceOnApplication(
		UnsafeSupplier<Boolean, Exception> priceOnApplicationUnsafeSupplier) {

		_priceOnApplicationSupplier = () -> {
			try {
				return priceOnApplicationUnsafeSupplier.get();
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
	protected Boolean priceOnApplication;

	@JsonIgnore
	private Supplier<Boolean> _priceOnApplicationSupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String pricingQuantityPriceFormatted;

	@JsonIgnore
	private Supplier<String> _pricingQuantityPriceFormattedSupplier;

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

	@io.swagger.v3.oas.annotations.media.Schema
	public String getPromoPriceFormatted() {
		if (_promoPriceFormattedSupplier != null) {
			promoPriceFormatted = _promoPriceFormattedSupplier.get();

			_promoPriceFormattedSupplier = null;
		}

		return promoPriceFormatted;
	}

	public void setPromoPriceFormatted(String promoPriceFormatted) {
		this.promoPriceFormatted = promoPriceFormatted;

		_promoPriceFormattedSupplier = null;
	}

	@JsonIgnore
	public void setPromoPriceFormatted(
		UnsafeSupplier<String, Exception> promoPriceFormattedUnsafeSupplier) {

		_promoPriceFormattedSupplier = () -> {
			try {
				return promoPriceFormattedUnsafeSupplier.get();
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
	protected String promoPriceFormatted;

	@JsonIgnore
	private Supplier<String> _promoPriceFormattedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getTierPrice() {
		if (_tierPriceSupplier != null) {
			tierPrice = _tierPriceSupplier.get();

			_tierPriceSupplier = null;
		}

		return tierPrice;
	}

	public void setTierPrice(Double tierPrice) {
		this.tierPrice = tierPrice;

		_tierPriceSupplier = null;
	}

	@JsonIgnore
	public void setTierPrice(
		UnsafeSupplier<Double, Exception> tierPriceUnsafeSupplier) {

		_tierPriceSupplier = () -> {
			try {
				return tierPriceUnsafeSupplier.get();
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
	protected Double tierPrice;

	@JsonIgnore
	private Supplier<Double> _tierPriceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTierPriceFormatted() {
		if (_tierPriceFormattedSupplier != null) {
			tierPriceFormatted = _tierPriceFormattedSupplier.get();

			_tierPriceFormattedSupplier = null;
		}

		return tierPriceFormatted;
	}

	public void setTierPriceFormatted(String tierPriceFormatted) {
		this.tierPriceFormatted = tierPriceFormatted;

		_tierPriceFormattedSupplier = null;
	}

	@JsonIgnore
	public void setTierPriceFormatted(
		UnsafeSupplier<String, Exception> tierPriceFormattedUnsafeSupplier) {

		_tierPriceFormattedSupplier = () -> {
			try {
				return tierPriceFormattedUnsafeSupplier.get();
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
	protected String tierPriceFormatted;

	@JsonIgnore
	private Supplier<String> _tierPriceFormattedSupplier;

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

		String discount = getDiscount();

		if (discount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discount\": ");

			sb.append("\"");

			sb.append(_escape(discount));

			sb.append("\"");
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

		String[] discountPercentages = getDiscountPercentages();

		if (discountPercentages != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"discountPercentages\": ");

			sb.append("[");

			for (int i = 0; i < discountPercentages.length; i++) {
				sb.append("\"");

				sb.append(_escape(discountPercentages[i]));

				sb.append("\"");

				if ((i + 1) < discountPercentages.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String finalPrice = getFinalPrice();

		if (finalPrice != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"finalPrice\": ");

			sb.append("\"");

			sb.append(_escape(finalPrice));

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

		Boolean priceOnApplication = getPriceOnApplication();

		if (priceOnApplication != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceOnApplication\": ");

			sb.append(priceOnApplication);
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

		Double promoPrice = getPromoPrice();

		if (promoPrice != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"promoPrice\": ");

			sb.append(promoPrice);
		}

		String promoPriceFormatted = getPromoPriceFormatted();

		if (promoPriceFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"promoPriceFormatted\": ");

			sb.append("\"");

			sb.append(_escape(promoPriceFormatted));

			sb.append("\"");
		}

		Double tierPrice = getTierPrice();

		if (tierPrice != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"tierPrice\": ");

			sb.append(tierPrice);
		}

		String tierPriceFormatted = getTierPriceFormatted();

		if (tierPriceFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"tierPriceFormatted\": ");

			sb.append("\"");

			sb.append(_escape(tierPriceFormatted));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Price",
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