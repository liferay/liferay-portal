/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.CheckboxFieldSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class CheckboxField extends Field implements Cloneable, Serializable {

	public static CheckboxField toDTO(String json) {
		return CheckboxFieldSerDes.toDTO(json);
	}

	public CheckboxFragmentConfigurationFieldDefaultValue getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(
		CheckboxFragmentConfigurationFieldDefaultValue defaultValue) {

		this.defaultValue = defaultValue;
	}

	public void setDefaultValue(
		UnsafeSupplier
			<CheckboxFragmentConfigurationFieldDefaultValue, Exception>
				defaultValueUnsafeSupplier) {

		try {
			defaultValue = defaultValueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected CheckboxFragmentConfigurationFieldDefaultValue defaultValue;

	public CheckboxTypeOptions getTypeOptions() {
		return typeOptions;
	}

	public void setTypeOptions(CheckboxTypeOptions typeOptions) {
		this.typeOptions = typeOptions;
	}

	public void setTypeOptions(
		UnsafeSupplier<CheckboxTypeOptions, Exception>
			typeOptionsUnsafeSupplier) {

		try {
			typeOptions = typeOptionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected CheckboxTypeOptions typeOptions;

	@Override
	public CheckboxField clone() throws CloneNotSupportedException {
		return (CheckboxField)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CheckboxField)) {
			return false;
		}

		CheckboxField checkboxField = (CheckboxField)object;

		return Objects.equals(toString(), checkboxField.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return CheckboxFieldSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:1083228702