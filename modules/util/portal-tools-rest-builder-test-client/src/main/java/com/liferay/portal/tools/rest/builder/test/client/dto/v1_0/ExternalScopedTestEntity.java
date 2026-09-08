/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.dto.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.function.UnsafeSupplier;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.ExternalScopedTestEntitySerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class ExternalScopedTestEntity implements Cloneable, Serializable {

	public static ExternalScopedTestEntity toDTO(String json) {
		return ExternalScopedTestEntitySerDes.toDTO(json);
	}

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

	public com.liferay.portal.tools.rest.builder.test.client.scope.Scope
		getScope() {

		return scope;
	}

	public void setScope(
		com.liferay.portal.tools.rest.builder.test.client.scope.Scope scope) {

		this.scope = scope;
	}

	public void setScope(
		UnsafeSupplier
			<com.liferay.portal.tools.rest.builder.test.client.scope.Scope,
			 Exception> scopeUnsafeSupplier) {

		try {
			scope = scopeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected com.liferay.portal.tools.rest.builder.test.client.scope.Scope
		scope;

	@Override
	public ExternalScopedTestEntity clone() throws CloneNotSupportedException {
		return (ExternalScopedTestEntity)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ExternalScopedTestEntity)) {
			return false;
		}

		ExternalScopedTestEntity externalScopedTestEntity =
			(ExternalScopedTestEntity)object;

		return Objects.equals(toString(), externalScopedTestEntity.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ExternalScopedTestEntitySerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-810702464