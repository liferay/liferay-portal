/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.CategoryFragmentConfigurationFieldDefaultValueSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class CategoryFragmentConfigurationFieldDefaultValue
	implements Cloneable, Serializable {

	public static CategoryFragmentConfigurationFieldDefaultValue toDTO(
		String json) {

		return CategoryFragmentConfigurationFieldDefaultValueSerDes.toDTO(json);
	}

	public ItemExternalReference getValue() {
		return value;
	}

	public void setValue(ItemExternalReference value) {
		this.value = value;
	}

	public void setValue(
		UnsafeSupplier<ItemExternalReference, Exception> valueUnsafeSupplier) {

		try {
			value = valueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ItemExternalReference value;

	@Override
	public CategoryFragmentConfigurationFieldDefaultValue clone()
		throws CloneNotSupportedException {

		return (CategoryFragmentConfigurationFieldDefaultValue)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof
				CategoryFragmentConfigurationFieldDefaultValue)) {

			return false;
		}

		CategoryFragmentConfigurationFieldDefaultValue
			categoryFragmentConfigurationFieldDefaultValue =
				(CategoryFragmentConfigurationFieldDefaultValue)object;

		return Objects.equals(
			toString(),
			categoryFragmentConfigurationFieldDefaultValue.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return CategoryFragmentConfigurationFieldDefaultValueSerDes.toJSON(
			this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-850148465