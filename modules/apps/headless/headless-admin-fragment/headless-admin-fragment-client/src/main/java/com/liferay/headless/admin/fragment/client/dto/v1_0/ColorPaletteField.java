/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.ColorPaletteFieldSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class ColorPaletteField
	extends Field implements Cloneable, Serializable {

	public static ColorPaletteField toDTO(String json) {
		return ColorPaletteFieldSerDes.toDTO(json);
	}

	public ColorPaletteFragmentConfigurationFieldDefaultValue
		getDefaultValue() {

		return defaultValue;
	}

	public void setDefaultValue(
		ColorPaletteFragmentConfigurationFieldDefaultValue defaultValue) {

		this.defaultValue = defaultValue;
	}

	public void setDefaultValue(
		UnsafeSupplier
			<ColorPaletteFragmentConfigurationFieldDefaultValue, Exception>
				defaultValueUnsafeSupplier) {

		try {
			defaultValue = defaultValueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ColorPaletteFragmentConfigurationFieldDefaultValue defaultValue;

	public TypeOptions getTypeOptions() {
		return typeOptions;
	}

	public void setTypeOptions(TypeOptions typeOptions) {
		this.typeOptions = typeOptions;
	}

	public void setTypeOptions(
		UnsafeSupplier<TypeOptions, Exception> typeOptionsUnsafeSupplier) {

		try {
			typeOptions = typeOptionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected TypeOptions typeOptions;

	@Override
	public ColorPaletteField clone() throws CloneNotSupportedException {
		return (ColorPaletteField)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ColorPaletteField)) {
			return false;
		}

		ColorPaletteField colorPaletteField = (ColorPaletteField)object;

		return Objects.equals(toString(), colorPaletteField.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ColorPaletteFieldSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:427699475