/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.TextFragmentConfigurationFieldDefaultValueSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class TextFragmentConfigurationFieldDefaultValue
	implements Cloneable, Serializable {

	public static TextFragmentConfigurationFieldDefaultValue toDTO(
		String json) {

		return TextFragmentConfigurationFieldDefaultValueSerDes.toDTO(json);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setValue(
		UnsafeSupplier<String, Exception> valueUnsafeSupplier) {

		try {
			value = valueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String value;

	@Override
	public TextFragmentConfigurationFieldDefaultValue clone()
		throws CloneNotSupportedException {

		return (TextFragmentConfigurationFieldDefaultValue)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TextFragmentConfigurationFieldDefaultValue)) {
			return false;
		}

		TextFragmentConfigurationFieldDefaultValue
			textFragmentConfigurationFieldDefaultValue =
				(TextFragmentConfigurationFieldDefaultValue)object;

		return Objects.equals(
			toString(), textFragmentConfigurationFieldDefaultValue.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return TextFragmentConfigurationFieldDefaultValueSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-155065001