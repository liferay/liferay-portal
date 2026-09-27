/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.NumberValidationSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class NumberValidation
	extends Validation implements Cloneable, Serializable {

	public static NumberValidation toDTO(String json) {
		return NumberValidationSerDes.toDTO(json);
	}

	public Long getMax() {
		return max;
	}

	public void setMax(Long max) {
		this.max = max;
	}

	public void setMax(UnsafeSupplier<Long, Exception> maxUnsafeSupplier) {
		try {
			max = maxUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long max;

	public Long getMin() {
		return min;
	}

	public void setMin(Long min) {
		this.min = min;
	}

	public void setMin(UnsafeSupplier<Long, Exception> minUnsafeSupplier) {
		try {
			min = minUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long min;

	@Override
	public NumberValidation clone() throws CloneNotSupportedException {
		return (NumberValidation)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof NumberValidation)) {
			return false;
		}

		NumberValidation numberValidation = (NumberValidation)object;

		return Objects.equals(toString(), numberValidation.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return NumberValidationSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-988857859