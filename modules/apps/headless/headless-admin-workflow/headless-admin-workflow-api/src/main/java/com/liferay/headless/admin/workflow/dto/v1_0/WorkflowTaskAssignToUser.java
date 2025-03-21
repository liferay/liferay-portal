/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.dto.v1_0;

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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Represents a write-only schema to assign a workflow task to a specific user.",
	value = "WorkflowTaskAssignToUser"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "WorkflowTaskAssignToUser")
public class WorkflowTaskAssignToUser implements Serializable {

	public static WorkflowTaskAssignToUser toDTO(String json) {
		return ObjectMapperUtil.readValue(WorkflowTaskAssignToUser.class, json);
	}

	public static WorkflowTaskAssignToUser unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			WorkflowTaskAssignToUser.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The ID of the user to assign the workflow task."
	)
	public Long getAssigneeId() {
		if (_assigneeIdSupplier != null) {
			assigneeId = _assigneeIdSupplier.get();

			_assigneeIdSupplier = null;
		}

		return assigneeId;
	}

	public void setAssigneeId(Long assigneeId) {
		this.assigneeId = assigneeId;

		_assigneeIdSupplier = null;
	}

	@JsonIgnore
	public void setAssigneeId(
		UnsafeSupplier<Long, Exception> assigneeIdUnsafeSupplier) {

		_assigneeIdSupplier = () -> {
			try {
				return assigneeIdUnsafeSupplier.get();
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
		description = "The ID of the user to assign the workflow task."
	)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected Long assigneeId;

	@JsonIgnore
	private Supplier<Long> _assigneeIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "An optional comment to add when assigning the workflow task."
	)
	public String getComment() {
		if (_commentSupplier != null) {
			comment = _commentSupplier.get();

			_commentSupplier = null;
		}

		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;

		_commentSupplier = null;
	}

	@JsonIgnore
	public void setComment(
		UnsafeSupplier<String, Exception> commentUnsafeSupplier) {

		_commentSupplier = () -> {
			try {
				return commentUnsafeSupplier.get();
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
		description = "An optional comment to add when assigning the workflow task."
	)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected String comment;

	@JsonIgnore
	private Supplier<String> _commentSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The date on which the workflow task should be executed."
	)
	public Date getDueDate() {
		if (_dueDateSupplier != null) {
			dueDate = _dueDateSupplier.get();

			_dueDateSupplier = null;
		}

		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;

		_dueDateSupplier = null;
	}

	@JsonIgnore
	public void setDueDate(
		UnsafeSupplier<Date, Exception> dueDateUnsafeSupplier) {

		_dueDateSupplier = () -> {
			try {
				return dueDateUnsafeSupplier.get();
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
		description = "The date on which the workflow task should be executed."
	)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected Date dueDate;

	@JsonIgnore
	private Supplier<Date> _dueDateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getWorkflowTaskId() {
		if (_workflowTaskIdSupplier != null) {
			workflowTaskId = _workflowTaskIdSupplier.get();

			_workflowTaskIdSupplier = null;
		}

		return workflowTaskId;
	}

	public void setWorkflowTaskId(Long workflowTaskId) {
		this.workflowTaskId = workflowTaskId;

		_workflowTaskIdSupplier = null;
	}

	@JsonIgnore
	public void setWorkflowTaskId(
		UnsafeSupplier<Long, Exception> workflowTaskIdUnsafeSupplier) {

		_workflowTaskIdSupplier = () -> {
			try {
				return workflowTaskIdUnsafeSupplier.get();
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
	protected Long workflowTaskId;

	@JsonIgnore
	private Supplier<Long> _workflowTaskIdSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WorkflowTaskAssignToUser)) {
			return false;
		}

		WorkflowTaskAssignToUser workflowTaskAssignToUser =
			(WorkflowTaskAssignToUser)object;

		return Objects.equals(toString(), workflowTaskAssignToUser.toString());
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

		Long assigneeId = getAssigneeId();

		if (assigneeId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assigneeId\": ");

			sb.append(assigneeId);
		}

		String comment = getComment();

		if (comment != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"comment\": ");

			sb.append("\"");

			sb.append(_escape(comment));

			sb.append("\"");
		}

		Date dueDate = getDueDate();

		if (dueDate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dueDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dueDate));

			sb.append("\"");
		}

		Long workflowTaskId = getWorkflowTaskId();

		if (workflowTaskId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowTaskId\": ");

			sb.append(workflowTaskId);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTaskAssignToUser",
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