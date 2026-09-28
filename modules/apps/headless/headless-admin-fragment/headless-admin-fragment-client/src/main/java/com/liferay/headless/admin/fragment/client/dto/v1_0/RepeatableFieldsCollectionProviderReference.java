/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.RepeatableFieldsCollectionProviderReferenceSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class RepeatableFieldsCollectionProviderReference
	extends CollectionReference implements Cloneable, Serializable {

	public static RepeatableFieldsCollectionProviderReference toDTO(
		String json) {

		return RepeatableFieldsCollectionProviderReferenceSerDes.toDTO(json);
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

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public void setFieldName(
		UnsafeSupplier<String, Exception> fieldNameUnsafeSupplier) {

		try {
			fieldName = fieldNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String fieldName;

	public ItemExternalReference getSubTypeExternalReference() {
		return subTypeExternalReference;
	}

	public void setSubTypeExternalReference(
		ItemExternalReference subTypeExternalReference) {

		this.subTypeExternalReference = subTypeExternalReference;
	}

	public void setSubTypeExternalReference(
		UnsafeSupplier<ItemExternalReference, Exception>
			subTypeExternalReferenceUnsafeSupplier) {

		try {
			subTypeExternalReference =
				subTypeExternalReferenceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ItemExternalReference subTypeExternalReference;

	@Override
	public RepeatableFieldsCollectionProviderReference clone()
		throws CloneNotSupportedException {

		return (RepeatableFieldsCollectionProviderReference)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof RepeatableFieldsCollectionProviderReference)) {
			return false;
		}

		RepeatableFieldsCollectionProviderReference
			repeatableFieldsCollectionProviderReference =
				(RepeatableFieldsCollectionProviderReference)object;

		return Objects.equals(
			toString(), repeatableFieldsCollectionProviderReference.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return RepeatableFieldsCollectionProviderReferenceSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1203062321