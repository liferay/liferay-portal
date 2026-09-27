/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.TextFieldSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class TextField extends Field implements Cloneable, Serializable {

	public static TextField toDTO(String json) {
		return TextFieldSerDes.toDTO(json);
	}

	public TextFragmentConfigurationFieldDefaultValue getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(
		TextFragmentConfigurationFieldDefaultValue defaultValue) {

		this.defaultValue = defaultValue;
	}

	public void setDefaultValue(
		UnsafeSupplier<TextFragmentConfigurationFieldDefaultValue, Exception>
			defaultValueUnsafeSupplier) {

		try {
			defaultValue = defaultValueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected TextFragmentConfigurationFieldDefaultValue defaultValue;

	public TextTypeOptions getTypeOptions() {
		return typeOptions;
	}

	public void setTypeOptions(TextTypeOptions typeOptions) {
		this.typeOptions = typeOptions;
	}

	public void setTypeOptions(
		UnsafeSupplier<TextTypeOptions, Exception> typeOptionsUnsafeSupplier) {

		try {
			typeOptions = typeOptionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected TextTypeOptions typeOptions;

	@Override
	public TextField clone() throws CloneNotSupportedException {
		return (TextField)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TextField)) {
			return false;
		}

		TextField textField = (TextField)object;

		return Objects.equals(toString(), textField.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return TextFieldSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1734822050