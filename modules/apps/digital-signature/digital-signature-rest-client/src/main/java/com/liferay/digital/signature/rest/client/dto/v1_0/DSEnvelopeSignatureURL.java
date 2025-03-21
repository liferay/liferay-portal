/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.rest.client.dto.v1_0;

import com.liferay.digital.signature.rest.client.function.UnsafeSupplier;
import com.liferay.digital.signature.rest.client.serdes.v1_0.DSEnvelopeSignatureURLSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author José Abelenda
 * @generated
 */
@Generated("")
public class DSEnvelopeSignatureURL implements Cloneable, Serializable {

	public static DSEnvelopeSignatureURL toDTO(String json) {
		return DSEnvelopeSignatureURLSerDes.toDTO(json);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUrl(UnsafeSupplier<String, Exception> urlUnsafeSupplier) {
		try {
			url = urlUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String url;

	@Override
	public DSEnvelopeSignatureURL clone() throws CloneNotSupportedException {
		return (DSEnvelopeSignatureURL)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DSEnvelopeSignatureURL)) {
			return false;
		}

		DSEnvelopeSignatureURL dsEnvelopeSignatureURL =
			(DSEnvelopeSignatureURL)object;

		return Objects.equals(toString(), dsEnvelopeSignatureURL.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return DSEnvelopeSignatureURLSerDes.toJSON(this);
	}

}