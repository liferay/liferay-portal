/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.client.dto.v1_0;

import com.liferay.headless.admin.workflow.client.function.UnsafeSupplier;
import com.liferay.headless.admin.workflow.client.serdes.v1_0.WorkflowTaskTransitionSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class WorkflowTaskTransition implements Cloneable, Serializable {

	public static WorkflowTaskTransition toDTO(String json) {
		return WorkflowTaskTransitionSerDes.toDTO(json);
	}

	public Transition[] getTransitions() {
		return transitions;
	}

	public void setTransitions(Transition[] transitions) {
		this.transitions = transitions;
	}

	public void setTransitions(
		UnsafeSupplier<Transition[], Exception> transitionsUnsafeSupplier) {

		try {
			transitions = transitionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Transition[] transitions;

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

	public String getWorkflowTaskLabel() {
		return workflowTaskLabel;
	}

	public void setWorkflowTaskLabel(String workflowTaskLabel) {
		this.workflowTaskLabel = workflowTaskLabel;
	}

	public void setWorkflowTaskLabel(
		UnsafeSupplier<String, Exception> workflowTaskLabelUnsafeSupplier) {

		try {
			workflowTaskLabel = workflowTaskLabelUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String workflowTaskLabel;

	public String getWorkflowTaskName() {
		return workflowTaskName;
	}

	public void setWorkflowTaskName(String workflowTaskName) {
		this.workflowTaskName = workflowTaskName;
	}

	public void setWorkflowTaskName(
		UnsafeSupplier<String, Exception> workflowTaskNameUnsafeSupplier) {

		try {
			workflowTaskName = workflowTaskNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String workflowTaskName;

	@Override
	public WorkflowTaskTransition clone() throws CloneNotSupportedException {
		return (WorkflowTaskTransition)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WorkflowTaskTransition)) {
			return false;
		}

		WorkflowTaskTransition workflowTaskTransition =
			(WorkflowTaskTransition)object;

		return Objects.equals(toString(), workflowTaskTransition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return WorkflowTaskTransitionSerDes.toJSON(this);
	}

}