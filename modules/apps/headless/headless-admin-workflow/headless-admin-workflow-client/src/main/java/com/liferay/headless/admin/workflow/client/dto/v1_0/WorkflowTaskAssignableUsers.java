/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.client.dto.v1_0;

import com.liferay.headless.admin.workflow.client.function.UnsafeSupplier;
import com.liferay.headless.admin.workflow.client.serdes.v1_0.WorkflowTaskAssignableUsersSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class WorkflowTaskAssignableUsers implements Cloneable, Serializable {

	public static WorkflowTaskAssignableUsers toDTO(String json) {
		return WorkflowTaskAssignableUsersSerDes.toDTO(json);
	}

	public WorkflowTaskAssignableUser[] getWorkflowTaskAssignableUsers() {
		return workflowTaskAssignableUsers;
	}

	public void setWorkflowTaskAssignableUsers(
		WorkflowTaskAssignableUser[] workflowTaskAssignableUsers) {

		this.workflowTaskAssignableUsers = workflowTaskAssignableUsers;
	}

	public void setWorkflowTaskAssignableUsers(
		UnsafeSupplier<WorkflowTaskAssignableUser[], Exception>
			workflowTaskAssignableUsersUnsafeSupplier) {

		try {
			workflowTaskAssignableUsers =
				workflowTaskAssignableUsersUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected WorkflowTaskAssignableUser[] workflowTaskAssignableUsers;

	@Override
	public WorkflowTaskAssignableUsers clone()
		throws CloneNotSupportedException {

		return (WorkflowTaskAssignableUsers)super.clone();
	}

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
		return WorkflowTaskAssignableUsersSerDes.toJSON(this);
	}

}