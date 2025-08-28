/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.dto.v1_0;

import com.liferay.object.admin.rest.client.function.UnsafeSupplier;
import com.liferay.object.admin.rest.client.serdes.v1_0.ObjectDefinitionValidationResponseSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class ObjectDefinitionValidationResponse
	implements Cloneable, Serializable {

	public static ObjectDefinitionValidationResponse toDTO(String json) {
		return ObjectDefinitionValidationResponseSerDes.toDTO(json);
	}

	public ObjectDefinitionValidationError[]
		getObjectDefinitionValidationErrors() {

		return objectDefinitionValidationErrors;
	}

	public void setObjectDefinitionValidationErrors(
		ObjectDefinitionValidationError[] objectDefinitionValidationErrors) {

		this.objectDefinitionValidationErrors =
			objectDefinitionValidationErrors;
	}

	public void setObjectDefinitionValidationErrors(
		UnsafeSupplier<ObjectDefinitionValidationError[], Exception>
			objectDefinitionValidationErrorsUnsafeSupplier) {

		try {
			objectDefinitionValidationErrors =
				objectDefinitionValidationErrorsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ObjectDefinitionValidationError[]
		objectDefinitionValidationErrors;

	@Override
	public ObjectDefinitionValidationResponse clone()
		throws CloneNotSupportedException {

		return (ObjectDefinitionValidationResponse)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectDefinitionValidationResponse)) {
			return false;
		}

		ObjectDefinitionValidationResponse objectDefinitionValidationResponse =
			(ObjectDefinitionValidationResponse)object;

		return Objects.equals(
			toString(), objectDefinitionValidationResponse.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ObjectDefinitionValidationResponseSerDes.toJSON(this);
	}

}