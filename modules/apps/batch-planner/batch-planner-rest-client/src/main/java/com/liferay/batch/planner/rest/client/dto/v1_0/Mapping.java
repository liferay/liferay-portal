/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.batch.planner.rest.client.dto.v1_0;

import com.liferay.batch.planner.rest.client.function.UnsafeSupplier;
import com.liferay.batch.planner.rest.client.serdes.v1_0.MappingSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Matija Petanjek
 * @generated
 */
@Generated("")
public class Mapping implements Cloneable, Serializable {

	public static Mapping toDTO(String json) {
		return MappingSerDes.toDTO(json);
	}

	public String getExternalFieldName() {
		return externalFieldName;
	}

	public void setExternalFieldName(String externalFieldName) {
		this.externalFieldName = externalFieldName;
	}

	public void setExternalFieldName(
		UnsafeSupplier<String, Exception> externalFieldNameUnsafeSupplier) {

		try {
			externalFieldName = externalFieldNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalFieldName;

	public String getExternalFieldType() {
		return externalFieldType;
	}

	public void setExternalFieldType(String externalFieldType) {
		this.externalFieldType = externalFieldType;
	}

	public void setExternalFieldType(
		UnsafeSupplier<String, Exception> externalFieldTypeUnsafeSupplier) {

		try {
			externalFieldType = externalFieldTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalFieldType;

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

	public String getInternalFieldName() {
		return internalFieldName;
	}

	public void setInternalFieldName(String internalFieldName) {
		this.internalFieldName = internalFieldName;
	}

	public void setInternalFieldName(
		UnsafeSupplier<String, Exception> internalFieldNameUnsafeSupplier) {

		try {
			internalFieldName = internalFieldNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String internalFieldName;

	public String getInternalFieldType() {
		return internalFieldType;
	}

	public void setInternalFieldType(String internalFieldType) {
		this.internalFieldType = internalFieldType;
	}

	public void setInternalFieldType(
		UnsafeSupplier<String, Exception> internalFieldTypeUnsafeSupplier) {

		try {
			internalFieldType = internalFieldTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String internalFieldType;

	public Long getPlanId() {
		return planId;
	}

	public void setPlanId(Long planId) {
		this.planId = planId;
	}

	public void setPlanId(
		UnsafeSupplier<Long, Exception> planIdUnsafeSupplier) {

		try {
			planId = planIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long planId;

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public void setScript(
		UnsafeSupplier<String, Exception> scriptUnsafeSupplier) {

		try {
			script = scriptUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String script;

	@Override
	public Mapping clone() throws CloneNotSupportedException {
		return (Mapping)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Mapping)) {
			return false;
		}

		Mapping mapping = (Mapping)object;

		return Objects.equals(toString(), mapping.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return MappingSerDes.toJSON(this);
	}

}