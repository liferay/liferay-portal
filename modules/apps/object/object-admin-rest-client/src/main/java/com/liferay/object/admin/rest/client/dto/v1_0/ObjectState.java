/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.dto.v1_0;

import com.liferay.object.admin.rest.client.function.UnsafeSupplier;
import com.liferay.object.admin.rest.client.serdes.v1_0.ObjectStateSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class ObjectState implements Cloneable, Serializable {

	public static ObjectState toDTO(String json) {
		return ObjectStateSerDes.toDTO(json);
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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setKey(UnsafeSupplier<String, Exception> keyUnsafeSupplier) {
		try {
			key = keyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String key;

	public ObjectStateTransition[] getObjectStateTransitions() {
		return objectStateTransitions;
	}

	public void setObjectStateTransitions(
		ObjectStateTransition[] objectStateTransitions) {

		this.objectStateTransitions = objectStateTransitions;
	}

	public void setObjectStateTransitions(
		UnsafeSupplier<ObjectStateTransition[], Exception>
			objectStateTransitionsUnsafeSupplier) {

		try {
			objectStateTransitions = objectStateTransitionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ObjectStateTransition[] objectStateTransitions;

	@Override
	public ObjectState clone() throws CloneNotSupportedException {
		return (ObjectState)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectState)) {
			return false;
		}

		ObjectState objectState = (ObjectState)object;

		return Objects.equals(toString(), objectState.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ObjectStateSerDes.toJSON(this);
	}

}