/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.dto.v1_0;

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

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Andrea Sbarra
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String currency;

	@JsonIgnore
	private Supplier<String> _currencySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "10.1")
	@Valid
	public BigDecimal getItemsQuantity() {
		if (_itemsQuantitySupplier != null) {
			itemsQuantity = _itemsQuantitySupplier.get();

			_itemsQuantitySupplier = null;
		}

		return itemsQuantity;
	}

	public void setItemsQuantity(BigDecimal itemsQuantity) {
		this.itemsQuantity = itemsQuantity;

		_itemsQuantitySupplier = null;
	}

	@JsonIgnore
	public void setItemsQuantity(
		UnsafeSupplier<BigDecimal, Exception> itemsQuantityUnsafeSupplier) {

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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected BigDecimal itemsQuantity;

	@JsonIgnore
	private Supplier<BigDecimal> _itemsQuantitySupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double shippingDiscountValue;

	@JsonIgnore
	private Supplier<Double> _shippingDiscountValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getShippingDiscountValueFormatted() {
		if (_shippingDiscountValueFormattedSupplier != null) {
			shippingDiscountValueFormatted =
				_shippingDiscountValueFormattedSupplier.get();

			_shippingDiscountValueFormattedSupplier = null;
		}

		return shippingDiscountValueFormatted;
	}

	public void setShippingDiscountValueFormatted(
		String shippingDiscountValueFormatted) {

		this.shippingDiscountValueFormatted = shippingDiscountValueFormatted;

		_shippingDiscountValueFormattedSupplier = null;
	}

	@JsonIgnore
	public void setShippingDiscountValueFormatted(
		UnsafeSupplier<String, Exception>
			shippingDiscountValueFormattedUnsafeSupplier) {

		_shippingDiscountValueFormattedSupplier = () -> {
			try {
				return shippingDiscountValueFormattedUnsafeSupplier.get();
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
	protected String shippingDiscountValueFormatted;

	@JsonIgnore
	private Supplier<String> _shippingDiscountValueFormattedSupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double shippingValue;

	@JsonIgnore
	private Supplier<Double> _shippingValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getShippingValueFormatted() {
		if (_shippingValueFormattedSupplier != null) {
			shippingValueFormatted = _shippingValueFormattedSupplier.get();

			_shippingValueFormattedSupplier = null;
		}

		return shippingValueFormatted;
	}

	public void setShippingValueFormatted(String shippingValueFormatted) {
		this.shippingValueFormatted = shippingValueFormatted;

		_shippingValueFormattedSupplier = null;
	}

	@JsonIgnore
	public void setShippingValueFormatted(
		UnsafeSupplier<String, Exception>
			shippingValueFormattedUnsafeSupplier) {

		_shippingValueFormattedSupplier = () -> {
			try {
				return shippingValueFormattedUnsafeSupplier.get();
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
	protected String shippingValueFormatted;

	@JsonIgnore
	private Supplier<String> _shippingValueFormattedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getShippingValueWithTaxAmount() {
		if (_shippingValueWithTaxAmountSupplier != null) {
			shippingValueWithTaxAmount =
				_shippingValueWithTaxAmountSupplier.get();

			_shippingValueWithTaxAmountSupplier = null;
		}

		return shippingValueWithTaxAmount;
	}

	public void setShippingValueWithTaxAmount(
		Double shippingValueWithTaxAmount) {

		this.shippingValueWithTaxAmount = shippingValueWithTaxAmount;

		_shippingValueWithTaxAmountSupplier = null;
	}

	@JsonIgnore
	public void setShippingValueWithTaxAmount(
		UnsafeSupplier<Double, Exception>
			shippingValueWithTaxAmountUnsafeSupplier) {

		_shippingValueWithTaxAmountSupplier = () -> {
			try {
				return shippingValueWithTaxAmountUnsafeSupplier.get();
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
	protected Double shippingValueWithTaxAmount;

	@JsonIgnore
	private Supplier<Double> _shippingValueWithTaxAmountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getShippingValueWithTaxAmountFormatted() {
		if (_shippingValueWithTaxAmountFormattedSupplier != null) {
			shippingValueWithTaxAmountFormatted =
				_shippingValueWithTaxAmountFormattedSupplier.get();

			_shippingValueWithTaxAmountFormattedSupplier = null;
		}

		return shippingValueWithTaxAmountFormatted;
	}

	public void setShippingValueWithTaxAmountFormatted(
		String shippingValueWithTaxAmountFormatted) {

		this.shippingValueWithTaxAmountFormatted =
			shippingValueWithTaxAmountFormatted;

		_shippingValueWithTaxAmountFormattedSupplier = null;
	}

	@JsonIgnore
	public void setShippingValueWithTaxAmountFormatted(
		UnsafeSupplier<String, Exception>
			shippingValueWithTaxAmountFormattedUnsafeSupplier) {

		_shippingValueWithTaxAmountFormattedSupplier = () -> {
			try {
				return shippingValueWithTaxAmountFormattedUnsafeSupplier.get();
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
	protected String shippingValueWithTaxAmountFormatted;

	@JsonIgnore
	private Supplier<String> _shippingValueWithTaxAmountFormattedSupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double subtotalDiscountValue;

	@JsonIgnore
	private Supplier<Double> _subtotalDiscountValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getSubtotalDiscountValueFormatted() {
		if (_subtotalDiscountValueFormattedSupplier != null) {
			subtotalDiscountValueFormatted =
				_subtotalDiscountValueFormattedSupplier.get();

			_subtotalDiscountValueFormattedSupplier = null;
		}

		return subtotalDiscountValueFormatted;
	}

	public void setSubtotalDiscountValueFormatted(
		String subtotalDiscountValueFormatted) {

		this.subtotalDiscountValueFormatted = subtotalDiscountValueFormatted;

		_subtotalDiscountValueFormattedSupplier = null;
	}

	@JsonIgnore
	public void setSubtotalDiscountValueFormatted(
		UnsafeSupplier<String, Exception>
			subtotalDiscountValueFormattedUnsafeSupplier) {

		_subtotalDiscountValueFormattedSupplier = () -> {
			try {
				return subtotalDiscountValueFormattedUnsafeSupplier.get();
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
	protected String subtotalDiscountValueFormatted;

	@JsonIgnore
	private Supplier<String> _subtotalDiscountValueFormattedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getSubtotalFormatted() {
		if (_subtotalFormattedSupplier != null) {
			subtotalFormatted = _subtotalFormattedSupplier.get();

			_subtotalFormattedSupplier = null;
		}

		return subtotalFormatted;
	}

	public void setSubtotalFormatted(String subtotalFormatted) {
		this.subtotalFormatted = subtotalFormatted;

		_subtotalFormattedSupplier = null;
	}

	@JsonIgnore
	public void setSubtotalFormatted(
		UnsafeSupplier<String, Exception> subtotalFormattedUnsafeSupplier) {

		_subtotalFormattedSupplier = () -> {
			try {
				return subtotalFormattedUnsafeSupplier.get();
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
	protected String subtotalFormatted;

	@JsonIgnore
	private Supplier<String> _subtotalFormattedSupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double taxValue;

	@JsonIgnore
	private Supplier<Double> _taxValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTaxValueFormatted() {
		if (_taxValueFormattedSupplier != null) {
			taxValueFormatted = _taxValueFormattedSupplier.get();

			_taxValueFormattedSupplier = null;
		}

		return taxValueFormatted;
	}

	public void setTaxValueFormatted(String taxValueFormatted) {
		this.taxValueFormatted = taxValueFormatted;

		_taxValueFormattedSupplier = null;
	}

	@JsonIgnore
	public void setTaxValueFormatted(
		UnsafeSupplier<String, Exception> taxValueFormattedUnsafeSupplier) {

		_taxValueFormattedSupplier = () -> {
			try {
				return taxValueFormattedUnsafeSupplier.get();
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
	protected String taxValueFormatted;

	@JsonIgnore
	private Supplier<String> _taxValueFormattedSupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double totalDiscountValue;

	@JsonIgnore
	private Supplier<Double> _totalDiscountValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTotalDiscountValueFormatted() {
		if (_totalDiscountValueFormattedSupplier != null) {
			totalDiscountValueFormatted =
				_totalDiscountValueFormattedSupplier.get();

			_totalDiscountValueFormattedSupplier = null;
		}

		return totalDiscountValueFormatted;
	}

	public void setTotalDiscountValueFormatted(
		String totalDiscountValueFormatted) {

		this.totalDiscountValueFormatted = totalDiscountValueFormatted;

		_totalDiscountValueFormattedSupplier = null;
	}

	@JsonIgnore
	public void setTotalDiscountValueFormatted(
		UnsafeSupplier<String, Exception>
			totalDiscountValueFormattedUnsafeSupplier) {

		_totalDiscountValueFormattedSupplier = () -> {
			try {
				return totalDiscountValueFormattedUnsafeSupplier.get();
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
	protected String totalDiscountValueFormatted;

	@JsonIgnore
	private Supplier<String> _totalDiscountValueFormattedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTotalFormatted() {
		if (_totalFormattedSupplier != null) {
			totalFormatted = _totalFormattedSupplier.get();

			_totalFormattedSupplier = null;
		}

		return totalFormatted;
	}

	public void setTotalFormatted(String totalFormatted) {
		this.totalFormatted = totalFormatted;

		_totalFormattedSupplier = null;
	}

	@JsonIgnore
	public void setTotalFormatted(
		UnsafeSupplier<String, Exception> totalFormattedUnsafeSupplier) {

		_totalFormattedSupplier = () -> {
			try {
				return totalFormattedUnsafeSupplier.get();
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
	protected String totalFormatted;

	@JsonIgnore
	private Supplier<String> _totalFormattedSupplier;

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

		BigDecimal itemsQuantity = getItemsQuantity();

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

		String shippingDiscountValueFormatted =
			getShippingDiscountValueFormatted();

		if (shippingDiscountValueFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingDiscountValueFormatted\": ");

			sb.append("\"");

			sb.append(_escape(shippingDiscountValueFormatted));

			sb.append("\"");
		}

		Double shippingValue = getShippingValue();

		if (shippingValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingValue\": ");

			sb.append(shippingValue);
		}

		String shippingValueFormatted = getShippingValueFormatted();

		if (shippingValueFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingValueFormatted\": ");

			sb.append("\"");

			sb.append(_escape(shippingValueFormatted));

			sb.append("\"");
		}

		Double shippingValueWithTaxAmount = getShippingValueWithTaxAmount();

		if (shippingValueWithTaxAmount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingValueWithTaxAmount\": ");

			sb.append(shippingValueWithTaxAmount);
		}

		String shippingValueWithTaxAmountFormatted =
			getShippingValueWithTaxAmountFormatted();

		if (shippingValueWithTaxAmountFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingValueWithTaxAmountFormatted\": ");

			sb.append("\"");

			sb.append(_escape(shippingValueWithTaxAmountFormatted));

			sb.append("\"");
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

		String subtotalDiscountValueFormatted =
			getSubtotalDiscountValueFormatted();

		if (subtotalDiscountValueFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalDiscountValueFormatted\": ");

			sb.append("\"");

			sb.append(_escape(subtotalDiscountValueFormatted));

			sb.append("\"");
		}

		String subtotalFormatted = getSubtotalFormatted();

		if (subtotalFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtotalFormatted\": ");

			sb.append("\"");

			sb.append(_escape(subtotalFormatted));

			sb.append("\"");
		}

		Double taxValue = getTaxValue();

		if (taxValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxValue\": ");

			sb.append(taxValue);
		}

		String taxValueFormatted = getTaxValueFormatted();

		if (taxValueFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxValueFormatted\": ");

			sb.append("\"");

			sb.append(_escape(taxValueFormatted));

			sb.append("\"");
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

		String totalDiscountValueFormatted = getTotalDiscountValueFormatted();

		if (totalDiscountValueFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalDiscountValueFormatted\": ");

			sb.append("\"");

			sb.append(_escape(totalDiscountValueFormatted));

			sb.append("\"");
		}

		String totalFormatted = getTotalFormatted();

		if (totalFormatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalFormatted\": ");

			sb.append("\"");

			sb.append(_escape(totalFormatted));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.delivery.order.dto.v1_0.Summary",
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