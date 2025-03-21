/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.dto.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.function.UnsafeSupplier;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.JSONMapAttributeTestEntitySerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class JSONMapAttributeTestEntity implements Cloneable, Serializable {

	public static JSONMapAttributeTestEntity toDTO(String json) {
		return JSONMapAttributeTestEntitySerDes.toDTO(json);
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

	public Map<String, Object> getProperties1() {
		return properties1;
	}

	public void setProperties1(Map<String, Object> properties1) {
		this.properties1 = properties1;
	}

	public void setProperties1(
		UnsafeSupplier<Map<String, Object>, Exception>
			properties1UnsafeSupplier) {

		try {
			properties1 = properties1UnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Object> properties1;

	public Map<String, Object> getProperties2() {
		return properties2;
	}

	public void setProperties2(Map<String, Object> properties2) {
		this.properties2 = properties2;
	}

	public void setProperties2(
		UnsafeSupplier<Map<String, Object>, Exception>
			properties2UnsafeSupplier) {

		try {
			properties2 = properties2UnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Object> properties2;

	@Override
	public JSONMapAttributeTestEntity clone()
		throws CloneNotSupportedException {

		return (JSONMapAttributeTestEntity)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof JSONMapAttributeTestEntity)) {
			return false;
		}

		JSONMapAttributeTestEntity jsonMapAttributeTestEntity =
			(JSONMapAttributeTestEntity)object;

		return Objects.equals(
			toString(), jsonMapAttributeTestEntity.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return JSONMapAttributeTestEntitySerDes.toJSON(this);
	}

}