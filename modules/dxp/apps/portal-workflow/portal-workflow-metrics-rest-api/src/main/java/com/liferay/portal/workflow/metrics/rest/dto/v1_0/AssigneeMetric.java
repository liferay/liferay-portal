/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.dto.v1_0;

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
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "https://schema.org/AssigneeMetric", value = "AssigneeMetric"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "AssigneeMetric")
public class AssigneeMetric implements Serializable {

	public static AssigneeMetric toDTO(String json) {
		return ObjectMapperUtil.readValue(AssigneeMetric.class, json);
	}

	public static AssigneeMetric unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(AssigneeMetric.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Assignee getAssignee() {
		if (_assigneeSupplier != null) {
			assignee = _assigneeSupplier.get();

			_assigneeSupplier = null;
		}

		return assignee;
	}

	public void setAssignee(Assignee assignee) {
		this.assignee = assignee;

		_assigneeSupplier = null;
	}

	@JsonIgnore
	public void setAssignee(
		UnsafeSupplier<Assignee, Exception> assigneeUnsafeSupplier) {

		_assigneeSupplier = () -> {
			try {
				return assigneeUnsafeSupplier.get();
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
	protected Assignee assignee;

	@JsonIgnore
	private Supplier<Assignee> _assigneeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getDurationTaskAvg() {
		if (_durationTaskAvgSupplier != null) {
			durationTaskAvg = _durationTaskAvgSupplier.get();

			_durationTaskAvgSupplier = null;
		}

		return durationTaskAvg;
	}

	public void setDurationTaskAvg(Long durationTaskAvg) {
		this.durationTaskAvg = durationTaskAvg;

		_durationTaskAvgSupplier = null;
	}

	@JsonIgnore
	public void setDurationTaskAvg(
		UnsafeSupplier<Long, Exception> durationTaskAvgUnsafeSupplier) {

		_durationTaskAvgSupplier = () -> {
			try {
				return durationTaskAvgUnsafeSupplier.get();
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
	protected Long durationTaskAvg;

	@JsonIgnore
	private Supplier<Long> _durationTaskAvgSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getOnTimeTaskCount() {
		if (_onTimeTaskCountSupplier != null) {
			onTimeTaskCount = _onTimeTaskCountSupplier.get();

			_onTimeTaskCountSupplier = null;
		}

		return onTimeTaskCount;
	}

	public void setOnTimeTaskCount(Long onTimeTaskCount) {
		this.onTimeTaskCount = onTimeTaskCount;

		_onTimeTaskCountSupplier = null;
	}

	@JsonIgnore
	public void setOnTimeTaskCount(
		UnsafeSupplier<Long, Exception> onTimeTaskCountUnsafeSupplier) {

		_onTimeTaskCountSupplier = () -> {
			try {
				return onTimeTaskCountUnsafeSupplier.get();
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
	protected Long onTimeTaskCount;

	@JsonIgnore
	private Supplier<Long> _onTimeTaskCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getOverdueTaskCount() {
		if (_overdueTaskCountSupplier != null) {
			overdueTaskCount = _overdueTaskCountSupplier.get();

			_overdueTaskCountSupplier = null;
		}

		return overdueTaskCount;
	}

	public void setOverdueTaskCount(Long overdueTaskCount) {
		this.overdueTaskCount = overdueTaskCount;

		_overdueTaskCountSupplier = null;
	}

	@JsonIgnore
	public void setOverdueTaskCount(
		UnsafeSupplier<Long, Exception> overdueTaskCountUnsafeSupplier) {

		_overdueTaskCountSupplier = () -> {
			try {
				return overdueTaskCountUnsafeSupplier.get();
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
	protected Long overdueTaskCount;

	@JsonIgnore
	private Supplier<Long> _overdueTaskCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getTaskCount() {
		if (_taskCountSupplier != null) {
			taskCount = _taskCountSupplier.get();

			_taskCountSupplier = null;
		}

		return taskCount;
	}

	public void setTaskCount(Long taskCount) {
		this.taskCount = taskCount;

		_taskCountSupplier = null;
	}

	@JsonIgnore
	public void setTaskCount(
		UnsafeSupplier<Long, Exception> taskCountUnsafeSupplier) {

		_taskCountSupplier = () -> {
			try {
				return taskCountUnsafeSupplier.get();
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
	protected Long taskCount;

	@JsonIgnore
	private Supplier<Long> _taskCountSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AssigneeMetric)) {
			return false;
		}

		AssigneeMetric assigneeMetric = (AssigneeMetric)object;

		return Objects.equals(toString(), assigneeMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Assignee assignee = getAssignee();

		if (assignee != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assignee\": ");

			sb.append(String.valueOf(assignee));
		}

		Long durationTaskAvg = getDurationTaskAvg();

		if (durationTaskAvg != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"durationTaskAvg\": ");

			sb.append(durationTaskAvg);
		}

		Long onTimeTaskCount = getOnTimeTaskCount();

		if (onTimeTaskCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"onTimeTaskCount\": ");

			sb.append(onTimeTaskCount);
		}

		Long overdueTaskCount = getOverdueTaskCount();

		if (overdueTaskCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"overdueTaskCount\": ");

			sb.append(overdueTaskCount);
		}

		Long taskCount = getTaskCount();

		if (taskCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taskCount\": ");

			sb.append(taskCount);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.portal.workflow.metrics.rest.dto.v1_0.AssigneeMetric",
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