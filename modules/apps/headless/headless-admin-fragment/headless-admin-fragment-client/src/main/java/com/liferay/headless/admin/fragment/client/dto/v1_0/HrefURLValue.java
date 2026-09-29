/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.HrefURLValueSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class HrefURLValue extends URLValue implements Cloneable, Serializable {

	public static HrefURLValue toDTO(String json) {
		return HrefURLValueSerDes.toDTO(json);
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public void setHref(UnsafeSupplier<String, Exception> hrefUnsafeSupplier) {
		try {
			href = hrefUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String href;

	@Override
	public HrefURLValue clone() throws CloneNotSupportedException {
		return (HrefURLValue)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof HrefURLValue)) {
			return false;
		}

		HrefURLValue hrefURLValue = (HrefURLValue)object;

		return Objects.equals(toString(), hrefURLValue.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return HrefURLValueSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:1702350779