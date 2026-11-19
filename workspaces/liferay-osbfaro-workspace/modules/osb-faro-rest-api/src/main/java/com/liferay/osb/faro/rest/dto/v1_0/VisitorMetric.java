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
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collection;
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
	description = "A set of visitor totals of a channel over a date range. Use `getWorkspaceGroupChannelVisitorMetric` to retrieve it.",
	value = "VisitorMetric"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A set of visitor totals of a channel over a date range. Use `getWorkspaceGroupChannelVisitorMetric` to retrieve it."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "VisitorMetric")
public class VisitorMetric implements Serializable {

	public static VisitorMetric toDTO(String json) {
		return ObjectMapperUtil.readValue(VisitorMetric.class, json);
	}

	public static VisitorMetric unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(VisitorMetric.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Average duration of the sessions in the range, in milliseconds."
	)
	public Long getAverageSessionDuration() {
		if (_averageSessionDurationSupplier != null) {
			averageSessionDuration = _averageSessionDurationSupplier.get();

			_averageSessionDurationSupplier = null;
		}

		return averageSessionDuration;
	}

	public void setAverageSessionDuration(Long averageSessionDuration) {
		this.averageSessionDuration = averageSessionDuration;

		_averageSessionDurationSupplier = null;
	}

	@JsonIgnore
	public void setAverageSessionDuration(
		UnsafeSupplier<Long, Exception> averageSessionDurationUnsafeSupplier) {

		_averageSessionDurationSupplier = () -> {
			try {
				return averageSessionDurationUnsafeSupplier.get();
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
		description = "Average duration of the sessions in the range, in milliseconds."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long averageSessionDuration;

	@JsonIgnore
	private Supplier<Long> _averageSessionDurationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Visitors identified by email. Anonymous visitors are `visitors` minus `knownVisitors`."
	)
	public Long getKnownVisitors() {
		if (_knownVisitorsSupplier != null) {
			knownVisitors = _knownVisitorsSupplier.get();

			_knownVisitorsSupplier = null;
		}

		return knownVisitors;
	}

	public void setKnownVisitors(Long knownVisitors) {
		this.knownVisitors = knownVisitors;

		_knownVisitorsSupplier = null;
	}

	@JsonIgnore
	public void setKnownVisitors(
		UnsafeSupplier<Long, Exception> knownVisitorsUnsafeSupplier) {

		_knownVisitorsSupplier = () -> {
			try {
				return knownVisitorsUnsafeSupplier.get();
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
		description = "Visitors identified by email. Anonymous visitors are `visitors` minus `knownVisitors`."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long knownVisitors;

	@JsonIgnore
	private Supplier<Long> _knownVisitorsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Sum of the durations of all sessions in the range, in milliseconds."
	)
	public Long getTotalSessionDuration() {
		if (_totalSessionDurationSupplier != null) {
			totalSessionDuration = _totalSessionDurationSupplier.get();

			_totalSessionDurationSupplier = null;
		}

		return totalSessionDuration;
	}

	public void setTotalSessionDuration(Long totalSessionDuration) {
		this.totalSessionDuration = totalSessionDuration;

		_totalSessionDurationSupplier = null;
	}

	@JsonIgnore
	public void setTotalSessionDuration(
		UnsafeSupplier<Long, Exception> totalSessionDurationUnsafeSupplier) {

		_totalSessionDurationSupplier = () -> {
			try {
				return totalSessionDurationUnsafeSupplier.get();
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
		description = "Sum of the durations of all sessions in the range, in milliseconds."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long totalSessionDuration;

	@JsonIgnore
	private Supplier<Long> _totalSessionDurationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Unique visitors, known and anonymous, with at least one session in the range."
	)
	public Long getVisitors() {
		if (_visitorsSupplier != null) {
			visitors = _visitorsSupplier.get();

			_visitorsSupplier = null;
		}

		return visitors;
	}

	public void setVisitors(Long visitors) {
		this.visitors = visitors;

		_visitorsSupplier = null;
	}

	@JsonIgnore
	public void setVisitors(
		UnsafeSupplier<Long, Exception> visitorsUnsafeSupplier) {

		_visitorsSupplier = () -> {
			try {
				return visitorsUnsafeSupplier.get();
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
		description = "Unique visitors, known and anonymous, with at least one session in the range."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long visitors;

	@JsonIgnore
	private Supplier<Long> _visitorsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof VisitorMetric)) {
			return false;
		}

		VisitorMetric visitorMetric = (VisitorMetric)object;

		return Objects.equals(toString(), visitorMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Long averageSessionDuration = getAverageSessionDuration();

		if (averageSessionDuration != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"averageSessionDuration\": ");

			sb.append(averageSessionDuration);
		}

		Long knownVisitors = getKnownVisitors();

		if (knownVisitors != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"knownVisitors\": ");

			sb.append(knownVisitors);
		}

		Long totalSessionDuration = getTotalSessionDuration();

		if (totalSessionDuration != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalSessionDuration\": ");

			sb.append(totalSessionDuration);
		}

		Long visitors = getVisitors();

		if (visitors != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"visitors\": ");

			sb.append(visitors);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.osb.faro.rest.dto.v1_0.VisitorMetric",
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

	private static String _toJSON(Object value) {
		if (value instanceof Collection) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray((Collection<?>)value));
		}
		else if (value instanceof Map) {
			return String.valueOf(
				JSONFactoryUtil.createJSONObject((Map<?, ?>)value));
		}
		else if (value instanceof Object[]) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray(
					Arrays.asList((Object[])value)));
		}
		else if (value instanceof String) {
			return StringBundler.concat("\"", _escape(value), "\"");
		}

		return String.valueOf(value);
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
// LIFERAY-REST-BUILDER-HASH:-978578460