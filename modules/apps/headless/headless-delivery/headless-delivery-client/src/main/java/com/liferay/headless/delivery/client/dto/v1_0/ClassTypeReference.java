/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.ClassTypeReferenceSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class ClassTypeReference implements Cloneable, Serializable {

	public static ClassTypeReference toDTO(String json) {
		return ClassTypeReferenceSerDes.toDTO(json);
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

	public Long getClassType() {
		return classType;
	}

	public void setClassType(Long classType) {
		this.classType = classType;
	}

	public void setClassType(
		UnsafeSupplier<Long, Exception> classTypeUnsafeSupplier) {

		try {
			classType = classTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long classType;

	@Override
	public ClassTypeReference clone() throws CloneNotSupportedException {
		return (ClassTypeReference)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ClassTypeReference)) {
			return false;
		}

		ClassTypeReference classTypeReference = (ClassTypeReference)object;

		return Objects.equals(toString(), classTypeReference.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ClassTypeReferenceSerDes.toJSON(this);
	}

}