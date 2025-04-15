/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.dto.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.function.UnsafeSupplier;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.UnreferencedTestEntitySerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class UnreferencedTestEntity implements Cloneable, Serializable {

	public static UnreferencedTestEntity toDTO(String json) {
		return UnreferencedTestEntitySerDes.toDTO(json);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDescription(
		UnsafeSupplier<String, Exception> descriptionUnsafeSupplier) {

		try {
			description = descriptionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String description;

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

	public String getPropertyWithHyphens() {
		return propertyWithHyphens;
	}

	public void setPropertyWithHyphens(String propertyWithHyphens) {
		this.propertyWithHyphens = propertyWithHyphens;
	}

	public void setPropertyWithHyphens(
		UnsafeSupplier<String, Exception> propertyWithHyphensUnsafeSupplier) {

		try {
			propertyWithHyphens = propertyWithHyphensUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String propertyWithHyphens;

	@Override
	public UnreferencedTestEntity clone() throws CloneNotSupportedException {
		return (UnreferencedTestEntity)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof UnreferencedTestEntity)) {
			return false;
		}

		UnreferencedTestEntity unreferencedTestEntity =
			(UnreferencedTestEntity)object;

		return Objects.equals(toString(), unreferencedTestEntity.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return UnreferencedTestEntitySerDes.toJSON(this);
	}

}