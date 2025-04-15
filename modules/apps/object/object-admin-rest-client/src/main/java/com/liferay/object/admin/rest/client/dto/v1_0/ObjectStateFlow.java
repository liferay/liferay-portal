/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.dto.v1_0;

import com.liferay.object.admin.rest.client.function.UnsafeSupplier;
import com.liferay.object.admin.rest.client.serdes.v1_0.ObjectStateFlowSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class ObjectStateFlow implements Cloneable, Serializable {

	public static ObjectStateFlow toDTO(String json) {
		return ObjectStateFlowSerDes.toDTO(json);
	}

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

	public ObjectState[] getObjectStates() {
		return objectStates;
	}

	public void setObjectStates(ObjectState[] objectStates) {
		this.objectStates = objectStates;
	}

	public void setObjectStates(
		UnsafeSupplier<ObjectState[], Exception> objectStatesUnsafeSupplier) {

		try {
			objectStates = objectStatesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ObjectState[] objectStates;

	@Override
	public ObjectStateFlow clone() throws CloneNotSupportedException {
		return (ObjectStateFlow)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectStateFlow)) {
			return false;
		}

		ObjectStateFlow objectStateFlow = (ObjectStateFlow)object;

		return Objects.equals(toString(), objectStateFlow.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ObjectStateFlowSerDes.toJSON(this);
	}

}