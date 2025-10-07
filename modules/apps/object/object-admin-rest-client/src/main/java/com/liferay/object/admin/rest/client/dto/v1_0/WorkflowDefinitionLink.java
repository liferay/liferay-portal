/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.dto.v1_0;

import com.liferay.object.admin.rest.client.function.UnsafeSupplier;
import com.liferay.object.admin.rest.client.serdes.v1_0.WorkflowDefinitionLinkSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class WorkflowDefinitionLink implements Cloneable, Serializable {

	public static WorkflowDefinitionLink toDTO(String json) {
		return WorkflowDefinitionLinkSerDes.toDTO(json);
	}

	public String getGroupExternalReferenceCode() {
		return groupExternalReferenceCode;
	}

	public void setGroupExternalReferenceCode(
		String groupExternalReferenceCode) {

		this.groupExternalReferenceCode = groupExternalReferenceCode;
	}

	public void setGroupExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			groupExternalReferenceCodeUnsafeSupplier) {

		try {
			groupExternalReferenceCode =
				groupExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String groupExternalReferenceCode;

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

	@Override
	public WorkflowDefinitionLink clone() throws CloneNotSupportedException {
		return (WorkflowDefinitionLink)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WorkflowDefinitionLink)) {
			return false;
		}

		WorkflowDefinitionLink workflowDefinitionLink =
			(WorkflowDefinitionLink)object;

		return Objects.equals(toString(), workflowDefinitionLink.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return WorkflowDefinitionLinkSerDes.toJSON(this);
	}

}