/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.MappingSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class Mapping implements Cloneable, Serializable {

	public static Mapping toDTO(String json) {
		return MappingSerDes.toDTO(json);
	}

	public String getFieldKey() {
		return fieldKey;
	}

	public void setFieldKey(String fieldKey) {
		this.fieldKey = fieldKey;
	}

	public void setFieldKey(
		UnsafeSupplier<String, Exception> fieldKeyUnsafeSupplier) {

		try {
			fieldKey = fieldKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String fieldKey;

	public Object getItemReference() {
		return itemReference;
	}

	public void setItemReference(Object itemReference) {
		this.itemReference = itemReference;
	}

	public void setItemReference(
		UnsafeSupplier<Object, Exception> itemReferenceUnsafeSupplier) {

		try {
			itemReference = itemReferenceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object itemReference;

	@Override
	public Mapping clone() throws CloneNotSupportedException {
		return (Mapping)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Mapping)) {
			return false;
		}

		Mapping mapping = (Mapping)object;

		return Objects.equals(toString(), mapping.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return MappingSerDes.toJSON(this);
	}

}