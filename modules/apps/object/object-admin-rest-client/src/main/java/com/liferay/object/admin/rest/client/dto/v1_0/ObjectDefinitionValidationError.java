/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.dto.v1_0;

import com.liferay.object.admin.rest.client.function.UnsafeSupplier;
import com.liferay.object.admin.rest.client.serdes.v1_0.ObjectDefinitionValidationErrorSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class ObjectDefinitionValidationError
	implements Cloneable, Serializable {

	public static ObjectDefinitionValidationError toDTO(String json) {
		return ObjectDefinitionValidationErrorSerDes.toDTO(json);
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setErrorMessage(
		UnsafeSupplier<String, Exception> errorMessageUnsafeSupplier) {

		try {
			errorMessage = errorMessageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String errorMessage;

	public String getExceptionClassName() {
		return exceptionClassName;
	}

	public void setExceptionClassName(String exceptionClassName) {
		this.exceptionClassName = exceptionClassName;
	}

	public void setExceptionClassName(
		UnsafeSupplier<String, Exception> exceptionClassNameUnsafeSupplier) {

		try {
			exceptionClassName = exceptionClassNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String exceptionClassName;

	public String getObjectDefinitionName() {
		return objectDefinitionName;
	}

	public void setObjectDefinitionName(String objectDefinitionName) {
		this.objectDefinitionName = objectDefinitionName;
	}

	public void setObjectDefinitionName(
		UnsafeSupplier<String, Exception> objectDefinitionNameUnsafeSupplier) {

		try {
			objectDefinitionName = objectDefinitionNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String objectDefinitionName;

	public String getObjectFieldName() {
		return objectFieldName;
	}

	public void setObjectFieldName(String objectFieldName) {
		this.objectFieldName = objectFieldName;
	}

	public void setObjectFieldName(
		UnsafeSupplier<String, Exception> objectFieldNameUnsafeSupplier) {

		try {
			objectFieldName = objectFieldNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String objectFieldName;

	public String getObjectFieldValue() {
		return objectFieldValue;
	}

	public void setObjectFieldValue(String objectFieldValue) {
		this.objectFieldValue = objectFieldValue;
	}

	public void setObjectFieldValue(
		UnsafeSupplier<String, Exception> objectFieldValueUnsafeSupplier) {

		try {
			objectFieldValue = objectFieldValueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String objectFieldValue;

	@Override
	public ObjectDefinitionValidationError clone()
		throws CloneNotSupportedException {

		return (ObjectDefinitionValidationError)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ObjectDefinitionValidationError)) {
			return false;
		}

		ObjectDefinitionValidationError objectDefinitionValidationError =
			(ObjectDefinitionValidationError)object;

		return Objects.equals(
			toString(), objectDefinitionValidationError.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ObjectDefinitionValidationErrorSerDes.toJSON(this);
	}

}