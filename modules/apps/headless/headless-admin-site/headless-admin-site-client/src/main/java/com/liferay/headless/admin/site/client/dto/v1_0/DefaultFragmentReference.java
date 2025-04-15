/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.DefaultFragmentReferenceSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class DefaultFragmentReference implements Cloneable, Serializable {

	public static DefaultFragmentReference toDTO(String json) {
		return DefaultFragmentReferenceSerDes.toDTO(json);
	}

	public String getDefaultFragmentKey() {
		return defaultFragmentKey;
	}

	public void setDefaultFragmentKey(String defaultFragmentKey) {
		this.defaultFragmentKey = defaultFragmentKey;
	}

	public void setDefaultFragmentKey(
		UnsafeSupplier<String, Exception> defaultFragmentKeyUnsafeSupplier) {

		try {
			defaultFragmentKey = defaultFragmentKeyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String defaultFragmentKey;

	@Override
	public DefaultFragmentReference clone() throws CloneNotSupportedException {
		return (DefaultFragmentReference)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DefaultFragmentReference)) {
			return false;
		}

		DefaultFragmentReference defaultFragmentReference =
			(DefaultFragmentReference)object;

		return Objects.equals(toString(), defaultFragmentReference.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return DefaultFragmentReferenceSerDes.toJSON(this);
	}

}