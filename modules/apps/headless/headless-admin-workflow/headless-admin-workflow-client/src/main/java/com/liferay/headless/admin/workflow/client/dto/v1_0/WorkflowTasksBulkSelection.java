/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.client.dto.v1_0;

import com.liferay.headless.admin.workflow.client.function.UnsafeSupplier;
import com.liferay.headless.admin.workflow.client.serdes.v1_0.WorkflowTasksBulkSelectionSerDes;

import java.io.Serializable;

import java.util.Date;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class WorkflowTasksBulkSelection implements Cloneable, Serializable {

	public static WorkflowTasksBulkSelection toDTO(String json) {
		return WorkflowTasksBulkSelectionSerDes.toDTO(json);
	}

	public Boolean getAndOperator() {
		return andOperator;
	}

	public void setAndOperator(Boolean andOperator) {
		this.andOperator = andOperator;
	}

	public void setAndOperator(
		UnsafeSupplier<Boolean, Exception> andOperatorUnsafeSupplier) {

		try {
			andOperator = andOperatorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean andOperator;

	public Long[] getAssetPrimaryKeys() {
		return assetPrimaryKeys;
	}

	public void setAssetPrimaryKeys(Long[] assetPrimaryKeys) {
		this.assetPrimaryKeys = assetPrimaryKeys;
	}

	public void setAssetPrimaryKeys(
		UnsafeSupplier<Long[], Exception> assetPrimaryKeysUnsafeSupplier) {

		try {
			assetPrimaryKeys = assetPrimaryKeysUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long[] assetPrimaryKeys;

	public String getAssetTitle() {
		return assetTitle;
	}

	public void setAssetTitle(String assetTitle) {
		this.assetTitle = assetTitle;
	}

	public void setAssetTitle(
		UnsafeSupplier<String, Exception> assetTitleUnsafeSupplier) {

		try {
			assetTitle = assetTitleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String assetTitle;

	public String[] getAssetTypes() {
		return assetTypes;
	}

	public void setAssetTypes(String[] assetTypes) {
		this.assetTypes = assetTypes;
	}

	public void setAssetTypes(
		UnsafeSupplier<String[], Exception> assetTypesUnsafeSupplier) {

		try {
			assetTypes = assetTypesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] assetTypes;

	public Long[] getAssigneeIds() {
		return assigneeIds;
	}

	public void setAssigneeIds(Long[] assigneeIds) {
		this.assigneeIds = assigneeIds;
	}

	public void setAssigneeIds(
		UnsafeSupplier<Long[], Exception> assigneeIdsUnsafeSupplier) {

		try {
			assigneeIds = assigneeIdsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long[] assigneeIds;

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

	public Date getDateDueEnd() {
		return dateDueEnd;
	}

	public void setDateDueEnd(Date dateDueEnd) {
		this.dateDueEnd = dateDueEnd;
	}

	public void setDateDueEnd(
		UnsafeSupplier<Date, Exception> dateDueEndUnsafeSupplier) {

		try {
			dateDueEnd = dateDueEndUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateDueEnd;

	public Date getDateDueStart() {
		return dateDueStart;
	}

	public void setDateDueStart(Date dateDueStart) {
		this.dateDueStart = dateDueStart;
	}

	public void setDateDueStart(
		UnsafeSupplier<Date, Exception> dateDueStartUnsafeSupplier) {

		try {
			dateDueStart = dateDueStartUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Date dateDueStart;

	public Boolean getSearchByRoles() {
		return searchByRoles;
	}

	public void setSearchByRoles(Boolean searchByRoles) {
		this.searchByRoles = searchByRoles;
	}

	public void setSearchByRoles(
		UnsafeSupplier<Boolean, Exception> searchByRolesUnsafeSupplier) {

		try {
			searchByRoles = searchByRolesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean searchByRoles;

	public Boolean getSearchByUserRoles() {
		return searchByUserRoles;
	}

	public void setSearchByUserRoles(Boolean searchByUserRoles) {
		this.searchByUserRoles = searchByUserRoles;
	}

	public void setSearchByUserRoles(
		UnsafeSupplier<Boolean, Exception> searchByUserRolesUnsafeSupplier) {

		try {
			searchByUserRoles = searchByUserRolesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean searchByUserRoles;

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

	public Long[] getWorkflowInstanceIds() {
		return workflowInstanceIds;
	}

	public void setWorkflowInstanceIds(Long[] workflowInstanceIds) {
		this.workflowInstanceIds = workflowInstanceIds;
	}

	public void setWorkflowInstanceIds(
		UnsafeSupplier<Long[], Exception> workflowInstanceIdsUnsafeSupplier) {

		try {
			workflowInstanceIds = workflowInstanceIdsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long[] workflowInstanceIds;

	public String[] getWorkflowTaskNames() {
		return workflowTaskNames;
	}

	public void setWorkflowTaskNames(String[] workflowTaskNames) {
		this.workflowTaskNames = workflowTaskNames;
	}

	public void setWorkflowTaskNames(
		UnsafeSupplier<String[], Exception> workflowTaskNamesUnsafeSupplier) {

		try {
			workflowTaskNames = workflowTaskNamesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] workflowTaskNames;

	@Override
	public WorkflowTasksBulkSelection clone()
		throws CloneNotSupportedException {

		return (WorkflowTasksBulkSelection)super.clone();
	}

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
		return WorkflowTasksBulkSelectionSerDes.toJSON(this);
	}

}