/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.client.dto.v1_0;

import com.liferay.headless.admin.workflow.client.function.UnsafeSupplier;
import com.liferay.headless.admin.workflow.client.serdes.v1_0.WorkflowTaskTransitionsSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class WorkflowTaskTransitions implements Cloneable, Serializable {

	public static WorkflowTaskTransitions toDTO(String json) {
		return WorkflowTaskTransitionsSerDes.toDTO(json);
	}

	public WorkflowTaskTransition[] getWorkflowTaskTransitions() {
		return workflowTaskTransitions;
	}

	public void setWorkflowTaskTransitions(
		WorkflowTaskTransition[] workflowTaskTransitions) {

		this.workflowTaskTransitions = workflowTaskTransitions;
	}

	public void setWorkflowTaskTransitions(
		UnsafeSupplier<WorkflowTaskTransition[], Exception>
			workflowTaskTransitionsUnsafeSupplier) {

		try {
			workflowTaskTransitions =
				workflowTaskTransitionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected WorkflowTaskTransition[] workflowTaskTransitions;

	@Override
	public WorkflowTaskTransitions clone() throws CloneNotSupportedException {
		return (WorkflowTaskTransitions)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WorkflowTaskTransitions)) {
			return false;
		}

		WorkflowTaskTransitions workflowTaskTransitions =
			(WorkflowTaskTransitions)object;

		return Objects.equals(toString(), workflowTaskTransitions.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return WorkflowTaskTransitionsSerDes.toJSON(this);
	}

}