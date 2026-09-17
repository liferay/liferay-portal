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
	description = "Event and session totals for a single account or individual over the selected date range, each with the previous period's total, a trend, and a time series bucketed by the requested interval. Use `getWorkspaceGroupChannelAccountEventMetric` or `getWorkspaceGroupChannelIndividualEventMetric` to retrieve it.",
	value = "EventMetric"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Event and session totals for a single account or individual over the selected date range, each with the previous period's total, a trend, and a time series bucketed by the requested interval. Use `getWorkspaceGroupChannelAccountEventMetric` or `getWorkspaceGroupChannelIndividualEventMetric` to retrieve it."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "EventMetric")
public class EventMetric implements Serializable {

	public static EventMetric toDTO(String json) {
		return ObjectMapperUtil.readValue(EventMetric.class, json);
	}

	public static EventMetric unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(EventMetric.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Metric getTotalEvents() {
		if (_totalEventsSupplier != null) {
			totalEvents = _totalEventsSupplier.get();

			_totalEventsSupplier = null;
		}

		return totalEvents;
	}

	public void setTotalEvents(Metric totalEvents) {
		this.totalEvents = totalEvents;

		_totalEventsSupplier = null;
	}

	@JsonIgnore
	public void setTotalEvents(
		UnsafeSupplier<Metric, Exception> totalEventsUnsafeSupplier) {

		_totalEventsSupplier = () -> {
			try {
				return totalEventsUnsafeSupplier.get();
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
	protected Metric totalEvents;

	@JsonIgnore
	private Supplier<Metric> _totalEventsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Metric getTotalSessions() {
		if (_totalSessionsSupplier != null) {
			totalSessions = _totalSessionsSupplier.get();

			_totalSessionsSupplier = null;
		}

		return totalSessions;
	}

	public void setTotalSessions(Metric totalSessions) {
		this.totalSessions = totalSessions;

		_totalSessionsSupplier = null;
	}

	@JsonIgnore
	public void setTotalSessions(
		UnsafeSupplier<Metric, Exception> totalSessionsUnsafeSupplier) {

		_totalSessionsSupplier = () -> {
			try {
				return totalSessionsUnsafeSupplier.get();
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
	protected Metric totalSessions;

	@JsonIgnore
	private Supplier<Metric> _totalSessionsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof EventMetric)) {
			return false;
		}

		EventMetric eventMetric = (EventMetric)object;

		return Objects.equals(toString(), eventMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Metric totalEvents = getTotalEvents();

		if (totalEvents != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalEvents\": ");

			sb.append(String.valueOf(totalEvents));
		}

		Metric totalSessions = getTotalSessions();

		if (totalSessions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalSessions\": ");

			sb.append(String.valueOf(totalSessions));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.osb.faro.rest.dto.v1_0.EventMetric",
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
// LIFERAY-REST-BUILDER-HASH:1122599641