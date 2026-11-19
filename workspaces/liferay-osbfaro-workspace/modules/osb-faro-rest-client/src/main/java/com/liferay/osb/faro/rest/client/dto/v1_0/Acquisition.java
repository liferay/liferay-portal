/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.dto.v1_0;

import com.liferay.osb.faro.rest.client.function.UnsafeSupplier;
import com.liferay.osb.faro.rest.client.serdes.v1_0.AcquisitionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public class Acquisition implements Cloneable, Serializable {

	public static Acquisition toDTO(String json) {
		return AcquisitionSerDes.toDTO(json);
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public void setCount(UnsafeSupplier<Long, Exception> countUnsafeSupplier) {
		try {
			count = countUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long count;

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
	public Acquisition clone() throws CloneNotSupportedException {
		return (Acquisition)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Acquisition)) {
			return false;
		}

		Acquisition acquisition = (Acquisition)object;

		return Objects.equals(toString(), acquisition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return AcquisitionSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1258644341