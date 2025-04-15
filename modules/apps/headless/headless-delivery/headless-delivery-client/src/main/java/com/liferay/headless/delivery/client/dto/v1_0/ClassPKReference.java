/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.ClassPKReferenceSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class ClassPKReference implements Cloneable, Serializable {

	public static ClassPKReference toDTO(String json) {
		return ClassPKReferenceSerDes.toDTO(json);
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

	public Long getClassPK() {
		return classPK;
	}

	public void setClassPK(Long classPK) {
		this.classPK = classPK;
	}

	public void setClassPK(
		UnsafeSupplier<Long, Exception> classPKUnsafeSupplier) {

		try {
			classPK = classPKUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long classPK;

	@Override
	public ClassPKReference clone() throws CloneNotSupportedException {
		return (ClassPKReference)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ClassPKReference)) {
			return false;
		}

		ClassPKReference classPKReference = (ClassPKReference)object;

		return Objects.equals(toString(), classPKReference.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ClassPKReferenceSerDes.toJSON(this);
	}

}