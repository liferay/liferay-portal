/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.dto.v1_0;

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
 * @author Carolina Barbosa
 * @generated
 */
@Generated("")
@GraphQLName("TaskStatistics")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "TaskStatistics")
public class TaskStatistics implements Serializable {

	public static TaskStatistics toDTO(String json) {
		return ObjectMapperUtil.readValue(TaskStatistics.class, json);
	}

	public static TaskStatistics unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(TaskStatistics.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getBlockedCount() {
		if (_blockedCountSupplier != null) {
			blockedCount = _blockedCountSupplier.get();

			_blockedCountSupplier = null;
		}

		return blockedCount;
	}

	public void setBlockedCount(Long blockedCount) {
		this.blockedCount = blockedCount;

		_blockedCountSupplier = null;
	}

	@JsonIgnore
	public void setBlockedCount(
		UnsafeSupplier<Long, Exception> blockedCountUnsafeSupplier) {

		_blockedCountSupplier = () -> {
			try {
				return blockedCountUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long blockedCount;

	@JsonIgnore
	private Supplier<Long> _blockedCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getInProgressCount() {
		if (_inProgressCountSupplier != null) {
			inProgressCount = _inProgressCountSupplier.get();

			_inProgressCountSupplier = null;
		}

		return inProgressCount;
	}

	public void setInProgressCount(Long inProgressCount) {
		this.inProgressCount = inProgressCount;

		_inProgressCountSupplier = null;
	}

	@JsonIgnore
	public void setInProgressCount(
		UnsafeSupplier<Long, Exception> inProgressCountUnsafeSupplier) {

		_inProgressCountSupplier = () -> {
			try {
				return inProgressCountUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long inProgressCount;

	@JsonIgnore
	private Supplier<Long> _inProgressCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getOverdueCount() {
		if (_overdueCountSupplier != null) {
			overdueCount = _overdueCountSupplier.get();

			_overdueCountSupplier = null;
		}

		return overdueCount;
	}

	public void setOverdueCount(Long overdueCount) {
		this.overdueCount = overdueCount;

		_overdueCountSupplier = null;
	}

	@JsonIgnore
	public void setOverdueCount(
		UnsafeSupplier<Long, Exception> overdueCountUnsafeSupplier) {

		_overdueCountSupplier = () -> {
			try {
				return overdueCountUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long overdueCount;

	@JsonIgnore
	private Supplier<Long> _overdueCountSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getTotalCount() {
		if (_totalCountSupplier != null) {
			totalCount = _totalCountSupplier.get();

			_totalCountSupplier = null;
		}

		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;

		_totalCountSupplier = null;
	}

	@JsonIgnore
	public void setTotalCount(
		UnsafeSupplier<Long, Exception> totalCountUnsafeSupplier) {

		_totalCountSupplier = () -> {
			try {
				return totalCountUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long totalCount;

	@JsonIgnore
	private Supplier<Long> _totalCountSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TaskStatistics)) {
			return false;
		}

		TaskStatistics taskStatistics = (TaskStatistics)object;

		return Objects.equals(toString(), taskStatistics.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Long blockedCount = getBlockedCount();

		if (blockedCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"blockedCount\": ");

			sb.append(blockedCount);
		}

		Long inProgressCount = getInProgressCount();

		if (inProgressCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"inProgressCount\": ");

			sb.append(inProgressCount);
		}

		Long overdueCount = getOverdueCount();

		if (overdueCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"overdueCount\": ");

			sb.append(overdueCount);
		}

		Long totalCount = getTotalCount();

		if (totalCount != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalCount\": ");

			sb.append(totalCount);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.cmp.dto.v1_0.TaskStatistics",
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
// LIFERAY-REST-BUILDER-HASH:1796453605