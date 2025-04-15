/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.dto.v1_0;

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
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "https://www.schema.org/HistogramMetric",
	value = "HistogramMetric"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "HistogramMetric")
public class HistogramMetric implements Serializable {

	public static HistogramMetric toDTO(String json) {
		return ObjectMapperUtil.readValue(HistogramMetric.class, json);
	}

	public static HistogramMetric unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(HistogramMetric.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Histogram[] getHistograms() {
		if (_histogramsSupplier != null) {
			histograms = _histogramsSupplier.get();

			_histogramsSupplier = null;
		}

		return histograms;
	}

	public void setHistograms(Histogram[] histograms) {
		this.histograms = histograms;

		_histogramsSupplier = null;
	}

	@JsonIgnore
	public void setHistograms(
		UnsafeSupplier<Histogram[], Exception> histogramsUnsafeSupplier) {

		_histogramsSupplier = () -> {
			try {
				return histogramsUnsafeSupplier.get();
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
	protected Histogram[] histograms;

	@JsonIgnore
	private Supplier<Histogram[]> _histogramsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("unit")
	@Valid
	public Unit getUnit() {
		if (_unitSupplier != null) {
			unit = _unitSupplier.get();

			_unitSupplier = null;
		}

		return unit;
	}

	@JsonIgnore
	public String getUnitAsString() {
		Unit unit = getUnit();

		if (unit == null) {
			return null;
		}

		return unit.toString();
	}

	public void setUnit(Unit unit) {
		this.unit = unit;

		_unitSupplier = null;
	}

	@JsonIgnore
	public void setUnit(UnsafeSupplier<Unit, Exception> unitUnsafeSupplier) {
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
	protected Unit unit;

	@JsonIgnore
	private Supplier<Unit> _unitSupplier;

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

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof HistogramMetric)) {
			return false;
		}

		HistogramMetric histogramMetric = (HistogramMetric)object;

		return Objects.equals(toString(), histogramMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Histogram[] histograms = getHistograms();

		if (histograms != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"histograms\": ");

			sb.append("[");

			for (int i = 0; i < histograms.length; i++) {
				sb.append(String.valueOf(histograms[i]));

				if ((i + 1) < histograms.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Unit unit = getUnit();

		if (unit != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unit\": ");

			sb.append("\"");

			sb.append(unit);

			sb.append("\"");
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
		defaultValue = "com.liferay.portal.workflow.metrics.rest.dto.v1_0.HistogramMetric",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("Unit")
	public static enum Unit {

		DAYS("Days"), HOURS("Hours"), MONTHS("Months"), WEEKS("Weeks"),
		YEARS("Years");

		@JsonCreator
		public static Unit create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Unit unit : values()) {
				if (Objects.equals(unit.getValue(), value)) {
					return unit;
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

		private Unit(String value) {
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