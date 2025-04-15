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

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName("WorkflowTaskAssignableUsers")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "WorkflowTaskAssignableUsers")
public class WorkflowTaskAssignableUsers implements Serializable {

	public static WorkflowTaskAssignableUsers toDTO(String json) {
		return ObjectMapperUtil.readValue(
			WorkflowTaskAssignableUsers.class, json);
	}

	public static WorkflowTaskAssignableUsers unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			WorkflowTaskAssignableUsers.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public WorkflowTaskAssignableUser[] getWorkflowTaskAssignableUsers() {
		if (_workflowTaskAssignableUsersSupplier != null) {
			workflowTaskAssignableUsers =
				_workflowTaskAssignableUsersSupplier.get();

			_workflowTaskAssignableUsersSupplier = null;
		}

		return workflowTaskAssignableUsers;
	}

	public void setWorkflowTaskAssignableUsers(
		WorkflowTaskAssignableUser[] workflowTaskAssignableUsers) {

		this.workflowTaskAssignableUsers = workflowTaskAssignableUsers;

		_workflowTaskAssignableUsersSupplier = null;
	}

	@JsonIgnore
	public void setWorkflowTaskAssignableUsers(
		UnsafeSupplier<WorkflowTaskAssignableUser[], Exception>
			workflowTaskAssignableUsersUnsafeSupplier) {

		_workflowTaskAssignableUsersSupplier = () -> {
			try {
				return workflowTaskAssignableUsersUnsafeSupplier.get();
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
	protected WorkflowTaskAssignableUser[] workflowTaskAssignableUsers;

	@JsonIgnore
	private Supplier<WorkflowTaskAssignableUser[]>
		_workflowTaskAssignableUsersSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WorkflowTaskAssignableUsers)) {
			return false;
		}

		WorkflowTaskAssignableUsers workflowTaskAssignableUsers =
			(WorkflowTaskAssignableUsers)object;

		return Objects.equals(
			toString(), workflowTaskAssignableUsers.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		WorkflowTaskAssignableUser[] workflowTaskAssignableUsers =
			getWorkflowTaskAssignableUsers();

		if (workflowTaskAssignableUsers != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowTaskAssignableUsers\": ");

			sb.append("[");

			for (int i = 0; i < workflowTaskAssignableUsers.length; i++) {
				sb.append(String.valueOf(workflowTaskAssignableUsers[i]));

				if ((i + 1) < workflowTaskAssignableUsers.length) {
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
		defaultValue = "com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTaskAssignableUsers",
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