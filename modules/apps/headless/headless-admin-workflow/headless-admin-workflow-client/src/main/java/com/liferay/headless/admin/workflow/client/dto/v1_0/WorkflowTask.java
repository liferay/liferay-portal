/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.client.dto.v1_0;

import com.liferay.headless.admin.workflow.client.function.UnsafeSupplier;
import com.liferay.headless.admin.workflow.client.serdes.v1_0.WorkflowTaskSerDes;

import java.io.Serializable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class WorkflowTask implements Cloneable, Serializable {

	public static WorkflowTask toDTO(String json) {
		return WorkflowTaskSerDes.toDTO(json);
	}

	public Map<String, Map<String, String>> getActions() {
		return actions;
	}

	public void setActions(Map<String, Map<String, String>> actions) {
		this.actions = actions;
	}

	public void setActions(
		UnsafeSupplier<Map<String, Map<String, String>>, Exception>
			actionsUnsafeSupplier) {

		try {
			actions = actionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Map<String, String>> actions;

	public Creator getAssigneePerson() {
		return assigneePerson;
	}

	public void setAssigneePerson(Creator assigneePerson) {
		this.assigneePerson = assigneePerson;
	}

	public void setAssigneePerson(
		UnsafeSupplier<Creator, Exception> assigneePersonUnsafeSupplier) {

		try {
			assigneePerson = assigneePersonUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Creator assigneePerson;

	public Role[] getAssigneeRoles() {
		return assigneeRoles;
	}

	public void setAssigneeRoles(Role[] assigneeRoles) {
		this.assigneeRoles = assigneeRoles;
	}

	public void setAssigneeRoles(
		UnsafeSupplier<Role[], Exception> assigneeRolesUnsafeSupplier) {

		try {
			assigneeRoles = assigneeRolesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Role[] assigneeRoles;

	public Boolean getCompleted() {
		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	public void setCompleted(
		UnsafeSupplier<Boolean, Exception> completedUnsafeSupplier) {

		try {
			completed = completedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean completed;

	public Date getDateCompletion() {
		return dateCompletion;
	}

	public void setDateCompletion(Date dateCompletion) {
		this.dateCompletion = dateCompletion;
	}

	public void setDateCompletion(
		UnsafeSupplier<Date, Exception> dateCompletionUnsafeSupplier) {

		try {
			dateCompletion = dateCompletionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateCompletion;

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public void setDateCreated(
		UnsafeSupplier<Date, Exception> dateCreatedUnsafeSupplier) {

		try {
			dateCreated = dateCreatedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateCreated;

	public Date getDateDue() {
		return dateDue;
	}

	public void setDateDue(Date dateDue) {
		this.dateDue = dateDue;
	}

	public void setDateDue(
		UnsafeSupplier<Date, Exception> dateDueUnsafeSupplier) {

		try {
			dateDue = dateDueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateDue;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		try {
			description = descriptionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String description;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setLabel(
		UnsafeSupplier<String, Exception> labelUnsafeSupplier) {

		try {
			label = labelUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String label;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String name;

	public ObjectReviewed getObjectReviewed() {
		return objectReviewed;
	}

	public void setObjectReviewed(ObjectReviewed objectReviewed) {
		this.objectReviewed = objectReviewed;
	}

	public void setObjectReviewed(
		UnsafeSupplier<ObjectReviewed, Exception>
			objectReviewedUnsafeSupplier) {

		try {
			objectReviewed = objectReviewedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ObjectReviewed objectReviewed;

	public Long getWorkflowDefinitionId() {
		return workflowDefinitionId;
	}

	public void setWorkflowDefinitionId(Long workflowDefinitionId) {
		this.workflowDefinitionId = workflowDefinitionId;
	}

	public void setWorkflowDefinitionId(
		UnsafeSupplier<Long, Exception> workflowDefinitionIdUnsafeSupplier) {

		try {
			workflowDefinitionId = workflowDefinitionIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long workflowDefinitionId;

	public String getWorkflowDefinitionName() {
		return workflowDefinitionName;
	}

	public void setWorkflowDefinitionName(String workflowDefinitionName) {
		this.workflowDefinitionName = workflowDefinitionName;
	}

	public void setWorkflowDefinitionName(
		UnsafeSupplier<String, Exception>
			workflowDefinitionNameUnsafeSupplier) {

		try {
			workflowDefinitionName = workflowDefinitionNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String workflowDefinitionName;

	public String getWorkflowDefinitionVersion() {
		return workflowDefinitionVersion;
	}

	public void setWorkflowDefinitionVersion(String workflowDefinitionVersion) {
		this.workflowDefinitionVersion = workflowDefinitionVersion;
	}

	public void setWorkflowDefinitionVersion(
		UnsafeSupplier<String, Exception>
			workflowDefinitionVersionUnsafeSupplier) {

		try {
			workflowDefinitionVersion =
				workflowDefinitionVersionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String workflowDefinitionVersion;

	public Long getWorkflowInstanceId() {
		return workflowInstanceId;
	}

	public void setWorkflowInstanceId(Long workflowInstanceId) {
		this.workflowInstanceId = workflowInstanceId;
	}

	public void setWorkflowInstanceId(
		UnsafeSupplier<Long, Exception> workflowInstanceIdUnsafeSupplier) {

		try {
			workflowInstanceId = workflowInstanceIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long workflowInstanceId;

	@Override
	public WorkflowTask clone() throws CloneNotSupportedException {
		return (WorkflowTask)super.clone();
	}

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
		return WorkflowTaskSerDes.toJSON(this);
	}

}