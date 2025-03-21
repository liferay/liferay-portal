/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.dto.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.ChildTestEntity3SerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class ChildTestEntity3
	extends TestEntity implements Cloneable, Serializable {

	public static ChildTestEntity3 toDTO(String json) {
		return ChildTestEntity3SerDes.toDTO(json);
	}

	@Override
	public ChildTestEntity3 clone() throws CloneNotSupportedException {
		return (ChildTestEntity3)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ChildTestEntity3)) {
			return false;
		}

		ChildTestEntity3 childTestEntity3 = (ChildTestEntity3)object;

		return Objects.equals(toString(), childTestEntity3.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ChildTestEntity3SerDes.toJSON(this);
	}

}