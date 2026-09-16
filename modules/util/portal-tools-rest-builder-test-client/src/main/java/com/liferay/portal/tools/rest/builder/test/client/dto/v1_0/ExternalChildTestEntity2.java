/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.dto.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.function.UnsafeSupplier;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.ExternalChildTestEntity2SerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class ExternalChildTestEntity2
	extends ExternalTestEntity1 implements Cloneable, Serializable {

	public static ExternalChildTestEntity2 toDTO(String json) {
		return ExternalChildTestEntity2SerDes.toDTO(json);
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
	public ExternalChildTestEntity2 clone() throws CloneNotSupportedException {
		return (ExternalChildTestEntity2)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ExternalChildTestEntity2)) {
			return false;
		}

		ExternalChildTestEntity2 externalChildTestEntity2 =
			(ExternalChildTestEntity2)object;

		return Objects.equals(toString(), externalChildTestEntity2.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ExternalChildTestEntity2SerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-332464900