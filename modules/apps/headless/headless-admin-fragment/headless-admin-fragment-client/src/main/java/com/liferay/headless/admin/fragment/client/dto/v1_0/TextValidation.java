/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.TextValidationSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class TextValidation
	extends Validation implements Cloneable, Serializable {

	public static TextValidation toDTO(String json) {
		return TextValidationSerDes.toDTO(json);
	}

	public Long getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(Long maxLength) {
		this.maxLength = maxLength;
	}

	public void setMaxLength(
		UnsafeSupplier<Long, Exception> maxLengthUnsafeSupplier) {

		try {
			maxLength = maxLengthUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long maxLength;

	public Long getMinLength() {
		return minLength;
	}

	public void setMinLength(Long minLength) {
		this.minLength = minLength;
	}

	public void setMinLength(
		UnsafeSupplier<Long, Exception> minLengthUnsafeSupplier) {

		try {
			minLength = minLengthUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long minLength;

	@Override
	public TextValidation clone() throws CloneNotSupportedException {
		return (TextValidation)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TextValidation)) {
			return false;
		}

		TextValidation textValidation = (TextValidation)object;

		return Objects.equals(toString(), textValidation.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return TextValidationSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:717664961