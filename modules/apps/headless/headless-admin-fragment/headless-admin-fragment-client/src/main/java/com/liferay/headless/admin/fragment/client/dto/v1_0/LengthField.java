/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.LengthFieldSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class LengthField extends Field implements Cloneable, Serializable {

	public static LengthField toDTO(String json) {
		return LengthFieldSerDes.toDTO(json);
	}

	public LengthFragmentConfigurationFieldDefaultValue getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(
		LengthFragmentConfigurationFieldDefaultValue defaultValue) {

		this.defaultValue = defaultValue;
	}

	public void setDefaultValue(
		UnsafeSupplier<LengthFragmentConfigurationFieldDefaultValue, Exception>
			defaultValueUnsafeSupplier) {

		try {
			defaultValue = defaultValueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected LengthFragmentConfigurationFieldDefaultValue defaultValue;

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
	public LengthField clone() throws CloneNotSupportedException {
		return (LengthField)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof LengthField)) {
			return false;
		}

		LengthField lengthField = (LengthField)object;

		return Objects.equals(toString(), lengthField.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return LengthFieldSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:754326827