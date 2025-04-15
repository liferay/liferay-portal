/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.client.dto.v1_0;

import com.liferay.headless.admin.workflow.client.function.UnsafeSupplier;
import com.liferay.headless.admin.workflow.client.serdes.v1_0.WorkflowInstanceSubmitSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class WorkflowInstanceSubmit implements Cloneable, Serializable {

	public static WorkflowInstanceSubmit toDTO(String json) {
		return WorkflowInstanceSubmitSerDes.toDTO(json);
	}

	public Map<String, ?> getContext() {
		return context;
	}

	public void setContext(Map<String, ?> context) {
		this.context = context;
	}

	public void setContext(
		UnsafeSupplier<Map<String, ?>, Exception> contextUnsafeSupplier) {

		try {
			context = contextUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, ?> context;

	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	public void setSiteId(
		UnsafeSupplier<Long, Exception> siteIdUnsafeSupplier) {

		try {
			siteId = siteIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long siteId;

	public String getTransitionName() {
		return transitionName;
	}

	public void setTransitionName(String transitionName) {
		this.transitionName = transitionName;
	}

	public void setTransitionName(
		UnsafeSupplier<String, Exception> transitionNameUnsafeSupplier) {

		try {
			transitionName = transitionNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String transitionName;

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

	@Override
	public WorkflowInstanceSubmit clone() throws CloneNotSupportedException {
		return (WorkflowInstanceSubmit)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WorkflowInstanceSubmit)) {
			return false;
		}

		WorkflowInstanceSubmit workflowInstanceSubmit =
			(WorkflowInstanceSubmit)object;

		return Objects.equals(toString(), workflowInstanceSubmit.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return WorkflowInstanceSubmitSerDes.toJSON(this);
	}

}