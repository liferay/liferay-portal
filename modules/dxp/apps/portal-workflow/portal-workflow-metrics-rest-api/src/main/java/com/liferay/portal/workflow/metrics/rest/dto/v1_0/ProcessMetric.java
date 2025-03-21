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
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "https://www.schema.org/ProcessMetric",
	value = "ProcessMetric"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ProcessMetric")
public class ProcessMetric implements Serializable {

	public static ProcessMetric toDTO(String json) {
		return ObjectMapperUtil.readValue(ProcessMetric.class, json);
	}

	public static ProcessMetric unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(ProcessMetric.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getInstanceCount() {
		if (_instanceCountSupplier != null) {
			instanceCount = _instanceCountSupplier.get();

			_instanceCountSupplier = null;
		}

		return instanceCount;
	}

	public void setInstanceCount(Long instanceCount) {
		this.instanceCount = instanceCount;

		_instanceCountSupplier = null;
	}

	@JsonIgnore
	public void setInstanceCount(
		UnsafeSupplier<Long, Exception> instanceCountUnsafeSupplier) {

		_instanceCountSupplier = () -> {
			try {
				return instanceCountUnsafeSupplier.get();
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
	protected Long instanceCount;

	@JsonIgnore
	private Supplier<Long> _instanceCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getOnTimeInstanceCount() {
		if (_onTimeInstanceCountSupplier != null) {
			onTimeInstanceCount = _onTimeInstanceCountSupplier.get();

			_onTimeInstanceCountSupplier = null;
		}

		return onTimeInstanceCount;
	}

	public void setOnTimeInstanceCount(Long onTimeInstanceCount) {
		this.onTimeInstanceCount = onTimeInstanceCount;

		_onTimeInstanceCountSupplier = null;
	}

	@JsonIgnore
	public void setOnTimeInstanceCount(
		UnsafeSupplier<Long, Exception> onTimeInstanceCountUnsafeSupplier) {

		_onTimeInstanceCountSupplier = () -> {
			try {
				return onTimeInstanceCountUnsafeSupplier.get();
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
	protected Long onTimeInstanceCount;

	@JsonIgnore
	private Supplier<Long> _onTimeInstanceCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getOverdueInstanceCount() {
		if (_overdueInstanceCountSupplier != null) {
			overdueInstanceCount = _overdueInstanceCountSupplier.get();

			_overdueInstanceCountSupplier = null;
		}

		return overdueInstanceCount;
	}

	public void setOverdueInstanceCount(Long overdueInstanceCount) {
		this.overdueInstanceCount = overdueInstanceCount;

		_overdueInstanceCountSupplier = null;
	}

	@JsonIgnore
	public void setOverdueInstanceCount(
		UnsafeSupplier<Long, Exception> overdueInstanceCountUnsafeSupplier) {

		_overdueInstanceCountSupplier = () -> {
			try {
				return overdueInstanceCountUnsafeSupplier.get();
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
	protected Long overdueInstanceCount;

	@JsonIgnore
	private Supplier<Long> _overdueInstanceCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Process getProcess() {
		if (_processSupplier != null) {
			process = _processSupplier.get();

			_processSupplier = null;
		}

		return process;
	}

	public void setProcess(Process process) {
		this.process = process;

		_processSupplier = null;
	}

	@JsonIgnore
	public void setProcess(
		UnsafeSupplier<Process, Exception> processUnsafeSupplier) {

		_processSupplier = () -> {
			try {
				return processUnsafeSupplier.get();
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
	protected Process process;

	@JsonIgnore
	private Supplier<Process> _processSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getUntrackedInstanceCount() {
		if (_untrackedInstanceCountSupplier != null) {
			untrackedInstanceCount = _untrackedInstanceCountSupplier.get();

			_untrackedInstanceCountSupplier = null;
		}

		return untrackedInstanceCount;
	}

	public void setUntrackedInstanceCount(Long untrackedInstanceCount) {
		this.untrackedInstanceCount = untrackedInstanceCount;

		_untrackedInstanceCountSupplier = null;
	}

	@JsonIgnore
	public void setUntrackedInstanceCount(
		UnsafeSupplier<Long, Exception> untrackedInstanceCountUnsafeSupplier) {

		_untrackedInstanceCountSupplier = () -> {
			try {
				return untrackedInstanceCountUnsafeSupplier.get();
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
	protected Long untrackedInstanceCount;

	@JsonIgnore
	private Supplier<Long> _untrackedInstanceCountSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ProcessMetric)) {
			return false;
		}

		ProcessMetric processMetric = (ProcessMetric)object;

		return Objects.equals(toString(), processMetric.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Long instanceCount = getInstanceCount();

		if (instanceCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"instanceCount\": ");

			sb.append(instanceCount);
		}

		Long onTimeInstanceCount = getOnTimeInstanceCount();

		if (onTimeInstanceCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"onTimeInstanceCount\": ");

			sb.append(onTimeInstanceCount);
		}

		Long overdueInstanceCount = getOverdueInstanceCount();

		if (overdueInstanceCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"overdueInstanceCount\": ");

			sb.append(overdueInstanceCount);
		}

		Process process = getProcess();

		if (process != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"process\": ");

			sb.append(String.valueOf(process));
		}

		Long untrackedInstanceCount = getUntrackedInstanceCount();

		if (untrackedInstanceCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"untrackedInstanceCount\": ");

			sb.append(untrackedInstanceCount);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.portal.workflow.metrics.rest.dto.v1_0.ProcessMetric",
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