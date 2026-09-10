/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.dto.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.function.UnsafeSupplier;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.OneOfPropertyTestEntitySerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class OneOfPropertyTestEntity implements Cloneable, Serializable {

	public static OneOfPropertyTestEntity toDTO(String json) {
		return OneOfPropertyTestEntitySerDes.toDTO(json);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String name;

	public Object getOneOfProperty() {
		return oneOfProperty;
	}

	public void setOneOfProperty(Object oneOfProperty) {
		this.oneOfProperty = oneOfProperty;
	}

	public void setOneOfProperty(
		UnsafeSupplier<Object, Exception> oneOfPropertyUnsafeSupplier) {

		try {
			oneOfProperty = oneOfPropertyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object oneOfProperty;

	@Override
	public OneOfPropertyTestEntity clone() throws CloneNotSupportedException {
		return (OneOfPropertyTestEntity)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof OneOfPropertyTestEntity)) {
			return false;
		}

		OneOfPropertyTestEntity oneOfPropertyTestEntity =
			(OneOfPropertyTestEntity)object;

		return Objects.equals(toString(), oneOfPropertyTestEntity.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return OneOfPropertyTestEntitySerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:1878674065