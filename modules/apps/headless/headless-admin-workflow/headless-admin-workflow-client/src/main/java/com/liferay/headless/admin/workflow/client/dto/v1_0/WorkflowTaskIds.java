/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.client.dto.v1_0;

import com.liferay.headless.admin.workflow.client.function.UnsafeSupplier;
import com.liferay.headless.admin.workflow.client.serdes.v1_0.WorkflowTaskIdsSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class WorkflowTaskIds implements Cloneable, Serializable {

	public static WorkflowTaskIds toDTO(String json) {
		return WorkflowTaskIdsSerDes.toDTO(json);
	}

	public Long[] getWorkflowTaskIds() {
		return workflowTaskIds;
	}

	public void setWorkflowTaskIds(Long[] workflowTaskIds) {
		this.workflowTaskIds = workflowTaskIds;
	}

	public void setWorkflowTaskIds(
		UnsafeSupplier<Long[], Exception> workflowTaskIdsUnsafeSupplier) {

		try {
			workflowTaskIds = workflowTaskIdsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long[] workflowTaskIds;

	@Override
	public WorkflowTaskIds clone() throws CloneNotSupportedException {
		return (WorkflowTaskIds)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WorkflowTaskIds)) {
			return false;
		}

		WorkflowTaskIds workflowTaskIds = (WorkflowTaskIds)object;

		return Objects.equals(toString(), workflowTaskIds.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return WorkflowTaskIdsSerDes.toJSON(this);
	}

}