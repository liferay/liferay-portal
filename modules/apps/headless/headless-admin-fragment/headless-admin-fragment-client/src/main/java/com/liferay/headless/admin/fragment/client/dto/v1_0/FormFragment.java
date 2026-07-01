/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.constant.v1_0.FieldType;
import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.FormFragmentSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class FormFragment extends Fragment implements Cloneable, Serializable {

	public static FormFragment toDTO(String json) {
		return FormFragmentSerDes.toDTO(json);
	}

	public FieldType[] getFieldTypes() {
		return fieldTypes;
	}

	public void setFieldTypes(FieldType[] fieldTypes) {
		this.fieldTypes = fieldTypes;
	}

	public void setFieldTypes(
		UnsafeSupplier<FieldType[], Exception> fieldTypesUnsafeSupplier) {

		try {
			fieldTypes = fieldTypesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FieldType[] fieldTypes;

	@Override
	public FormFragment clone() throws CloneNotSupportedException {
		return (FormFragment)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FormFragment)) {
			return false;
		}

		FormFragment formFragment = (FormFragment)object;

		return Objects.equals(toString(), formFragment.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FormFragmentSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:1427590116