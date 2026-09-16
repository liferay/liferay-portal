/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.dto.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.function.UnsafeSupplier;
import com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0.ExternalChildTestEntity1SerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class ExternalChildTestEntity1
	extends ExternalTestEntity1 implements Cloneable, Serializable {

	public static ExternalChildTestEntity1 toDTO(String json) {
		return ExternalChildTestEntity1SerDes.toDTO(json);
	}

	public String getExternalProperty() {
		return externalProperty;
	}

	public void setExternalProperty(String externalProperty) {
		this.externalProperty = externalProperty;
	}

	public void setExternalProperty(
		UnsafeSupplier<String, Exception> externalPropertyUnsafeSupplier) {

		try {
			externalProperty = externalPropertyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalProperty;

	@Override
	public ExternalChildTestEntity1 clone() throws CloneNotSupportedException {
		return (ExternalChildTestEntity1)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ExternalChildTestEntity1)) {
			return false;
		}

		ExternalChildTestEntity1 externalChildTestEntity1 =
			(ExternalChildTestEntity1)object;

		return Objects.equals(toString(), externalChildTestEntity1.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ExternalChildTestEntity1SerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-280265678