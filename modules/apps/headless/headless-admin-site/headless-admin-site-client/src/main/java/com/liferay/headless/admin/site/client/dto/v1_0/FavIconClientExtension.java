/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.FavIconClientExtensionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class FavIconClientExtension
	extends FavIcon implements Cloneable, Serializable {

	public static FavIconClientExtension toDTO(String json) {
		return FavIconClientExtensionSerDes.toDTO(json);
	}

	public Map<String, String> getClientExtensionConfig() {
		return clientExtensionConfig;
	}

	public void setClientExtensionConfig(
		Map<String, String> clientExtensionConfig) {

		this.clientExtensionConfig = clientExtensionConfig;
	}

	public void setClientExtensionConfig(
		UnsafeSupplier<Map<String, String>, Exception>
			clientExtensionConfigUnsafeSupplier) {

		try {
			clientExtensionConfig = clientExtensionConfigUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> clientExtensionConfig;

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

	@Override
	public FavIconClientExtension clone() throws CloneNotSupportedException {
		return (FavIconClientExtension)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FavIconClientExtension)) {
			return false;
		}

		FavIconClientExtension favIconClientExtension =
			(FavIconClientExtension)object;

		return Objects.equals(toString(), favIconClientExtension.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FavIconClientExtensionSerDes.toJSON(this);
	}

}