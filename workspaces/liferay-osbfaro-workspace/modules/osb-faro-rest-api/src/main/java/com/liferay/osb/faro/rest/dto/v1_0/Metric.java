/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.dto.v1_0;

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
 * @author Leslie Wong
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "A single aggregated metric over the selected date range, compared to the previous range of the same length, with a time series bucketed by the requested interval.",
	value = "Metric"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A single aggregated metric over the selected date range, compared to the previous range of the same length, with a time series bucketed by the requested interval."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Metric")
public class Metric implements Serializable {

	public static Metric toDTO(String json) {
		return ObjectMapperUtil.readValue(Metric.class, json);
	}

	public static Metric unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Metric.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Time series of the metric across the selected date range, one bucket per interval."
	)
	@Valid
	public HistogramBucket[] getHistogramBuckets() {
		if (_histogramBucketsSupplier != null) {
			histogramBuckets = _histogramBucketsSupplier.get();

			_histogramBucketsSupplier = null;
		}

		return histogramBuckets;
	}

	public void setHistogramBuckets(HistogramBucket[] histogramBuckets) {
		this.histogramBuckets = histogramBuckets;

		_histogramBucketsSupplier = null;
	}

	@JsonIgnore
	public void setHistogramBuckets(
		UnsafeSupplier<HistogramBucket[], Exception>
			histogramBucketsUnsafeSupplier) {

		_histogramBucketsSupplier = () -> {
			try {
				return histogramBucketsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Time series of the metric across the selected date range, one bucket per interval."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected HistogramBucket[] histogramBuckets;

	@JsonIgnore
	private Supplier<HistogramBucket[]> _histogramBucketsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Total over the previous date range of the same length."
	)
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

	@GraphQLField(
		description = "Total over the previous date range of the same length."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double previousValue;

	@JsonIgnore
	private Supplier<Double> _previousValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Direction of the change from the previous range (e.g. POSITIVE, NEGATIVE, NEUTRAL). The set of values is defined by the analytics engine."
	)
	public String getTrendClassification() {
		if (_trendClassificationSupplier != null) {
			trendClassification = _trendClassificationSupplier.get();

			_trendClassificationSupplier = null;
		}

		return trendClassification;
	}

	public void setTrendClassification(String trendClassification) {
		this.trendClassification = trendClassification;

		_trendClassificationSupplier = null;
	}

	@JsonIgnore
	public void setTrendClassification(
		UnsafeSupplier<String, Exception> trendClassificationUnsafeSupplier) {

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

	@GraphQLField(
		description = "Direction of the change from the previous range (e.g. POSITIVE, NEGATIVE, NEUTRAL). The set of values is defined by the analytics engine."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String trendClassification;

	@JsonIgnore
	private Supplier<String> _trendClassificationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Percentage change from the previous date range."
	)
	public Double getTrendPercentage() {
		if (_trendPercentageSupplier != null) {
			trendPercentage = _trendPercentageSupplier.get();

			_trendPercentageSupplier = null;
		}

		return trendPercentage;
	}

	public void setTrendPercentage(Double trendPercentage) {
		this.trendPercentage = trendPercentage;

		_trendPercentageSupplier = null;
	}

	@JsonIgnore
	public void setTrendPercentage(
		UnsafeSupplier<Double, Exception> trendPercentageUnsafeSupplier) {

		_trendPercentageSupplier = () -> {
			try {
				return trendPercentageUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "Percentage change from the previous date range."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double trendPercentage;

	@JsonIgnore
	private Supplier<Double> _trendPercentageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Total over the selected date range."
	)
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

	@GraphQLField(description = "Total over the selected date range.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Double value;

	@JsonIgnore
	private Supplier<Double> _valueSupplier;

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

		HistogramBucket[] histogramBuckets = getHistogramBuckets();

		if (histogramBuckets != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"histogramBuckets\": ");

			sb.append("[");

			for (int i = 0; i < histogramBuckets.length; i++) {
				sb.append(String.valueOf(histogramBuckets[i]));

				if ((i + 1) < histogramBuckets.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Double previousValue = getPreviousValue();

		if (previousValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"previousValue\": ");

			sb.append(previousValue);
		}

		String trendClassification = getTrendClassification();

		if (trendClassification != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"trendClassification\": ");

			sb.append("\"");

			sb.append(_escape(trendClassification));

			sb.append("\"");
		}

		Double trendPercentage = getTrendPercentage();

		if (trendPercentage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"trendPercentage\": ");

			sb.append(trendPercentage);
		}

		Double value = getValue();

		if (value != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"value\": ");

			sb.append(value);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.osb.faro.rest.dto.v1_0.Metric",
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
// LIFERAY-REST-BUILDER-HASH:1665257887