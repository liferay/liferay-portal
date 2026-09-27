/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.CheckboxFragmentConfigurationFieldDefaultValueSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class CheckboxFragmentConfigurationFieldDefaultValue
	implements Cloneable, Serializable {

	public static CheckboxFragmentConfigurationFieldDefaultValue toDTO(
		String json) {

		return CheckboxFragmentConfigurationFieldDefaultValueSerDes.toDTO(json);
	}

	public Boolean getValue() {
		return value;
	}

	public void setValue(Boolean value) {
		this.value = value;
	}

	public void setValue(
		UnsafeSupplier<Boolean, Exception> valueUnsafeSupplier) {

		try {
			value = valueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean value;

	@Override
	public CheckboxFragmentConfigurationFieldDefaultValue clone()
		throws CloneNotSupportedException {

		return (CheckboxFragmentConfigurationFieldDefaultValue)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof
				CheckboxFragmentConfigurationFieldDefaultValue)) {

			return false;
		}

		CheckboxFragmentConfigurationFieldDefaultValue
			checkboxFragmentConfigurationFieldDefaultValue =
				(CheckboxFragmentConfigurationFieldDefaultValue)object;

		return Objects.equals(
			toString(),
			checkboxFragmentConfigurationFieldDefaultValue.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return CheckboxFragmentConfigurationFieldDefaultValueSerDes.toJSON(
			this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-528046285