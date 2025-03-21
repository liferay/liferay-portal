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

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Represents a task to be executed in a workflow.",
	value = "WorkflowTask"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "WorkflowTask")
public class WorkflowTask implements Serializable {

	public static WorkflowTask toDTO(String json) {
		return ObjectMapperUtil.readValue(WorkflowTask.class, json);
	}

	public static WorkflowTask unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(WorkflowTask.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, Map<String, String>> getActions() {
		if (_actionsSupplier != null) {
			actions = _actionsSupplier.get();

			_actionsSupplier = null;
		}

		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;

		_actionsSupplier = null;
	}

	@JsonIgnore
	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		_actionsSupplier = () -> {
			try {
				return actionsUnsafeSupplier.get();
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
	protected Map<String, Map<String, String>> actions;

	@JsonIgnore
	private Supplier<Map<String, Map<String, String>>> _actionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Creator getAssigneePerson() {
		if (_assigneePersonSupplier != null) {
			assigneePerson = _assigneePersonSupplier.get();

			_assigneePersonSupplier = null;
		}

		return assigneePerson;
	}

	public void setAssigneePerson(Creator assigneePerson) {
		this.assigneePerson = assigneePerson;

		_assigneePersonSupplier = null;
	}

	@JsonIgnore
	public void setAssigneePerson(
		UnsafeSupplier<Creator, Exception> assigneePersonUnsafeSupplier) {

		_assigneePersonSupplier = () -> {
			try {
				return assigneePersonUnsafeSupplier.get();
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
	protected Creator assigneePerson;

	@JsonIgnore
	private Supplier<Creator> _assigneePersonSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Role[] getAssigneeRoles() {
		if (_assigneeRolesSupplier != null) {
			assigneeRoles = _assigneeRolesSupplier.get();

			_assigneeRolesSupplier = null;
		}

		return assigneeRoles;
	}

	public void setAssigneeRoles(Role[] assigneeRoles) {
		this.assigneeRoles = assigneeRoles;

		_assigneeRolesSupplier = null;
	}

	@JsonIgnore
	public void setAssigneeRoles(
		UnsafeSupplier<Role[], Exception> assigneeRolesUnsafeSupplier) {

		_assigneeRolesSupplier = () -> {
			try {
				return assigneeRolesUnsafeSupplier.get();
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
	protected Role[] assigneeRoles;

	@JsonIgnore
	private Supplier<Role[]> _assigneeRolesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether the task is complete."
	)
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

	@GraphQLField(
		description = "A flag that indicates whether the task is complete."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Boolean completed;

	@JsonIgnore
	private Supplier<Boolean> _completedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The task's completion date."
	)
	public Date getDateCompletion() {
		if (_dateCompletionSupplier != null) {
			dateCompletion = _dateCompletionSupplier.get();

			_dateCompletionSupplier = null;
		}

		return dateCompletion;
	}

	public void setDateCompletion(Date dateCompletion) {
		this.dateCompletion = dateCompletion;

		_dateCompletionSupplier = null;
	}

	@JsonIgnore
	public void setDateCompletion(
		UnsafeSupplier<Date, Exception> dateCompletionUnsafeSupplier) {

		_dateCompletionSupplier = () -> {
			try {
				return dateCompletionUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The task's completion date.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateCompletion;

	@JsonIgnore
	private Supplier<Date> _dateCompletionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The task's creation date."
	)
	public Date getDateCreated() {
		if (_dateCreatedSupplier != null) {
			dateCreated = _dateCreatedSupplier.get();

			_dateCreatedSupplier = null;
		}

		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;

		_dateCreatedSupplier = null;
	}

	@JsonIgnore
	public void setDateCreated(
		UnsafeSupplier<Date, Exception> dateCreatedUnsafeSupplier) {

		_dateCreatedSupplier = () -> {
			try {
				return dateCreatedUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The task's creation date.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateCreated;

	@JsonIgnore
	private Supplier<Date> _dateCreatedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The date the task should be completed by."
	)
	public Date getDateDue() {
		if (_dateDueSupplier != null) {
			dateDue = _dateDueSupplier.get();

			_dateDueSupplier = null;
		}

		return dateDue;
	}

	public void setDateDue(Date dateDue) {
		this.dateDue = dateDue;

		_dateDueSupplier = null;
	}

	@JsonIgnore
	public void setDateDue(
		UnsafeSupplier<Date, Exception> dateDueUnsafeSupplier) {

		_dateDueSupplier = () -> {
			try {
				return dateDueUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The date the task should be completed by.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Date dateDue;

	@JsonIgnore
	private Supplier<Date> _dateDueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The task's description."
	)
	public String getDescription() {
		if (_descriptionSupplier != null) {
			description = _descriptionSupplier.get();

			_descriptionSupplier = null;
		}

		return description;
	}

	public void setDescription(String description) {
		this.description = description;

		_descriptionSupplier = null;
	}

	@JsonIgnore
	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		_descriptionSupplier = () -> {
			try {
				return descriptionUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The task's description.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String description;

	@JsonIgnore
	private Supplier<String> _descriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(description = "The task's ID.")
	public Long getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(Long id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		_idSupplier = () -> {
			try {
				return idUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The task's ID.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getLabel() {
		if (_labelSupplier != null) {
			label = _labelSupplier.get();

			_labelSupplier = null;
		}

		return label;
	}

	public void setLabel(String label) {
		this.label = label;

		_labelSupplier = null;
	}

	@JsonIgnore
	public void setLabel(
		UnsafeSupplier<String, Exception> labelUnsafeSupplier) {

		_labelSupplier = () -> {
			try {
				return labelUnsafeSupplier.get();
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
	protected String label;

	@JsonIgnore
	private Supplier<String> _labelSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The task's name."
	)
	public String getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(String name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		_nameSupplier = () -> {
			try {
				return nameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The task's name.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The object/asset that the task's workflow is managing."
	)
	@Valid
	public ObjectReviewed getObjectReviewed() {
		if (_objectReviewedSupplier != null) {
			objectReviewed = _objectReviewedSupplier.get();

			_objectReviewedSupplier = null;
		}

		return objectReviewed;
	}

	public void setObjectReviewed(ObjectReviewed objectReviewed) {
		this.objectReviewed = objectReviewed;

		_objectReviewedSupplier = null;
	}

	@JsonIgnore
	public void setObjectReviewed(
		UnsafeSupplier<ObjectReviewed, Exception>
			objectReviewedUnsafeSupplier) {

		_objectReviewedSupplier = () -> {
			try {
				return objectReviewedUnsafeSupplier.get();
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
		description = "The object/asset that the task's workflow is managing."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected ObjectReviewed objectReviewed;

	@JsonIgnore
	private Supplier<ObjectReviewed> _objectReviewedSupplier;

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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Long workflowDefinitionId;

	@JsonIgnore
	private Supplier<Long> _workflowDefinitionIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The name of the task's workflow definition."
	)
	public String getWorkflowDefinitionName() {
		if (_workflowDefinitionNameSupplier != null) {
			workflowDefinitionName = _workflowDefinitionNameSupplier.get();

			_workflowDefinitionNameSupplier = null;
		}

		return workflowDefinitionName;
	}

	public void setWorkflowDefinitionName(String workflowDefinitionName) {
		this.workflowDefinitionName = workflowDefinitionName;

		_workflowDefinitionNameSupplier = null;
	}

	@JsonIgnore
	public void setWorkflowDefinitionName(
		UnsafeSupplier<String, Exception>
			workflowDefinitionNameUnsafeSupplier) {

		_workflowDefinitionNameSupplier = () -> {
			try {
				return workflowDefinitionNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The name of the task's workflow definition.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String workflowDefinitionName;

	@JsonIgnore
	private Supplier<String> _workflowDefinitionNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getWorkflowDefinitionVersion() {
		if (_workflowDefinitionVersionSupplier != null) {
			workflowDefinitionVersion =
				_workflowDefinitionVersionSupplier.get();

			_workflowDefinitionVersionSupplier = null;
		}

		return workflowDefinitionVersion;
	}

	public void setWorkflowDefinitionVersion(String workflowDefinitionVersion) {
		this.workflowDefinitionVersion = workflowDefinitionVersion;

		_workflowDefinitionVersionSupplier = null;
	}

	@JsonIgnore
	public void setWorkflowDefinitionVersion(
		UnsafeSupplier<String, Exception>
			workflowDefinitionVersionUnsafeSupplier) {

		_workflowDefinitionVersionSupplier = () -> {
			try {
				return workflowDefinitionVersionUnsafeSupplier.get();
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
	protected String workflowDefinitionVersion;

	@JsonIgnore
	private Supplier<String> _workflowDefinitionVersionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getWorkflowInstanceId() {
		if (_workflowInstanceIdSupplier != null) {
			workflowInstanceId = _workflowInstanceIdSupplier.get();

			_workflowInstanceIdSupplier = null;
		}

		return workflowInstanceId;
	}

	public void setWorkflowInstanceId(Long workflowInstanceId) {
		this.workflowInstanceId = workflowInstanceId;

		_workflowInstanceIdSupplier = null;
	}

	@JsonIgnore
	public void setWorkflowInstanceId(
		UnsafeSupplier<Long, Exception> workflowInstanceIdUnsafeSupplier) {

		_workflowInstanceIdSupplier = () -> {
			try {
				return workflowInstanceIdUnsafeSupplier.get();
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
	protected Long workflowInstanceId;

	@JsonIgnore
	private Supplier<Long> _workflowInstanceIdSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WorkflowTask)) {
			return false;
		}

		WorkflowTask workflowTask = (WorkflowTask)object;

		return Objects.equals(toString(), workflowTask.toString());
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

		Map<String, Map<String, String>> actions = getActions();

		if (actions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(actions));
		}

		Creator assigneePerson = getAssigneePerson();

		if (assigneePerson != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assigneePerson\": ");

			sb.append(String.valueOf(assigneePerson));
		}

		Role[] assigneeRoles = getAssigneeRoles();

		if (assigneeRoles != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assigneeRoles\": ");

			sb.append("[");

			for (int i = 0; i < assigneeRoles.length; i++) {
				sb.append(String.valueOf(assigneeRoles[i]));

				if ((i + 1) < assigneeRoles.length) {
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

		Date dateCompletion = getDateCompletion();

		if (dateCompletion != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCompletion\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateCompletion));

			sb.append("\"");
		}

		Date dateCreated = getDateCreated();

		if (dateCreated != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateCreated));

			sb.append("\"");
		}

		Date dateDue = getDateDue();

		if (dateDue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateDue\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateDue));

			sb.append("\"");
		}

		String description = getDescription();

		if (description != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(description));

			sb.append("\"");
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		String label = getLabel();

		if (label != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(label));

			sb.append("\"");
		}

		String name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(name));

			sb.append("\"");
		}

		ObjectReviewed objectReviewed = getObjectReviewed();

		if (objectReviewed != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectReviewed\": ");

			sb.append(String.valueOf(objectReviewed));
		}

		Long workflowDefinitionId = getWorkflowDefinitionId();

		if (workflowDefinitionId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowDefinitionId\": ");

			sb.append(workflowDefinitionId);
		}

		String workflowDefinitionName = getWorkflowDefinitionName();

		if (workflowDefinitionName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowDefinitionName\": ");

			sb.append("\"");

			sb.append(_escape(workflowDefinitionName));

			sb.append("\"");
		}

		String workflowDefinitionVersion = getWorkflowDefinitionVersion();

		if (workflowDefinitionVersion != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowDefinitionVersion\": ");

			sb.append("\"");

			sb.append(_escape(workflowDefinitionVersion));

			sb.append("\"");
		}

		Long workflowInstanceId = getWorkflowInstanceId();

		if (workflowInstanceId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"workflowInstanceId\": ");

			sb.append(workflowInstanceId);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTask",
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