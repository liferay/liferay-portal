/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.rest.client.dto.v1_0;

import com.liferay.batch.planner.rest.client.function.UnsafeSupplier;
import com.liferay.batch.planner.rest.client.serdes.v1_0.PlanSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Matija Petanjek
 * @generated
 */
@Generated("")
public class Plan implements Cloneable, Serializable {

	public static Plan toDTO(String json) {
		return PlanSerDes.toDTO(json);
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setActive(
		UnsafeSupplier<Boolean, Exception> activeUnsafeSupplier) {

		try {
			active = activeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean active;

	public Boolean getExport() {
		return export;
	}

	public void setExport(Boolean export) {
		this.export = export;
	}

	public void setExport(
		UnsafeSupplier<Boolean, Exception> exportUnsafeSupplier) {

		try {
			export = exportUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean export;

	public String getExternalType() {
		return externalType;
	}

	public void setExternalType(String externalType) {
		this.externalType = externalType;
	}

	public void setExternalType(
		UnsafeSupplier<String, Exception> externalTypeUnsafeSupplier) {

		try {
			externalType = externalTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalType;

	public String getExternalURL() {
		return externalURL;
	}

	public void setExternalURL(String externalURL) {
		this.externalURL = externalURL;
	}

	public void setExternalURL(
		UnsafeSupplier<String, Exception> externalURLUnsafeSupplier) {

		try {
			externalURL = externalURLUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalURL;

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

	public String getInternalClassName() {
		return internalClassName;
	}

	public void setInternalClassName(String internalClassName) {
		this.internalClassName = internalClassName;
	}

	public void setInternalClassName(
		UnsafeSupplier<String, Exception> internalClassNameUnsafeSupplier) {

		try {
			internalClassName = internalClassNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String internalClassName;

	public String getInternalClassNameKey() {
		return internalClassNameKey;
	}

	public void setInternalClassNameKey(String internalClassNameKey) {
		this.internalClassNameKey = internalClassNameKey;
	}

	public void setInternalClassNameKey(
		UnsafeSupplier<String, Exception> internalClassNameKeyUnsafeSupplier) {

		try {
			internalClassNameKey = internalClassNameKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String internalClassNameKey;

	public Mapping[] getMappings() {
		return mappings;
	}

	public void setMappings(Mapping[] mappings) {
		this.mappings = mappings;
	}

	public void setMappings(
		UnsafeSupplier<Mapping[], Exception> mappingsUnsafeSupplier) {

		try {
			mappings = mappingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Mapping[] mappings;

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

	public Policy[] getPolicies() {
		return policies;
	}

	public void setPolicies(Policy[] policies) {
		this.policies = policies;
	}

	public void setPolicies(
		UnsafeSupplier<Policy[], Exception> policiesUnsafeSupplier) {

		try {
			policies = policiesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Policy[] policies;

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public void setSize(UnsafeSupplier<Integer, Exception> sizeUnsafeSupplier) {
		try {
			size = sizeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer size;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setStatus(
		UnsafeSupplier<Integer, Exception> statusUnsafeSupplier) {

		try {
			status = statusUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer status;

	public String getTaskItemDelegateName() {
		return taskItemDelegateName;
	}

	public void setTaskItemDelegateName(String taskItemDelegateName) {
		this.taskItemDelegateName = taskItemDelegateName;
	}

	public void setTaskItemDelegateName(
		UnsafeSupplier<String, Exception> taskItemDelegateNameUnsafeSupplier) {

		try {
			taskItemDelegateName = taskItemDelegateNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String taskItemDelegateName;

	public Boolean getTemplate() {
		return template;
	}

	public void setTemplate(Boolean template) {
		this.template = template;
	}

	public void setTemplate(
		UnsafeSupplier<Boolean, Exception> templateUnsafeSupplier) {

		try {
			template = templateUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean template;

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public void setTotal(
		UnsafeSupplier<Integer, Exception> totalUnsafeSupplier) {

		try {
			total = totalUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer total;

	@Override
	public Plan clone() throws CloneNotSupportedException {
		return (Plan)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Plan)) {
			return false;
		}

		Plan plan = (Plan)object;

		return Objects.equals(toString(), plan.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PlanSerDes.toJSON(this);
	}

}