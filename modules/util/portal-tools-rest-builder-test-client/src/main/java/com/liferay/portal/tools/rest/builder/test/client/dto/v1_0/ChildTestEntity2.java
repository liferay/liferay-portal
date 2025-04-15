/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.dto.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.function.UnsafeSupplier;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.ChildTestEntity2SerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class ChildTestEntity2
	extends TestEntity implements Cloneable, Serializable {

	public static ChildTestEntity2 toDTO(String json) {
		return ChildTestEntity2SerDes.toDTO(json);
	}

	public String getProperty2() {
		return property2;
	}

	public void setProperty2(String property2) {
		this.property2 = property2;
	}

	public void setProperty2(
		UnsafeSupplier<String, Exception> property2UnsafeSupplier) {

		try {
			property2 = property2UnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String property2;

	@Override
	public ChildTestEntity2 clone() throws CloneNotSupportedException {
		return (ChildTestEntity2)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ChildTestEntity2)) {
			return false;
		}

		ChildTestEntity2 childTestEntity2 = (ChildTestEntity2)object;

		return Objects.equals(toString(), childTestEntity2.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ChildTestEntity2SerDes.toJSON(this);
	}

}