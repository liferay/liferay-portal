/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.ColorPaletteFragmentConfigurationFieldDefaultValueSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class ColorPaletteFragmentConfigurationFieldDefaultValue
	implements Cloneable, Serializable {

	public static ColorPaletteFragmentConfigurationFieldDefaultValue toDTO(
		String json) {

		return ColorPaletteFragmentConfigurationFieldDefaultValueSerDes.toDTO(
			json);
	}

	public ColorPaletteValue getValue() {
		return value;
	}

	public void setValue(ColorPaletteValue value) {
		this.value = value;
	}

	public void setValue(
		UnsafeSupplier<ColorPaletteValue, Exception> valueUnsafeSupplier) {

		try {
			value = valueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ColorPaletteValue value;

	@Override
	public ColorPaletteFragmentConfigurationFieldDefaultValue clone()
		throws CloneNotSupportedException {

		return (ColorPaletteFragmentConfigurationFieldDefaultValue)
			super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof
				ColorPaletteFragmentConfigurationFieldDefaultValue)) {

			return false;
		}

		ColorPaletteFragmentConfigurationFieldDefaultValue
			colorPaletteFragmentConfigurationFieldDefaultValue =
				(ColorPaletteFragmentConfigurationFieldDefaultValue)object;

		return Objects.equals(
			toString(),
			colorPaletteFragmentConfigurationFieldDefaultValue.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ColorPaletteFragmentConfigurationFieldDefaultValueSerDes.toJSON(
			this);
	}

}
// LIFERAY-REST-BUILDER-HASH:1206151681