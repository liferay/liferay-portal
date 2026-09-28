/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.CollectionSelectorFieldSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class CollectionSelectorField
	extends Field implements Cloneable, Serializable {

	public static CollectionSelectorField toDTO(String json) {
		return CollectionSelectorFieldSerDes.toDTO(json);
	}

	public CollectionFragmentConfigurationFieldDefaultValue getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(
		CollectionFragmentConfigurationFieldDefaultValue defaultValue) {

		this.defaultValue = defaultValue;
	}

	public void setDefaultValue(
		UnsafeSupplier
			<CollectionFragmentConfigurationFieldDefaultValue, Exception>
				defaultValueUnsafeSupplier) {

		try {
			defaultValue = defaultValueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected CollectionFragmentConfigurationFieldDefaultValue defaultValue;

	public CollectionSelectorTypeOptions getTypeOptions() {
		return typeOptions;
	}

	public void setTypeOptions(CollectionSelectorTypeOptions typeOptions) {
		this.typeOptions = typeOptions;
	}

	public void setTypeOptions(
		UnsafeSupplier<CollectionSelectorTypeOptions, Exception>
			typeOptionsUnsafeSupplier) {

		try {
			typeOptions = typeOptionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected CollectionSelectorTypeOptions typeOptions;

	@Override
	public CollectionSelectorField clone() throws CloneNotSupportedException {
		return (CollectionSelectorField)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof CollectionSelectorField)) {
			return false;
		}

		CollectionSelectorField collectionSelectorField =
			(CollectionSelectorField)object;

		return Objects.equals(toString(), collectionSelectorField.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return CollectionSelectorFieldSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-77096923