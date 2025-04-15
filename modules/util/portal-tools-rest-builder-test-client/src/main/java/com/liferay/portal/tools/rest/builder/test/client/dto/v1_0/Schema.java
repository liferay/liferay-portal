/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.dto.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.function.UnsafeSupplier;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.SchemaSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class Schema implements Cloneable, Serializable {

	public static Schema toDTO(String json) {
		return SchemaSerDes.toDTO(json);
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
	public Schema clone() throws CloneNotSupportedException {
		return (Schema)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Schema)) {
			return false;
		}

		Schema schema = (Schema)object;

		return Objects.equals(toString(), schema.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SchemaSerDes.toJSON(this);
	}

}