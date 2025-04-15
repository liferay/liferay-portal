/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.dto.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.function.UnsafeSupplier;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.EntityModelResourceTestEntity1SerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class EntityModelResourceTestEntity1 implements Cloneable, Serializable {

	public static EntityModelResourceTestEntity1 toDTO(String json) {
		return EntityModelResourceTestEntity1SerDes.toDTO(json);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

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

	@Override
	public EntityModelResourceTestEntity1 clone()
		throws CloneNotSupportedException {

		return (EntityModelResourceTestEntity1)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof EntityModelResourceTestEntity1)) {
			return false;
		}

		EntityModelResourceTestEntity1 entityModelResourceTestEntity1 =
			(EntityModelResourceTestEntity1)object;

		return Objects.equals(
			toString(), entityModelResourceTestEntity1.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return EntityModelResourceTestEntity1SerDes.toJSON(this);
	}

}