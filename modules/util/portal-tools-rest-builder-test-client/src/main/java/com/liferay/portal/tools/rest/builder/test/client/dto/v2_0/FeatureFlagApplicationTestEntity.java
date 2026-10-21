/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.dto.v2_0;

import com.liferay.portal.tools.rest.builder.test.client.function.UnsafeSupplier;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v2_0.FeatureFlagApplicationTestEntitySerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class FeatureFlagApplicationTestEntity
	implements Cloneable, Serializable {

	public static FeatureFlagApplicationTestEntity toDTO(String json) {
		return FeatureFlagApplicationTestEntitySerDes.toDTO(json);
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
	public FeatureFlagApplicationTestEntity clone()
		throws CloneNotSupportedException {

		return (FeatureFlagApplicationTestEntity)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FeatureFlagApplicationTestEntity)) {
			return false;
		}

		FeatureFlagApplicationTestEntity featureFlagApplicationTestEntity =
			(FeatureFlagApplicationTestEntity)object;

		return Objects.equals(
			toString(), featureFlagApplicationTestEntity.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FeatureFlagApplicationTestEntitySerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:493450801