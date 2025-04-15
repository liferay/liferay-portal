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

import jakarta.annotation.Generated;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName("WorkflowTasksBulkSelection")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "WorkflowTasksBulkSelection")
public class WorkflowTasksBulkSelection implements Serializable {

	public static WorkflowTasksBulkSelection toDTO(String json) {
		return ObjectMapperUtil.readValue(
			WorkflowTasksBulkSelection.class, json);
	}

	public static WorkflowTasksBulkSelection unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			WorkflowTasksBulkSelection.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getAndOperator() {
		if (_andOperatorSupplier != null) {
			andOperator = _andOperatorSupplier.get();

			_andOperatorSupplier = null;
		}

		return andOperator;
	}

	public void setAndOperator(Boolean andOperator) {
		this.andOperator = andOperator;

		_andOperatorSupplier = null;
	}

	@JsonIgnore
	public void setAndOperator(
		UnsafeSupplier<Boolean, Exception> andOperatorUnsafeSupplier) {

		_andOperatorSupplier = () -> {
			try {
				return andOperatorUnsafeSupplier.get();
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
	protected Boolean andOperator;

	@JsonIgnore
	private Supplier<Boolean> _andOperatorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long[] getAssetPrimaryKeys() {
		if (_assetPrimaryKeysSupplier != null) {
			assetPrimaryKeys = _assetPrimaryKeysSupplier.get();

			_assetPrimaryKeysSupplier = null;
		}

		return assetPrimaryKeys;
	}

	public void setAssetPrimaryKeys(Long[] assetPrimaryKeys) {
		this.assetPrimaryKeys = assetPrimaryKeys;

		_assetPrimaryKeysSupplier = null;
	}

	@JsonIgnore
	public void setAssetPrimaryKeys(
		UnsafeSupplier<Long[], Exception> assetPrimaryKeysUnsafeSupplier) {

		_assetPrimaryKeysSupplier = () -> {
			try {
				return assetPrimaryKeysUnsafeSupplier.get();
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
	protected Long[] assetPrimaryKeys;

	@JsonIgnore
	private Supplier<Long[]> _assetPrimaryKeysSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getAssetTitle() {
		if (_assetTitleSupplier != null) {
			assetTitle = _assetTitleSupplier.get();

			_assetTitleSupplier = null;
		}

		return assetTitle;
	}

	public void setAssetTitle(String assetTitle) {
		this.assetTitle = assetTitle;

		_assetTitleSupplier = null;
	}

	@JsonIgnore
	public void setAssetTitle(
		UnsafeSupplier<String, Exception> assetTitleUnsafeSupplier) {

		_assetTitleSupplier = () -> {
			try {
				return assetTitleUnsafeSupplier.get();
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
	protected String assetTitle;

	@JsonIgnore
	private Supplier<String> _assetTitleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getAssetTypes() {
		if (_assetTypesSupplier != null) {
			assetTypes = _assetTypesSupplier.get();

			_assetTypesSupplier = null;
		}

		return assetTypes;
	}

	public void setAssetTypes(String[] assetTypes) {
		this.assetTypes = assetTypes;

		_assetTypesSupplier = null;
	}

	@JsonIgnore
	public void setAssetTypes(
		UnsafeSupplier<String[], Exception> assetTypesUnsafeSupplier) {

		_assetTypesSupplier = () -> {
			try {
				return assetTypesUnsafeSupplier.get();
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
	protected String[] assetTypes;

	@JsonIgnore
	private Supplier<String[]> _assetTypesSupplier;

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
	public Date getDateDueEnd() {
		if (_dateDueEndSupplier != null) {
			dateDueEnd = _dateDueEndSupplier.get();

			_dateDueEndSupplier = null;
		}

		return dateDueEnd;
	}

	public void setDateDueEnd(Date dateDueEnd) {
		this.dateDueEnd = dateDueEnd;

		_dateDueEndSupplier = null;
	}

	@JsonIgnore
	public void setDateDueEnd(
		UnsafeSupplier<Date, Exception> dateDueEndUnsafeSupplier) {

		_dateDueEndSupplier = () -> {
			try {
				return dateDueEndUnsafeSupplier.get();
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
	protected Date dateDueEnd;

	@JsonIgnore
	private Supplier<Date> _dateDueEndSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getDateDueStart() {
		if (_dateDueStartSupplier != null) {
			dateDueStart = _dateDueStartSupplier.get();

			_dateDueStartSupplier = null;
		}

		return dateDueStart;
	}

	public void setDateDueStart(Date dateDueStart) {
		this.dateDueStart = dateDueStart;

		_dateDueStartSupplier = null;
	}

	@JsonIgnore
	public void setDateDueStart(
		UnsafeSupplier<Date, Exception> dateDueStartUnsafeSupplier) {

		_dateDueStartSupplier = () -> {
			try {
				return dateDueStartUnsafeSupplier.get();
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
	protected Date dateDueStart;

	@JsonIgnore
	private Supplier<Date> _dateDueStartSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getSearchByRoles() {
		if (_searchByRolesSupplier != null) {
			searchByRoles = _searchByRolesSupplier.get();

			_searchByRolesSupplier = null;
		}

		return searchByRoles;
	}

	public void setSearchByRoles(Boolean searchByRoles) {
		this.searchByRoles = searchByRoles;

		_searchByRolesSupplier = null;
	}

	@JsonIgnore
	public void setSearchByRoles(
		UnsafeSupplier<Boolean, Exception> searchByRolesUnsafeSupplier) {

		_searchByRolesSupplier = () -> {
			try {
				return searchByRolesUnsafeSupplier.get();
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
	protected Boolean searchByRoles;

	@JsonIgnore
	private Supplier<Boolean> _searchByRolesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getSearchByUserRoles() {
		if (_searchByUserRolesSupplier != null) {
			searchByUserRoles = _searchByUserRolesSupplier.get();

			_searchByUserRolesSupplier = null;
		}

		return searchByUserRoles;
	}

	public void setSearchByUserRoles(Boolean searchByUserRoles) {
		this.searchByUserRoles = searchByUserRoles;

		_searchByUserRolesSupplier = null;
	}

	@JsonIgnore
	public void setSearchByUserRoles(
		UnsafeSupplier<Boolean, Exception> searchByUserRolesUnsafeSupplier) {

		_searchByUserRolesSupplier = () -> {
			try {
				return searchByUserRolesUnsafeSupplier.get();
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
	protected Boolean searchByUserRoles;

	@JsonIgnore
	private Supplier<Boolean> _searchByUserRolesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getWorkflowDefinitionId() {
		if (_workflowDefinitionIdSupplier != null) {
			workflowDefinitionId = _workflowDefinitionIdSupplier.get();

			_workflowDefinitionIdSupplier = null;
		}

		return workflowDefinitionId;
	}

	public void setWorkflowDefinitionId(Long workflowDefinitionId) {
		this.workflowDefinitionId = workflowDefinitionId;

		_workflowDefinitionIdSupplier = null;
	}

	@JsonIgnore
	public void setWorkflowDefinitionId(
		UnsafeSupplier<Long, Exception> workflowDefinitionIdUnsafeSupplier) {

		_workflowDefinitionIdSupplier = () -> {
			try {
				return workflowDefinitionIdUnsafeSupplier.get();
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
	protected Long workflowDefinitionId;

	@JsonIgnore
	private Supplier<Long> _workflowDefinitionIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long[] getWorkflowInstanceIds() {
		if (_workflowInstanceIdsSupplier != null) {
			workflowInstanceIds = _workflowInstanceIdsSupplier.get();

			_workflowInstanceIdsSupplier = null;
		}

		return workflowInstanceIds;
	}

	public void setWorkflowInstanceIds(Long[] workflowInstanceIds) {
		this.workflowInstanceIds = workflowInstanceIds;

		_workflowInstanceIdsSupplier = null;
	}

	@JsonIgnore
	public void setWorkflowInstanceIds(
		UnsafeSupplier<Long[], Exception> workflowInstanceIdsUnsafeSupplier) {

		_workflowInstanceIdsSupplier = () -> {
			try {
				return workflowInstanceIdsUnsafeSupplier.get();
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
	protected Long[] workflowInstanceIds;

	@JsonIgnore
	private Supplier<Long[]> _workflowInstanceIdsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getWorkflowTaskNames() {
		if (_workflowTaskNamesSupplier != null) {
			workflowTaskNames = _workflowTaskNamesSupplier.get();

			_workflowTaskNamesSupplier = null;
		}

		return workflowTaskNames;
	}

	public void setWorkflowTaskNames(String[] workflowTaskNames) {
		this.workflowTaskNames = workflowTaskNames;

		_workflowTaskNamesSupplier = null;
	}

	@JsonIgnore
	public void setWorkflowTaskNames(
		UnsafeSupplier<String[], Exception> workflowTaskNamesUnsafeSupplier) {

		_workflowTaskNamesSupplier = () -> {
			try {
				return workflowTaskNamesUnsafeSupplier.get();
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
	protected String[] workflowTaskNames;

	@JsonIgnore
	private Supplier<String[]> _workflowTaskNamesSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WorkflowTasksBulkSelection)) {
			return false;
		}

		WorkflowTasksBulkSelection workflowTasksBulkSelection =
			(WorkflowTasksBulkSelection)object;

		return Objects.equals(
			toString(), workflowTasksBulkSelection.toString());
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

		Boolean andOperator = getAndOperator();

		if (andOperator != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"andOperator\": ");

			sb.append(andOperator);
		}

		Long[] assetPrimaryKeys = getAssetPrimaryKeys();

		if (assetPrimaryKeys != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetPrimaryKeys\": ");

			sb.append("[");

			for (int i = 0; i < assetPrimaryKeys.length; i++) {
				sb.append(assetPrimaryKeys[i]);

				if ((i + 1) < assetPrimaryKeys.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String assetTitle = getAssetTitle();

		if (assetTitle != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetTitle\": ");

			sb.append("\"");

			sb.append(_escape(assetTitle));

			sb.append("\"");
		}

		String[] assetTypes = getAssetTypes();

		if (assetTypes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetTypes\": ");

			sb.append("[");

			for (int i = 0; i < assetTypes.length; i++) {
				sb.append("\"");

				sb.append(_escape(assetTypes[i]));

				sb.append("\"");

				if ((i + 1) < assetTypes.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

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

		Boolean completed = getCompleted();

		if (completed != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"completed\": ");

			sb.append(completed);
		}

		Date dateDueEnd = getDateDueEnd();

		if (dateDueEnd != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateDueEnd\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateDueEnd));

			sb.append("\"");
		}

		Date dateDueStart = getDateDueStart();

		if (dateDueStart != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateDueStart\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateDueStart));

			sb.append("\"");
		}

		Boolean searchByRoles = getSearchByRoles();

		if (searchByRoles != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"searchByRoles\": ");

			sb.append(searchByRoles);
		}

		Boolean searchByUserRoles = getSearchByUserRoles();

		if (searchByUserRoles != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"searchByUserRoles\": ");

			sb.append(searchByUserRoles);
		}

		Long workflowDefinitionId = getWorkflowDefinitionId();

		if (workflowDefinitionId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowDefinitionId\": ");

			sb.append(workflowDefinitionId);
		}

		Long[] workflowInstanceIds = getWorkflowInstanceIds();

		if (workflowInstanceIds != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowInstanceIds\": ");

			sb.append("[");

			for (int i = 0; i < workflowInstanceIds.length; i++) {
				sb.append(workflowInstanceIds[i]);

				if ((i + 1) < workflowInstanceIds.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String[] workflowTaskNames = getWorkflowTaskNames();

		if (workflowTaskNames != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowTaskNames\": ");

			sb.append("[");

			for (int i = 0; i < workflowTaskNames.length; i++) {
				sb.append("\"");

				sb.append(_escape(workflowTaskNames[i]));

				sb.append("\"");

				if ((i + 1) < workflowTaskNames.length) {
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
		defaultValue = "com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTasksBulkSelection",
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