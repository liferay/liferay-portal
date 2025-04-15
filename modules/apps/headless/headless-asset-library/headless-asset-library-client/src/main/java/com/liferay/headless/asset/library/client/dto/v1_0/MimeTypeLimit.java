/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.client.dto.v1_0;

import com.liferay.headless.asset.library.client.function.UnsafeSupplier;
import com.liferay.headless.asset.library.client.serdes.v1_0.MimeTypeLimitSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Roberto Díaz
 * @generated
 */
@Generated("")
public class MimeTypeLimit implements Cloneable, Serializable {

	public static MimeTypeLimit toDTO(String json) {
		return MimeTypeLimitSerDes.toDTO(json);
	}

	public Integer getMaximumSize() {
		return maximumSize;
	}

	public void setMaximumSize(Integer maximumSize) {
		this.maximumSize = maximumSize;
	}

	public void setMaximumSize(
		UnsafeSupplier<Integer, Exception> maximumSizeUnsafeSupplier) {

		try {
			maximumSize = maximumSizeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer maximumSize;

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public void setMimeType(
		UnsafeSupplier<String, Exception> mimeTypeUnsafeSupplier) {

		try {
			mimeType = mimeTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String mimeType;

	@Override
	public MimeTypeLimit clone() throws CloneNotSupportedException {
		return (MimeTypeLimit)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof MimeTypeLimit)) {
			return false;
		}

		MimeTypeLimit mimeTypeLimit = (MimeTypeLimit)object;

		return Objects.equals(toString(), mimeTypeLimit.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return MimeTypeLimitSerDes.toJSON(this);
	}

}