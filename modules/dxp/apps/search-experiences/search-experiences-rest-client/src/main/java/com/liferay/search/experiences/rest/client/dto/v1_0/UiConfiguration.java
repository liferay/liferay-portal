/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.dto.v1_0;

import com.liferay.search.experiences.rest.client.function.UnsafeSupplier;
import com.liferay.search.experiences.rest.client.serdes.v1_0.UiConfigurationSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class UiConfiguration implements Cloneable, Serializable {

	public static UiConfiguration toDTO(String json) {
		return UiConfigurationSerDes.toDTO(json);
	}

	public FieldSet[] getFieldSets() {
		return fieldSets;
	}

	public void setFieldSets(FieldSet[] fieldSets) {
		this.fieldSets = fieldSets;
	}

	public void setFieldSets(
		UnsafeSupplier<FieldSet[], Exception> fieldSetsUnsafeSupplier) {

		try {
			fieldSets = fieldSetsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FieldSet[] fieldSets;

	@Override
	public UiConfiguration clone() throws CloneNotSupportedException {
		return (UiConfiguration)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof UiConfiguration)) {
			return false;
		}

		UiConfiguration uiConfiguration = (UiConfiguration)object;

		return Objects.equals(toString(), uiConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return UiConfigurationSerDes.toJSON(this);
	}

}