/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.dto.v1_0;

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

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Marcos Martins
 * @generated
 */
@Generated("")
@GraphQLName("Metric")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Metric")
public class Metric implements Serializable {

	public static Metric toDTO(String json) {
		return ObjectMapperUtil.readValue(Metric.class, json);
	}

	public static Metric unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Metric.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getMetricType() {
		if (_metricTypeSupplier != null) {
			metricType = _metricTypeSupplier.get();

			_metricTypeSupplier = null;
		}

		return metricType;
	}

	public void setMetricType(String metricType) {
		this.metricType = metricType;

		_metricTypeSupplier = null;
	}

	@JsonIgnore
	public void setMetricType(
		UnsafeSupplier<String, Exception> metricTypeUnsafeSupplier) {

		_metricTypeSupplier = () -> {
			try {
				return metricTypeUnsafeSupplier.get();
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
	protected String metricType;

	@JsonIgnore
	private Supplier<String> _metricTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getPreviousValue() {
		if (_previousValueSupplier != null) {
			previousValue = _previousValueSupplier.get();

			_previousValueSupplier = null;
		}

		return previousValue;
	}

	public void setPreviousValue(Double previousValue) {
		this.previousValue = previousValue;

		_previousValueSupplier = null;
	}

	@JsonIgnore
	public void setPreviousValue(
		UnsafeSupplier<Double, Exception> previousValueUnsafeSupplier) {

		_previousValueSupplier = () -> {
			try {
				return previousValueUnsafeSupplier.get();
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
	protected Double previousValue;

	@JsonIgnore
	private Supplier<Double> _previousValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getPreviousValueKey() {
		if (_previousValueKeySupplier != null) {
			previousValueKey = _previousValueKeySupplier.get();

			_previousValueKeySupplier = null;
		}

		return previousValueKey;
	}

	public void setPreviousValueKey(String previousValueKey) {
		this.previousValueKey = previousValueKey;

		_previousValueKeySupplier = null;
	}

	@JsonIgnore
	public void setPreviousValueKey(
		UnsafeSupplier<String, Exception> previousValueKeyUnsafeSupplier) {

		_previousValueKeySupplier = () -> {
			try {
				return previousValueKeyUnsafeSupplier.get();
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
	protected String previousValueKey;

	@JsonIgnore
	private Supplier<String> _previousValueKeySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Trend getTrend() {
		if (_trendSupplier != null) {
			trend = _trendSupplier.get();

			_trendSupplier = null;
		}

		return trend;
	}

	public void setTrend(Trend trend) {
		this.trend = trend;

		_trendSupplier = null;
	}

	@JsonIgnore
	public void setTrend(UnsafeSupplier<Trend, Exception> trendUnsafeSupplier) {
		_trendSupplier = () -> {
			try {
				return trendUnsafeSupplier.get();
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
	protected Trend trend;

	@JsonIgnore
	private Supplier<Trend> _trendSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getValue() {
		if (_valueSupplier != null) {
			value = _valueSupplier.get();

			_valueSupplier = null;
		}

		return value;
	}

	public void setValue(Double value) {
		this.value = value;

		_valueSupplier = null;
	}

	@JsonIgnore
	public void setValue(
		UnsafeSupplier<Double, Exception> valueUnsafeSupplier) {

		_valueSupplier = () -> {
			try {
				return valueUnsafeSupplier.get();
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
	protected Double value;

	@JsonIgnore
	private Supplier<Double> _valueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getValueKey() {
		if (_valueKeySupplier != null) {
			valueKey = _valueKeySupplier.get();

			_valueKeySupplier = null;
		}

		return valueKey;
	}

	public void setValueKey(String valueKey) {
		this.valueKey = valueKey;

		_valueKeySupplier = null;
	}

	@JsonIgnore
	public void setValueKey(
		UnsafeSupplier<String, Exception> valueKeyUnsafeSupplier) {

		_valueKeySupplier = () -> {
			try {
				return valueKeyUnsafeSupplier.get();
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
	protected String valueKey;

	@JsonIgnore
	private Supplier<String> _valueKeySupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Metric)) {
			return false;
		}

		Metric metric = (Metric)object;

		return Objects.equals(toString(), metric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String metricType = getMetricType();

		if (metricType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metricType\": ");

			sb.append("\"");

			sb.append(_escape(metricType));

			sb.append("\"");
		}

		Double previousValue = getPreviousValue();

		if (previousValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"previousValue\": ");

			sb.append(previousValue);
		}

		String previousValueKey = getPreviousValueKey();

		if (previousValueKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"previousValueKey\": ");

			sb.append("\"");

			sb.append(_escape(previousValueKey));

			sb.append("\"");
		}

		Trend trend = getTrend();

		if (trend != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"trend\": ");

			sb.append(String.valueOf(trend));
		}

		Double value = getValue();

		if (value != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			sb.append(value);
		}

		String valueKey = getValueKey();

		if (valueKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"valueKey\": ");

			sb.append("\"");

			sb.append(_escape(valueKey));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.analytics.reports.rest.dto.v1_0.Metric",
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