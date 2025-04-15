/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.dto.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.function.UnsafeSupplier;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.ChildTestEntity1SerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class ChildTestEntity1
	extends TestEntity implements Cloneable, Serializable {

	public static ChildTestEntity1 toDTO(String json) {
		return ChildTestEntity1SerDes.toDTO(json);
	}

	public String getProperty1() {
		return property1;
	}

	public void setProperty1(String property1) {
		this.property1 = property1;
	}

	public void setProperty1(
		UnsafeSupplier<String, Exception> property1UnsafeSupplier) {

		try {
			property1 = property1UnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String property1;

	@Override
	public ChildTestEntity1 clone() throws CloneNotSupportedException {
		return (ChildTestEntity1)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ChildTestEntity1)) {
			return false;
		}

		ChildTestEntity1 childTestEntity1 = (ChildTestEntity1)object;

		return Objects.equals(toString(), childTestEntity1.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ChildTestEntity1SerDes.toJSON(this);
	}

}