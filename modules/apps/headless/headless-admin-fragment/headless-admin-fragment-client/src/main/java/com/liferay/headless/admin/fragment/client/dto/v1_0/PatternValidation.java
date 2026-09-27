/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.PatternValidationSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class PatternValidation
	extends Validation implements Cloneable, Serializable {

	public static PatternValidation toDTO(String json) {
		return PatternValidationSerDes.toDTO(json);
	}

	public String getRegexp() {
		return regexp;
	}

	public void setRegexp(String regexp) {
		this.regexp = regexp;
	}

	public void setRegexp(
		UnsafeSupplier<String, Exception> regexpUnsafeSupplier) {

		try {
			regexp = regexpUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String regexp;

	@Override
	public PatternValidation clone() throws CloneNotSupportedException {
		return (PatternValidation)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PatternValidation)) {
			return false;
		}

		PatternValidation patternValidation = (PatternValidation)object;

		return Objects.equals(toString(), patternValidation.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PatternValidationSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1786912150