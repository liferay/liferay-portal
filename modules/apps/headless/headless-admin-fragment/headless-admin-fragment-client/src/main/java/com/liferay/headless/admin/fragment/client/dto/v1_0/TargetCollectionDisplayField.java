/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.TargetCollectionDisplayFieldSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class TargetCollectionDisplayField
	extends Field implements Cloneable, Serializable {

	public static TargetCollectionDisplayField toDTO(String json) {
		return TargetCollectionDisplayFieldSerDes.toDTO(json);
	}

	public TargetCollectionDisplayFragmentConfigurationFieldDefaultValue
		getDefaultValue() {

		return defaultValue;
	}

	public void setDefaultValue(
		TargetCollectionDisplayFragmentConfigurationFieldDefaultValue
			defaultValue) {

		this.defaultValue = defaultValue;
	}

	public void setDefaultValue(
		UnsafeSupplier
			<TargetCollectionDisplayFragmentConfigurationFieldDefaultValue,
			 Exception> defaultValueUnsafeSupplier) {

		try {
			defaultValue = defaultValueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected TargetCollectionDisplayFragmentConfigurationFieldDefaultValue
		defaultValue;

	public TargetCollectionDisplayTypeOptions getTypeOptions() {
		return typeOptions;
	}

	public void setTypeOptions(TargetCollectionDisplayTypeOptions typeOptions) {
		this.typeOptions = typeOptions;
	}

	public void setTypeOptions(
		UnsafeSupplier<TargetCollectionDisplayTypeOptions, Exception>
			typeOptionsUnsafeSupplier) {

		try {
			typeOptions = typeOptionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected TargetCollectionDisplayTypeOptions typeOptions;

	@Override
	public TargetCollectionDisplayField clone()
		throws CloneNotSupportedException {

		return (TargetCollectionDisplayField)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TargetCollectionDisplayField)) {
			return false;
		}

		TargetCollectionDisplayField targetCollectionDisplayField =
			(TargetCollectionDisplayField)object;

		return Objects.equals(
			toString(), targetCollectionDisplayField.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return TargetCollectionDisplayFieldSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:1232657142