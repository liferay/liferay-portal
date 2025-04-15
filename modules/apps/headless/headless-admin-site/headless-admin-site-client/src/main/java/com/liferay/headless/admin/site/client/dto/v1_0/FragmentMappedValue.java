/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.FragmentMappedValueSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class FragmentMappedValue implements Cloneable, Serializable {

	public static FragmentMappedValue toDTO(String json) {
		return FragmentMappedValueSerDes.toDTO(json);
	}

	public Mapping getMapping() {
		return mapping;
	}

	public void setMapping(Mapping mapping) {
		this.mapping = mapping;
	}

	public void setMapping(
		UnsafeSupplier<Mapping, Exception> mappingUnsafeSupplier) {

		try {
			mapping = mappingUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Mapping mapping;

	@Override
	public FragmentMappedValue clone() throws CloneNotSupportedException {
		return (FragmentMappedValue)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FragmentMappedValue)) {
			return false;
		}

		FragmentMappedValue fragmentMappedValue = (FragmentMappedValue)object;

		return Objects.equals(toString(), fragmentMappedValue.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FragmentMappedValueSerDes.toJSON(this);
	}

}