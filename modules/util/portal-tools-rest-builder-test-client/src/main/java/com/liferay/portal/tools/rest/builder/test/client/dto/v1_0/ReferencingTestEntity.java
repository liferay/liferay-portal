/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.dto.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.function.UnsafeSupplier;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.ReferencingTestEntitySerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class ReferencingTestEntity implements Cloneable, Serializable {

	public static ReferencingTestEntity toDTO(String json) {
		return ReferencingTestEntitySerDes.toDTO(json);
	}

	public ExternalScopedTestEntity getExternalScopedTestEntity() {
		return externalScopedTestEntity;
	}

	public void setExternalScopedTestEntity(
		ExternalScopedTestEntity externalScopedTestEntity) {

		this.externalScopedTestEntity = externalScopedTestEntity;
	}

	public void setExternalScopedTestEntity(
		UnsafeSupplier<ExternalScopedTestEntity, Exception>
			externalScopedTestEntityUnsafeSupplier) {

		try {
			externalScopedTestEntity =
				externalScopedTestEntityUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ExternalScopedTestEntity externalScopedTestEntity;

	@Override
	public ReferencingTestEntity clone() throws CloneNotSupportedException {
		return (ReferencingTestEntity)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ReferencingTestEntity)) {
			return false;
		}

		ReferencingTestEntity referencingTestEntity =
			(ReferencingTestEntity)object;

		return Objects.equals(toString(), referencingTestEntity.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ReferencingTestEntitySerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:717412057