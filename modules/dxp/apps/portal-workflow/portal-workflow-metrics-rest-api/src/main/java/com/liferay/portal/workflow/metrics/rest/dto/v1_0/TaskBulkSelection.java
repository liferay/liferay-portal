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
	description = "https://schema.org/TaskBulkSelection",
	value = "TaskBulkSelection"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "TaskBulkSelection")
public class TaskBulkSelection implements Serializable {

	public static TaskBulkSelection toDTO(String json) {
		return ObjectMapperUtil.readValue(TaskBulkSelection.class, json);
	}

	public static TaskBulkSelection unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(TaskBulkSelection.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Long[] getAssigneeIds() {
		if (_assigneeIdsSupplier != null) {
			assigneeIds = _assigneeIdsSupplier.get();

			_assigneeIdsSupplier = null;
		}

		return assigneeIds;
	}

	public void setAssigneeIds(Long[] assigneeIds) {
		this.assigneeIds = assigneeIds;

		_assigneeIdsSupplier = null;
	}

	@JsonIgnore
	public void setAssigneeIds(
		UnsafeSupplier<Long[], Exception> assigneeIdsUnsafeSupplier) {

		_assigneeIdsSupplier = () -> {
			try {
				return assigneeIdsUnsafeSupplier.get();
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
	protected Long[] assigneeIds;

	@JsonIgnore
	private Supplier<Long[]> _assigneeIdsSupplier;

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
	public Long getProcessId() {
		if (_processIdSupplier != null) {
			processId = _processIdSupplier.get();

			_processIdSupplier = null;
		}

		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;

		_processIdSupplier = null;
	}

	@JsonIgnore
	public void setProcessId(
		UnsafeSupplier<Long, Exception> processIdUnsafeSupplier) {

		_processIdSupplier = () -> {
			try {
				return processIdUnsafeSupplier.get();
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
	protected Long processId;

	@JsonIgnore
	private Supplier<Long> _processIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getSlaStatuses() {
		if (_slaStatusesSupplier != null) {
			slaStatuses = _slaStatusesSupplier.get();

			_slaStatusesSupplier = null;
		}

		return slaStatuses;
	}

	public void setSlaStatuses(String[] slaStatuses) {
		this.slaStatuses = slaStatuses;

		_slaStatusesSupplier = null;
	}

	@JsonIgnore
	public void setSlaStatuses(
		UnsafeSupplier<String[], Exception> slaStatusesUnsafeSupplier) {

		_slaStatusesSupplier = () -> {
			try {
				return slaStatusesUnsafeSupplier.get();
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
	protected String[] slaStatuses;

	@JsonIgnore
	private Supplier<String[]> _slaStatusesSupplier;

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

		if (!(object instanceof TaskBulkSelection)) {
			return false;
		}

		TaskBulkSelection taskBulkSelection = (TaskBulkSelection)object;

		return Objects.equals(toString(), taskBulkSelection.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Long[] assigneeIds = getAssigneeIds();

		if (assigneeIds != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assigneeIds\": ");

			sb.append("[");

			for (int i = 0; i < assigneeIds.length; i++) {
				sb.append(assigneeIds[i]);

				if ((i + 1) < assigneeIds.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
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

		Long processId = getProcessId();

		if (processId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"processId\": ");

			sb.append(processId);
		}

		String[] slaStatuses = getSlaStatuses();

		if (slaStatuses != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"slaStatuses\": ");

			sb.append("[");

			for (int i = 0; i < slaStatuses.length; i++) {
				sb.append("\"");

				sb.append(_escape(slaStatuses[i]));

				sb.append("\"");

				if ((i + 1) < slaStatuses.length) {
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
		defaultValue = "com.liferay.portal.workflow.metrics.rest.dto.v1_0.TaskBulkSelection",
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