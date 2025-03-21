/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.client.dto.v1_0;

import com.liferay.headless.admin.workflow.client.function.UnsafeSupplier;
import com.liferay.headless.admin.workflow.client.serdes.v1_0.WorkflowTaskAssignableUserSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class WorkflowTaskAssignableUser implements Cloneable, Serializable {

	public static WorkflowTaskAssignableUser toDTO(String json) {
		return WorkflowTaskAssignableUserSerDes.toDTO(json);
	}

	public Assignee[] getAssignableUsers() {
		return assignableUsers;
	}

	public void setAssignableUsers(Assignee[] assignableUsers) {
		this.assignableUsers = assignableUsers;
	}

	public void setAssignableUsers(
		UnsafeSupplier<Assignee[], Exception> assignableUsersUnsafeSupplier) {

		try {
			assignableUsers = assignableUsersUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Assignee[] assignableUsers;

	public Long getWorkflowTaskId() {
		return workflowTaskId;
	}

	public void setWorkflowTaskId(Long workflowTaskId) {
		this.workflowTaskId = workflowTaskId;
	}

	public void setWorkflowTaskId(
		UnsafeSupplier<Long, Exception> workflowTaskIdUnsafeSupplier) {

		try {
			workflowTaskId = workflowTaskIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long workflowTaskId;

	@Override
	public WorkflowTaskAssignableUser clone()
		throws CloneNotSupportedException {

		return (WorkflowTaskAssignableUser)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WorkflowTaskAssignableUser)) {
			return false;
		}

		WorkflowTaskAssignableUser workflowTaskAssignableUser =
			(WorkflowTaskAssignableUser)object;

		return Objects.equals(
			toString(), workflowTaskAssignableUser.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return WorkflowTaskAssignableUserSerDes.toJSON(this);
	}

}