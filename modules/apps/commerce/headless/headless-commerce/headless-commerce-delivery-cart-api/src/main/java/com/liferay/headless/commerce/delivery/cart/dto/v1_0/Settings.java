/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.dto.v1_0;

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
@GraphQLName("Settings")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Settings")
public class Settings implements Serializable {

	public static Settings toDTO(String json) {
		return ObjectMapperUtil.readValue(Settings.class, json);
	}

	public static Settings unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Settings.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public BigDecimal[] getAllowedQuantities() {
		if (_allowedQuantitiesSupplier != null) {
			allowedQuantities = _allowedQuantitiesSupplier.get();

			_allowedQuantitiesSupplier = null;
		}

		return allowedQuantities;
	}

	public void setAllowedQuantities(BigDecimal[] allowedQuantities) {
		this.allowedQuantities = allowedQuantities;

		_allowedQuantitiesSupplier = null;
	}

	@JsonIgnore
	public void setAllowedQuantities(
		UnsafeSupplier<BigDecimal[], Exception>
			allowedQuantitiesUnsafeSupplier) {

		_allowedQuantitiesSupplier = () -> {
			try {
				return allowedQuantitiesUnsafeSupplier.get();
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
	protected BigDecimal[] allowedQuantities;

	@JsonIgnore
	private Supplier<BigDecimal[]> _allowedQuantitiesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "10.1")
	@Valid
	public BigDecimal getMaxQuantity() {
		if (_maxQuantitySupplier != null) {
			maxQuantity = _maxQuantitySupplier.get();

			_maxQuantitySupplier = null;
		}

		return maxQuantity;
	}

	public void setMaxQuantity(BigDecimal maxQuantity) {
		this.maxQuantity = maxQuantity;

		_maxQuantitySupplier = null;
	}

	@JsonIgnore
	public void setMaxQuantity(
		UnsafeSupplier<BigDecimal, Exception> maxQuantityUnsafeSupplier) {

		_maxQuantitySupplier = () -> {
			try {
				return maxQuantityUnsafeSupplier.get();
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
	protected BigDecimal maxQuantity;

	@JsonIgnore
	private Supplier<BigDecimal> _maxQuantitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "10.1")
	@Valid
	public BigDecimal getMinQuantity() {
		if (_minQuantitySupplier != null) {
			minQuantity = _minQuantitySupplier.get();

			_minQuantitySupplier = null;
		}

		return minQuantity;
	}

	public void setMinQuantity(BigDecimal minQuantity) {
		this.minQuantity = minQuantity;

		_minQuantitySupplier = null;
	}

	@JsonIgnore
	public void setMinQuantity(
		UnsafeSupplier<BigDecimal, Exception> minQuantityUnsafeSupplier) {

		_minQuantitySupplier = () -> {
			try {
				return minQuantityUnsafeSupplier.get();
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
	protected BigDecimal minQuantity;

	@JsonIgnore
	private Supplier<BigDecimal> _minQuantitySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(example = "10.1")
	@Valid
	public BigDecimal getMultipleQuantity() {
		if (_multipleQuantitySupplier != null) {
			multipleQuantity = _multipleQuantitySupplier.get();

			_multipleQuantitySupplier = null;
		}

		return multipleQuantity;
	}

	public void setMultipleQuantity(BigDecimal multipleQuantity) {
		this.multipleQuantity = multipleQuantity;

		_multipleQuantitySupplier = null;
	}

	@JsonIgnore
	public void setMultipleQuantity(
		UnsafeSupplier<BigDecimal, Exception> multipleQuantityUnsafeSupplier) {

		_multipleQuantitySupplier = () -> {
			try {
				return multipleQuantityUnsafeSupplier.get();
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
	protected BigDecimal multipleQuantity;

	@JsonIgnore
	private Supplier<BigDecimal> _multipleQuantitySupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Settings)) {
			return false;
		}

		Settings settings = (Settings)object;

		return Objects.equals(toString(), settings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		BigDecimal[] allowedQuantities = getAllowedQuantities();

		if (allowedQuantities != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"allowedQuantities\": ");

			sb.append("[");

			for (int i = 0; i < allowedQuantities.length; i++) {
				sb.append(allowedQuantities[i]);

				if ((i + 1) < allowedQuantities.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		BigDecimal maxQuantity = getMaxQuantity();

		if (maxQuantity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maxQuantity\": ");

			sb.append(maxQuantity);
		}

		BigDecimal minQuantity = getMinQuantity();

		if (minQuantity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"minQuantity\": ");

			sb.append(minQuantity);
		}

		BigDecimal multipleQuantity = getMultipleQuantity();

		if (multipleQuantity != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"multipleQuantity\": ");

			sb.append(multipleQuantity);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.delivery.cart.dto.v1_0.Settings",
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