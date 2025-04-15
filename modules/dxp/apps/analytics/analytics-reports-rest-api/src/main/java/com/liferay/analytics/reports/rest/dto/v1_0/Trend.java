/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

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

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Marcos Martins
 * @generated
 */
@Generated("")
@GraphQLName("Trend")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Trend")
public class Trend implements Serializable {

	public static Trend toDTO(String json) {
		return ObjectMapperUtil.readValue(Trend.class, json);
	}

	public static Trend unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Trend.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Double getPercentage() {
		if (_percentageSupplier != null) {
			percentage = _percentageSupplier.get();

			_percentageSupplier = null;
		}

		return percentage;
	}

	public void setPercentage(Double percentage) {
		this.percentage = percentage;

		_percentageSupplier = null;
	}

	@JsonIgnore
	public void setPercentage(
		UnsafeSupplier<Double, Exception> percentageUnsafeSupplier) {

		_percentageSupplier = () -> {
			try {
				return percentageUnsafeSupplier.get();
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
	protected Double percentage;

	@JsonIgnore
	private Supplier<Double> _percentageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("trendClassification")
	@Valid
	public TrendClassification getTrendClassification() {
		if (_trendClassificationSupplier != null) {
			trendClassification = _trendClassificationSupplier.get();

			_trendClassificationSupplier = null;
		}

		return trendClassification;
	}

	@JsonIgnore
	public String getTrendClassificationAsString() {
		TrendClassification trendClassification = getTrendClassification();

		if (trendClassification == null) {
			return null;
		}

		return trendClassification.toString();
	}

	public void setTrendClassification(
		TrendClassification trendClassification) {

		this.trendClassification = trendClassification;

		_trendClassificationSupplier = null;
	}

	@JsonIgnore
	public void setTrendClassification(
		UnsafeSupplier<TrendClassification, Exception>
			trendClassificationUnsafeSupplier) {

		_trendClassificationSupplier = () -> {
			try {
				return trendClassificationUnsafeSupplier.get();
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
	protected TrendClassification trendClassification;

	@JsonIgnore
	private Supplier<TrendClassification> _trendClassificationSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Trend)) {
			return false;
		}

		Trend trend = (Trend)object;

		return Objects.equals(toString(), trend.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Double percentage = getPercentage();

		if (percentage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"percentage\": ");

			sb.append(percentage);
		}

		TrendClassification trendClassification = getTrendClassification();

		if (trendClassification != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"trendClassification\": ");

			sb.append("\"");

			sb.append(trendClassification);

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.analytics.reports.rest.dto.v1_0.Trend",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("TrendClassification")
	public static enum TrendClassification {

		NEGATIVE("NEGATIVE"), NEUTRAL("NEUTRAL"), POSITIVE("POSITIVE");

		@JsonCreator
		public static TrendClassification create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (TrendClassification trendClassification : values()) {
				if (Objects.equals(trendClassification.getValue(), value)) {
					return trendClassification;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private TrendClassification(String value) {
			_value = value;
		}

		private final String _value;

	}

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