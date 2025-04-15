/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.client.dto.v1_0;

import com.liferay.headless.admin.workflow.client.function.UnsafeSupplier;
import com.liferay.headless.admin.workflow.client.serdes.v1_0.WorkflowDefinitionLinkSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class WorkflowDefinitionLink implements Cloneable, Serializable {

	public static WorkflowDefinitionLink toDTO(String json) {
		return WorkflowDefinitionLinkSerDes.toDTO(json);
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setClassName(
		UnsafeSupplier<String, Exception> classNameUnsafeSupplier) {

		try {
			className = classNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String className;

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

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

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public void setGroupId(
		UnsafeSupplier<Long, Exception> groupIdUnsafeSupplier) {

		try {
			groupId = groupIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long groupId;

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

	public Integer getWorkflowDefinitionVersion() {
		return workflowDefinitionVersion;
	}

	public void setWorkflowDefinitionVersion(
		Integer workflowDefinitionVersion) {

		this.workflowDefinitionVersion = workflowDefinitionVersion;
	}

	public void setWorkflowDefinitionVersion(
		UnsafeSupplier<Integer, Exception>
			workflowDefinitionVersionUnsafeSupplier) {

		try {
			workflowDefinitionVersion =
				workflowDefinitionVersionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer workflowDefinitionVersion;

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