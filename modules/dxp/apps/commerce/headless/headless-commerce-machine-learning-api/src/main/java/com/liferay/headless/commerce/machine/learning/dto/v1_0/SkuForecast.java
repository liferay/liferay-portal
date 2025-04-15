/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.dto.v1_0;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Riccardo Ferrari
 * @generated
 */
@Generated("")
@GraphQLName("SkuForecast")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "SkuForecast")
public class SkuForecast implements Serializable {

	public static SkuForecast toDTO(String json) {
		return ObjectMapperUtil.readValue(SkuForecast.class, json);
	}

	public static SkuForecast unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(SkuForecast.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Float getActual() {
		if (_actualSupplier != null) {
			actual = _actualSupplier.get();

			_actualSupplier = null;
		}

		return actual;
	}

	public void setActual(Float actual) {
		this.actual = actual;

		_actualSupplier = null;
	}

	@JsonIgnore
	public void setActual(
		UnsafeSupplier<Float, Exception> actualUnsafeSupplier) {

		_actualSupplier = () -> {
			try {
				return actualUnsafeSupplier.get();
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
	protected Float actual;

	@JsonIgnore
	private Supplier<Float> _actualSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Float getForecast() {
		if (_forecastSupplier != null) {
			forecast = _forecastSupplier.get();

			_forecastSupplier = null;
		}

		return forecast;
	}

	public void setForecast(Float forecast) {
		this.forecast = forecast;

		_forecastSupplier = null;
	}

	@JsonIgnore
	public void setForecast(
		UnsafeSupplier<Float, Exception> forecastUnsafeSupplier) {

		_forecastSupplier = () -> {
			try {
				return forecastUnsafeSupplier.get();
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
	protected Float forecast;

	@JsonIgnore
	private Supplier<Float> _forecastSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Float getForecastLowerBound() {
		if (_forecastLowerBoundSupplier != null) {
			forecastLowerBound = _forecastLowerBoundSupplier.get();

			_forecastLowerBoundSupplier = null;
		}

		return forecastLowerBound;
	}

	public void setForecastLowerBound(Float forecastLowerBound) {
		this.forecastLowerBound = forecastLowerBound;

		_forecastLowerBoundSupplier = null;
	}

	@JsonIgnore
	public void setForecastLowerBound(
		UnsafeSupplier<Float, Exception> forecastLowerBoundUnsafeSupplier) {

		_forecastLowerBoundSupplier = () -> {
			try {
				return forecastLowerBoundUnsafeSupplier.get();
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
	protected Float forecastLowerBound;

	@JsonIgnore
	private Supplier<Float> _forecastLowerBoundSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Float getForecastUpperBound() {
		if (_forecastUpperBoundSupplier != null) {
			forecastUpperBound = _forecastUpperBoundSupplier.get();

			_forecastUpperBoundSupplier = null;
		}

		return forecastUpperBound;
	}

	public void setForecastUpperBound(Float forecastUpperBound) {
		this.forecastUpperBound = forecastUpperBound;

		_forecastUpperBoundSupplier = null;
	}

	@JsonIgnore
	public void setForecastUpperBound(
		UnsafeSupplier<Float, Exception> forecastUpperBoundUnsafeSupplier) {

		_forecastUpperBoundSupplier = () -> {
			try {
				return forecastUpperBoundUnsafeSupplier.get();
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
	protected Float forecastUpperBound;

	@JsonIgnore
	private Supplier<Float> _forecastUpperBoundSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getSku() {
		if (_skuSupplier != null) {
			sku = _skuSupplier.get();

			_skuSupplier = null;
		}

		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;

		_skuSupplier = null;
	}

	@JsonIgnore
	public void setSku(UnsafeSupplier<String, Exception> skuUnsafeSupplier) {
		_skuSupplier = () -> {
			try {
				return skuUnsafeSupplier.get();
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
	protected String sku;

	@JsonIgnore
	private Supplier<String> _skuSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getTimestamp() {
		if (_timestampSupplier != null) {
			timestamp = _timestampSupplier.get();

			_timestampSupplier = null;
		}

		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;

		_timestampSupplier = null;
	}

	@JsonIgnore
	public void setTimestamp(
		UnsafeSupplier<Date, Exception> timestampUnsafeSupplier) {

		_timestampSupplier = () -> {
			try {
				return timestampUnsafeSupplier.get();
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
	protected Date timestamp;

	@JsonIgnore
	private Supplier<Date> _timestampSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getUnit() {
		if (_unitSupplier != null) {
			unit = _unitSupplier.get();

			_unitSupplier = null;
		}

		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;

		_unitSupplier = null;
	}

	@JsonIgnore
	public void setUnit(UnsafeSupplier<String, Exception> unitUnsafeSupplier) {
		_unitSupplier = () -> {
			try {
				return unitUnsafeSupplier.get();
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
	protected String unit;

	@JsonIgnore
	private Supplier<String> _unitSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SkuForecast)) {
			return false;
		}

		SkuForecast skuForecast = (SkuForecast)object;

		return Objects.equals(toString(), skuForecast.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		Float actual = getActual();

		if (actual != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actual\": ");

			sb.append(actual);
		}

		Float forecast = getForecast();

		if (forecast != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"forecast\": ");

			sb.append(forecast);
		}

		Float forecastLowerBound = getForecastLowerBound();

		if (forecastLowerBound != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"forecastLowerBound\": ");

			sb.append(forecastLowerBound);
		}

		Float forecastUpperBound = getForecastUpperBound();

		if (forecastUpperBound != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"forecastUpperBound\": ");

			sb.append(forecastUpperBound);
		}

		String sku = getSku();

		if (sku != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sku\": ");

			sb.append("\"");

			sb.append(_escape(sku));

			sb.append("\"");
		}

		Date timestamp = getTimestamp();

		if (timestamp != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"timestamp\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(timestamp));

			sb.append("\"");
		}

		String unit = getUnit();

		if (unit != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unit\": ");

			sb.append("\"");

			sb.append(_escape(unit));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.machine.learning.dto.v1_0.SkuForecast",
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