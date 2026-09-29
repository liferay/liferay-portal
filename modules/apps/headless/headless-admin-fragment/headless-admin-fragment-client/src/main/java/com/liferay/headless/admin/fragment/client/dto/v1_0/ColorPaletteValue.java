/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.ColorPaletteValueSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class ColorPaletteValue implements Cloneable, Serializable {

	public static ColorPaletteValue toDTO(String json) {
		return ColorPaletteValueSerDes.toDTO(json);
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void setColor(
		UnsafeSupplier<String, Exception> colorUnsafeSupplier) {

		try {
			color = colorUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String color;

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public void setCssClass(
		UnsafeSupplier<String, Exception> cssClassUnsafeSupplier) {

		try {
			cssClass = cssClassUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String cssClass;

	public String getRgbValue() {
		return rgbValue;
	}

	public void setRgbValue(String rgbValue) {
		this.rgbValue = rgbValue;
	}

	public void setRgbValue(
		UnsafeSupplier<String, Exception> rgbValueUnsafeSupplier) {

		try {
			rgbValue = rgbValueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String rgbValue;

	@Override
	public ColorPaletteValue clone() throws CloneNotSupportedException {
		return (ColorPaletteValue)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ColorPaletteValue)) {
			return false;
		}

		ColorPaletteValue colorPaletteValue = (ColorPaletteValue)object;

		return Objects.equals(toString(), colorPaletteValue.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ColorPaletteValueSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1964719865