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
@GraphQLName("Summary")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Summary")
public class Summary implements Serializable {

	public static Summary toDTO(String json) {
		return ObjectMapperUtil.readValue(Summary.class, json);
	}

	public static Summary unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Summary.class, json);
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
	public Integer getItemsQuantity() {
		if (_itemsQuantitySupplier != null) {
			itemsQuantity = _itemsQuantitySupplier.get();

			_itemsQuantitySupplier = null;
		}

		return itemsQuantity;
	}

	public void setItemsQuantity(Integer itemsQuantity) {
		this.itemsQuantity = itemsQuantity;

		_itemsQuantitySupplier = null;
	}

	@JsonIgnore
	public void setItemsQuantity(
		UnsafeSupplier<Integer, Exception> itemsQuantityUnsafeSupplier) {

		_itemsQuantitySupplier = () -> {
			try {
				return itemsQuantityUnsafeSupplier.get();
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
	protected Integer itemsQuantity;

	@JsonIgnore
	private Supplier<Integer> _itemsQuantitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getShippingDiscountPercentages() {
		if (_shippingDiscountPercentagesSupplier != null) {
			shippingDiscountPercentages =
				_shippingDiscountPercentagesSupplier.get();

			_shippingDiscountPercentagesSupplier = null;
		}

		return shippingDiscountPercentages;
	}

	public void setShippingDiscountPercentages(
		String[] shippingDiscountPercentages) {

		this.shippingDiscountPercentages = shippingDiscountPercentages;

		_shippingDiscountPercentagesSupplier = null;
	}

	@JsonIgnore
	public void setShippingDiscountPercentages(
		UnsafeSupplier<String[], Exception>
			shippingDiscountPercentagesUnsafeSupplier) {

		_shippingDiscountPercentagesSupplier = () -> {
			try {
				return shippingDiscountPercentagesUnsafeSupplier.get();
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
	protected String[] shippingDiscountPercentages;

	@JsonIgnore
	private Supplier<String[]> _shippingDiscountPercentagesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getShippingDiscountValue() {
		if (_shippingDiscountValueSupplier != null) {
			shippingDiscountValue = _shippingDiscountValueSupplier.get();

			_shippingDiscountValueSupplier = null;
		}

		return shippingDiscountValue;
	}

	public void setShippingDiscountValue(Double shippingDiscountValue) {
		this.shippingDiscountValue = shippingDiscountValue;

		_shippingDiscountValueSupplier = null;
	}

	@JsonIgnore
	public void setShippingDiscountValue(
		UnsafeSupplier<Double, Exception> shippingDiscountValueUnsafeSupplier) {

		_shippingDiscountValueSupplier = () -> {
			try {
				return shippingDiscountValueUnsafeSupplier.get();
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
	protected Double shippingDiscountValue;

	@JsonIgnore
	private Supplier<Double> _shippingDiscountValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getShippingValue() {
		if (_shippingValueSupplier != null) {
			shippingValue = _shippingValueSupplier.get();

			_shippingValueSupplier = null;
		}

		return shippingValue;
	}

	public void setShippingValue(Double shippingValue) {
		this.shippingValue = shippingValue;

		_shippingValueSupplier = null;
	}

	@JsonIgnore
	public void setShippingValue(
		UnsafeSupplier<Double, Exception> shippingValueUnsafeSupplier) {

		_shippingValueSupplier = () -> {
			try {
				return shippingValueUnsafeSupplier.get();
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
	protected Double shippingValue;

	@JsonIgnore
	private Supplier<Double> _shippingValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getSubtotal() {
		if (_subtotalSupplier != null) {
			subtotal = _subtotalSupplier.get();

			_subtotalSupplier = null;
		}

		return subtotal;
	}

	public void setSubtotal(Double subtotal) {
		this.subtotal = subtotal;

		_subtotalSupplier = null;
	}

	@JsonIgnore
	public void setSubtotal(
		UnsafeSupplier<Double, Exception> subtotalUnsafeSupplier) {

		_subtotalSupplier = () -> {
			try {
				return subtotalUnsafeSupplier.get();
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
	protected Double subtotal;

	@JsonIgnore
	private Supplier<Double> _subtotalSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getSubtotalDiscountPercentages() {
		if (_subtotalDiscountPercentagesSupplier != null) {
			subtotalDiscountPercentages =
				_subtotalDiscountPercentagesSupplier.get();

			_subtotalDiscountPercentagesSupplier = null;
		}

		return subtotalDiscountPercentages;
	}

	public void setSubtotalDiscountPercentages(
		String[] subtotalDiscountPercentages) {

		this.subtotalDiscountPercentages = subtotalDiscountPercentages;

		_subtotalDiscountPercentagesSupplier = null;
	}

	@JsonIgnore
	public void setSubtotalDiscountPercentages(
		UnsafeSupplier<String[], Exception>
			subtotalDiscountPercentagesUnsafeSupplier) {

		_subtotalDiscountPercentagesSupplier = () -> {
			try {
				return subtotalDiscountPercentagesUnsafeSupplier.get();
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
	protected String[] subtotalDiscountPercentages;

	@JsonIgnore
	private Supplier<String[]> _subtotalDiscountPercentagesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getSubtotalDiscountValue() {
		if (_subtotalDiscountValueSupplier != null) {
			subtotalDiscountValue = _subtotalDiscountValueSupplier.get();

			_subtotalDiscountValueSupplier = null;
		}

		return subtotalDiscountValue;
	}

	public void setSubtotalDiscountValue(Double subtotalDiscountValue) {
		this.subtotalDiscountValue = subtotalDiscountValue;

		_subtotalDiscountValueSupplier = null;
	}

	@JsonIgnore
	public void setSubtotalDiscountValue(
		UnsafeSupplier<Double, Exception> subtotalDiscountValueUnsafeSupplier) {

		_subtotalDiscountValueSupplier = () -> {
			try {
				return subtotalDiscountValueUnsafeSupplier.get();
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
	protected Double subtotalDiscountValue;

	@JsonIgnore
	private Supplier<Double> _subtotalDiscountValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getTaxValue() {
		if (_taxValueSupplier != null) {
			taxValue = _taxValueSupplier.get();

			_taxValueSupplier = null;
		}

		return taxValue;
	}

	public void setTaxValue(Double taxValue) {
		this.taxValue = taxValue;

		_taxValueSupplier = null;
	}

	@JsonIgnore
	public void setTaxValue(
		UnsafeSupplier<Double, Exception> taxValueUnsafeSupplier) {

		_taxValueSupplier = () -> {
			try {
				return taxValueUnsafeSupplier.get();
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
	protected Double taxValue;

	@JsonIgnore
	private Supplier<Double> _taxValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getTotal() {
		if (_totalSupplier != null) {
			total = _totalSupplier.get();

			_totalSupplier = null;
		}

		return total;
	}

	public void setTotal(Double total) {
		this.total = total;

		_totalSupplier = null;
	}

	@JsonIgnore
	public void setTotal(
		UnsafeSupplier<Double, Exception> totalUnsafeSupplier) {

		_totalSupplier = () -> {
			try {
				return totalUnsafeSupplier.get();
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
	protected Double total;

	@JsonIgnore
	private Supplier<Double> _totalSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getTotalDiscountPercentages() {
		if (_totalDiscountPercentagesSupplier != null) {
			totalDiscountPercentages = _totalDiscountPercentagesSupplier.get();

			_totalDiscountPercentagesSupplier = null;
		}

		return totalDiscountPercentages;
	}

	public void setTotalDiscountPercentages(String[] totalDiscountPercentages) {
		this.totalDiscountPercentages = totalDiscountPercentages;

		_totalDiscountPercentagesSupplier = null;
	}

	@JsonIgnore
	public void setTotalDiscountPercentages(
		UnsafeSupplier<String[], Exception>
			totalDiscountPercentagesUnsafeSupplier) {

		_totalDiscountPercentagesSupplier = () -> {
			try {
				return totalDiscountPercentagesUnsafeSupplier.get();
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
	protected String[] totalDiscountPercentages;

	@JsonIgnore
	private Supplier<String[]> _totalDiscountPercentagesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getTotalDiscountValue() {
		if (_totalDiscountValueSupplier != null) {
			totalDiscountValue = _totalDiscountValueSupplier.get();

			_totalDiscountValueSupplier = null;
		}

		return totalDiscountValue;
	}

	public void setTotalDiscountValue(Double totalDiscountValue) {
		this.totalDiscountValue = totalDiscountValue;

		_totalDiscountValueSupplier = null;
	}

	@JsonIgnore
	public void setTotalDiscountValue(
		UnsafeSupplier<Double, Exception> totalDiscountValueUnsafeSupplier) {

		_totalDiscountValueSupplier = () -> {
			try {
				return totalDiscountValueUnsafeSupplier.get();
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
	protected Double totalDiscountValue;

	@JsonIgnore
	private Supplier<Double> _totalDiscountValueSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Summary)) {
			return false;
		}

		Summary summary = (Summary)object;

		return Objects.equals(toString(), summary.toString());
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

		Integer itemsQuantity = getItemsQuantity();

		if (itemsQuantity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"itemsQuantity\": ");

			sb.append(itemsQuantity);
		}

		String[] shippingDiscountPercentages = getShippingDiscountPercentages();

		if (shippingDiscountPercentages != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountPercentages\": ");

			sb.append("[");

			for (int i = 0; i < shippingDiscountPercentages.length; i++) {
				sb.append("\"");

				sb.append(_escape(shippingDiscountPercentages[i]));

				sb.append("\"");

				if ((i + 1) < shippingDiscountPercentages.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Double shippingDiscountValue = getShippingDiscountValue();

		if (shippingDiscountValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountValue\": ");

			sb.append(shippingDiscountValue);
		}

		Double shippingValue = getShippingValue();

		if (shippingValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingValue\": ");

			sb.append(shippingValue);
		}

		Double subtotal = getSubtotal();

		if (subtotal != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotal\": ");

			sb.append(subtotal);
		}

		String[] subtotalDiscountPercentages = getSubtotalDiscountPercentages();

		if (subtotalDiscountPercentages != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountPercentages\": ");

			sb.append("[");

			for (int i = 0; i < subtotalDiscountPercentages.length; i++) {
				sb.append("\"");

				sb.append(_escape(subtotalDiscountPercentages[i]));

				sb.append("\"");

				if ((i + 1) < subtotalDiscountPercentages.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Double subtotalDiscountValue = getSubtotalDiscountValue();

		if (subtotalDiscountValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountValue\": ");

			sb.append(subtotalDiscountValue);
		}

		Double taxValue = getTaxValue();

		if (taxValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxValue\": ");

			sb.append(taxValue);
		}

		Double total = getTotal();

		if (total != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"total\": ");

			sb.append(total);
		}

		String[] totalDiscountPercentages = getTotalDiscountPercentages();

		if (totalDiscountPercentages != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountPercentages\": ");

			sb.append("[");

			for (int i = 0; i < totalDiscountPercentages.length; i++) {
				sb.append("\"");

				sb.append(_escape(totalDiscountPercentages[i]));

				sb.append("\"");

				if ((i + 1) < totalDiscountPercentages.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Double totalDiscountValue = getTotalDiscountValue();

		if (totalDiscountValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountValue\": ");

			sb.append(totalDiscountValue);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.punchout.dto.v1_0.Summary",
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