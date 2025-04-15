/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.ClientExtensionSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class ClientExtension implements Cloneable, Serializable {

	public static ClientExtension toDTO(String json) {
		return ClientExtensionSerDes.toDTO(json);
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
	public ClientExtension clone() throws CloneNotSupportedException {
		return (ClientExtension)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ClientExtension)) {
			return false;
		}

		ClientExtension clientExtension = (ClientExtension)object;

		return Objects.equals(toString(), clientExtension.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ClientExtensionSerDes.toJSON(this);
	}

}