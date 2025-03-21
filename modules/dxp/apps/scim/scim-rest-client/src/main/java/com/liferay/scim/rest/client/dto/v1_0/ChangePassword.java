/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.dto.v1_0;

import com.liferay.scim.rest.client.function.UnsafeSupplier;
import com.liferay.scim.rest.client.serdes.v1_0.ChangePasswordSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class ChangePassword implements Cloneable, Serializable {

	public static ChangePassword toDTO(String json) {
		return ChangePasswordSerDes.toDTO(json);
	}

	public Boolean getSupported() {
		return supported;
	}

	public void setSupported(Boolean supported) {
		this.supported = supported;
	}

	public void setSupported(
		UnsafeSupplier<Boolean, Exception> supportedUnsafeSupplier) {

		try {
			supported = supportedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean supported;

	@Override
	public ChangePassword clone() throws CloneNotSupportedException {
		return (ChangePassword)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ChangePassword)) {
			return false;
		}

		ChangePassword changePassword = (ChangePassword)object;

		return Objects.equals(toString(), changePassword.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ChangePasswordSerDes.toJSON(this);
	}

}