/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.client.dto.v1_0;

import com.liferay.headless.commerce.admin.account.client.function.UnsafeSupplier;
import com.liferay.headless.commerce.admin.account.client.serdes.v1_0.Body_1SerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public class Body_1 implements Cloneable, Serializable {

	public static Body_1 toDTO(String json) {
		return Body_1SerDes.toDTO(json);
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public void setLogo(UnsafeSupplier<String, Exception> logoUnsafeSupplier) {
		try {
			logo = logoUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String logo;

	@Override
	public Body_1 clone() throws CloneNotSupportedException {
		return (Body_1)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Body_1)) {
			return false;
		}

		Body_1 body_1 = (Body_1)object;

		return Objects.equals(toString(), body_1.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return Body_1SerDes.toJSON(this);
	}

}