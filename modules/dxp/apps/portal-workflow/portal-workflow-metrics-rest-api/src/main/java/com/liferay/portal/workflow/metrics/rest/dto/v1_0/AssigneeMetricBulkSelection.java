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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "https://schema.org/AssigneeMetricBulkSelection",
	value = "AssigneeMetricBulkSelection"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "AssigneeMetricBulkSelection")
public class AssigneeMetricBulkSelection implements Serializable {

	public static AssigneeMetricBulkSelection toDTO(String json) {
		return ObjectMapperUtil.readValue(
			AssigneeMetricBulkSelection.class, json);
	}

	public static AssigneeMetricBulkSelection unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			AssigneeMetricBulkSelection.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getCompleted() {
		if (_completedSupplier != null) {
			completed = _completedSupplier.get();

			_completedSupplier = null;
		}

		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;

		_completedSupplier = null;
	}

	@JsonIgnore
	public void setCompleted(
		UnsafeSupplier<Boolean, Exception> completedUnsafeSupplier) {

		_completedSupplier = () -> {
			try {
				return completedUnsafeSupplier.get();
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
	protected Boolean completed;

	@JsonIgnore
	private Supplier<Boolean> _completedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getDateEnd() {
		if (_dateEndSupplier != null) {
			dateEnd = _dateEndSupplier.get();

			_dateEndSupplier = null;
		}

		return dateEnd;
	}

	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;

		_dateEndSupplier = null;
	}

	@JsonIgnore
	public void setDateEnd(
		UnsafeSupplier<Date, Exception> dateEndUnsafeSupplier) {

		_dateEndSupplier = () -> {
			try {
				return dateEndUnsafeSupplier.get();
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
	protected Date dateEnd;

	@JsonIgnore
	private Supplier<Date> _dateEndSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getDateStart() {
		if (_dateStartSupplier != null) {
			dateStart = _dateStartSupplier.get();

			_dateStartSupplier = null;
		}

		return dateStart;
	}

	public void setDateStart(Date dateStart) {
		this.dateStart = dateStart;

		_dateStartSupplier = null;
	}

	@JsonIgnore
	public void setDateStart(
		UnsafeSupplier<Date, Exception> dateStartUnsafeSupplier) {

		_dateStartSupplier = () -> {
			try {
				return dateStartUnsafeSupplier.get();
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
	protected Date dateStart;

	@JsonIgnore
	private Supplier<Date> _dateStartSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long[] getInstanceIds() {
		if (_instanceIdsSupplier != null) {
			instanceIds = _instanceIdsSupplier.get();

			_instanceIdsSupplier = null;
		}

		return instanceIds;
	}

	public void setInstanceIds(Long[] instanceIds) {
		this.instanceIds = instanceIds;

		_instanceIdsSupplier = null;
	}

	@JsonIgnore
	public void setInstanceIds(
		UnsafeSupplier<Long[], Exception> instanceIdsUnsafeSupplier) {

		_instanceIdsSupplier = () -> {
			try {
				return instanceIdsUnsafeSupplier.get();
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
	protected Long[] instanceIds;

	@JsonIgnore
	private Supplier<Long[]> _instanceIdsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getKeywords() {
		if (_keywordsSupplier != null) {
			keywords = _keywordsSupplier.get();

			_keywordsSupplier = null;
		}

		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;

		_keywordsSupplier = null;
	}

	@JsonIgnore
	public void setKeywords(
		UnsafeSupplier<String, Exception> keywordsUnsafeSupplier) {

		_keywordsSupplier = () -> {
			try {
				return keywordsUnsafeSupplier.get();
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
	protected String keywords;

	@JsonIgnore
	private Supplier<String> _keywordsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long[] getRoleIds() {
		if (_roleIdsSupplier != null) {
			roleIds = _roleIdsSupplier.get();

			_roleIdsSupplier = null;
		}

		return roleIds;
	}

	public void setRoleIds(Long[] roleIds) {
		this.roleIds = roleIds;

		_roleIdsSupplier = null;
	}

	@JsonIgnore
	public void setRoleIds(
		UnsafeSupplier<Long[], Exception> roleIdsUnsafeSupplier) {

		_roleIdsSupplier = () -> {
			try {
				return roleIdsUnsafeSupplier.get();
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
	protected Long[] roleIds;

	@JsonIgnore
	private Supplier<Long[]> _roleIdsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getTaskNames() {
		if (_taskNamesSupplier != null) {
			taskNames = _taskNamesSupplier.get();

			_taskNamesSupplier = null;
		}

		return taskNames;
	}

	public void setTaskNames(String[] taskNames) {
		this.taskNames = taskNames;

		_taskNamesSupplier = null;
	}

	@JsonIgnore
	public void setTaskNames(
		UnsafeSupplier<String[], Exception> taskNamesUnsafeSupplier) {

		_taskNamesSupplier = () -> {
			try {
				return taskNamesUnsafeSupplier.get();
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
	protected String[] taskNames;

	@JsonIgnore
	private Supplier<String[]> _taskNamesSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AssigneeMetricBulkSelection)) {
			return false;
		}

		AssigneeMetricBulkSelection assigneeMetricBulkSelection =
			(AssigneeMetricBulkSelection)object;

		return Objects.equals(
			toString(), assigneeMetricBulkSelection.toString());
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

		Boolean completed = getCompleted();

		if (completed != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"completed\": ");

			sb.append(completed);
		}

		Date dateEnd = getDateEnd();

		if (dateEnd != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateEnd\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateEnd));

			sb.append("\"");
		}

		Date dateStart = getDateStart();

		if (dateStart != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateStart\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateStart));

			sb.append("\"");
		}

		Long[] instanceIds = getInstanceIds();

		if (instanceIds != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"instanceIds\": ");

			sb.append("[");

			for (int i = 0; i < instanceIds.length; i++) {
				sb.append(instanceIds[i]);

				if ((i + 1) < instanceIds.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String keywords = getKeywords();

		if (keywords != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keywords\": ");

			sb.append("\"");

			sb.append(_escape(keywords));

			sb.append("\"");
		}

		Long[] roleIds = getRoleIds();

		if (roleIds != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"roleIds\": ");

			sb.append("[");

			for (int i = 0; i < roleIds.length; i++) {
				sb.append(roleIds[i]);

				if ((i + 1) < roleIds.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String[] taskNames = getTaskNames();

		if (taskNames != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taskNames\": ");

			sb.append("[");

			for (int i = 0; i < taskNames.length; i++) {
				sb.append("\"");

				sb.append(_escape(taskNames[i]));

				sb.append("\"");

				if ((i + 1) < taskNames.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.portal.workflow.metrics.rest.dto.v1_0.AssigneeMetricBulkSelection",
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