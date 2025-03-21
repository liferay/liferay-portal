/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.math.BigDecimal;

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
@GraphQLName("ProductShippingConfiguration")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ProductShippingConfiguration")
public class ProductShippingConfiguration implements Serializable {

	public static ProductShippingConfiguration toDTO(String json) {
		return ObjectMapperUtil.readValue(
			ProductShippingConfiguration.class, json);
	}

	public static ProductShippingConfiguration unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			ProductShippingConfiguration.class, json);
	}

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "202")
	@Valid
	public BigDecimal getDepth() {
		if (_depthSupplier != null) {
			depth = _depthSupplier.get();

			_depthSupplier = null;
		}

		return depth;
	}

	public void setDepth(BigDecimal depth) {
		this.depth = depth;

		_depthSupplier = null;
	}

	@JsonIgnore
	public void setDepth(
		UnsafeSupplier<BigDecimal, Exception> depthUnsafeSupplier) {

		_depthSupplier = () -> {
			try {
				return depthUnsafeSupplier.get();
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
	protected BigDecimal depth;

	@JsonIgnore
	private Supplier<BigDecimal> _depthSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getFreeShipping() {
		if (_freeShippingSupplier != null) {
			freeShipping = _freeShippingSupplier.get();

			_freeShippingSupplier = null;
		}

		return freeShipping;
	}

	public void setFreeShipping(Boolean freeShipping) {
		this.freeShipping = freeShipping;

		_freeShippingSupplier = null;
	}

	@JsonIgnore
	public void setFreeShipping(
		UnsafeSupplier<Boolean, Exception> freeShippingUnsafeSupplier) {

		_freeShippingSupplier = () -> {
			try {
				return freeShippingUnsafeSupplier.get();
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
	protected Boolean freeShipping;

	@JsonIgnore
	private Supplier<Boolean> _freeShippingSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "202")
	@Valid
	public BigDecimal getHeight() {
		if (_heightSupplier != null) {
			height = _heightSupplier.get();

			_heightSupplier = null;
		}

		return height;
	}

	public void setHeight(BigDecimal height) {
		this.height = height;

		_heightSupplier = null;
	}

	@JsonIgnore
	public void setHeight(
		UnsafeSupplier<BigDecimal, Exception> heightUnsafeSupplier) {

		_heightSupplier = () -> {
			try {
				return heightUnsafeSupplier.get();
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
	protected BigDecimal height;

	@JsonIgnore
	private Supplier<BigDecimal> _heightSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getShippable() {
		if (_shippableSupplier != null) {
			shippable = _shippableSupplier.get();

			_shippableSupplier = null;
		}

		return shippable;
	}

	public void setShippable(Boolean shippable) {
		this.shippable = shippable;

		_shippableSupplier = null;
	}

	@JsonIgnore
	public void setShippable(
		UnsafeSupplier<Boolean, Exception> shippableUnsafeSupplier) {

		_shippableSupplier = () -> {
			try {
				return shippableUnsafeSupplier.get();
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
	protected Boolean shippable;

	@JsonIgnore
	private Supplier<Boolean> _shippableSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "202")
	@Valid
	public BigDecimal getShippingExtraPrice() {
		if (_shippingExtraPriceSupplier != null) {
			shippingExtraPrice = _shippingExtraPriceSupplier.get();

			_shippingExtraPriceSupplier = null;
		}

		return shippingExtraPrice;
	}

	public void setShippingExtraPrice(BigDecimal shippingExtraPrice) {
		this.shippingExtraPrice = shippingExtraPrice;

		_shippingExtraPriceSupplier = null;
	}

	@JsonIgnore
	public void setShippingExtraPrice(
		UnsafeSupplier<BigDecimal, Exception>
			shippingExtraPriceUnsafeSupplier) {

		_shippingExtraPriceSupplier = () -> {
			try {
				return shippingExtraPriceUnsafeSupplier.get();
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
	protected BigDecimal shippingExtraPrice;

	@JsonIgnore
	private Supplier<BigDecimal> _shippingExtraPriceSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "true")
	public Boolean getShippingSeparately() {
		if (_shippingSeparatelySupplier != null) {
			shippingSeparately = _shippingSeparatelySupplier.get();

			_shippingSeparatelySupplier = null;
		}

		return shippingSeparately;
	}

	public void setShippingSeparately(Boolean shippingSeparately) {
		this.shippingSeparately = shippingSeparately;

		_shippingSeparatelySupplier = null;
	}

	@JsonIgnore
	public void setShippingSeparately(
		UnsafeSupplier<Boolean, Exception> shippingSeparatelyUnsafeSupplier) {

		_shippingSeparatelySupplier = () -> {
			try {
				return shippingSeparatelyUnsafeSupplier.get();
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
	protected Boolean shippingSeparately;

	@JsonIgnore
	private Supplier<Boolean> _shippingSeparatelySupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "202")
	@Valid
	public BigDecimal getWeight() {
		if (_weightSupplier != null) {
			weight = _weightSupplier.get();

			_weightSupplier = null;
		}

		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;

		_weightSupplier = null;
	}

	@JsonIgnore
	public void setWeight(
		UnsafeSupplier<BigDecimal, Exception> weightUnsafeSupplier) {

		_weightSupplier = () -> {
			try {
				return weightUnsafeSupplier.get();
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
	protected BigDecimal weight;

	@JsonIgnore
	private Supplier<BigDecimal> _weightSupplier;

	@DecimalMin("0")
	@io.swagger.v3.oas.annotations.media.Schema(example = "202")
	@Valid
	public BigDecimal getWidth() {
		if (_widthSupplier != null) {
			width = _widthSupplier.get();

			_widthSupplier = null;
		}

		return width;
	}

	public void setWidth(BigDecimal width) {
		this.width = width;

		_widthSupplier = null;
	}

	@JsonIgnore
	public void setWidth(
		UnsafeSupplier<BigDecimal, Exception> widthUnsafeSupplier) {

		_widthSupplier = () -> {
			try {
				return widthUnsafeSupplier.get();
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
	protected BigDecimal width;

	@JsonIgnore
	private Supplier<BigDecimal> _widthSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ProductShippingConfiguration)) {
			return false;
		}

		ProductShippingConfiguration productShippingConfiguration =
			(ProductShippingConfiguration)object;

		return Objects.equals(
			toString(), productShippingConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		BigDecimal depth = getDepth();

		if (depth != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"depth\": ");

			sb.append(depth);
		}

		Boolean freeShipping = getFreeShipping();

		if (freeShipping != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"freeShipping\": ");

			sb.append(freeShipping);
		}

		BigDecimal height = getHeight();

		if (height != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"height\": ");

			sb.append(height);
		}

		Boolean shippable = getShippable();

		if (shippable != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippable\": ");

			sb.append(shippable);
		}

		BigDecimal shippingExtraPrice = getShippingExtraPrice();

		if (shippingExtraPrice != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingExtraPrice\": ");

			sb.append(shippingExtraPrice);
		}

		Boolean shippingSeparately = getShippingSeparately();

		if (shippingSeparately != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"shippingSeparately\": ");

			sb.append(shippingSeparately);
		}

		BigDecimal weight = getWeight();

		if (weight != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"weight\": ");

			sb.append(weight);
		}

		BigDecimal width = getWidth();

		if (width != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"width\": ");

			sb.append(width);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductShippingConfiguration",
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