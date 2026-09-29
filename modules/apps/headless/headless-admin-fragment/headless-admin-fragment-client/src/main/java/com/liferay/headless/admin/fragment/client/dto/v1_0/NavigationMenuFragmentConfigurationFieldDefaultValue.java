/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.NavigationMenuFragmentConfigurationFieldDefaultValueSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class NavigationMenuFragmentConfigurationFieldDefaultValue
	implements Cloneable, Serializable {

	public static NavigationMenuFragmentConfigurationFieldDefaultValue toDTO(
		String json) {

		return NavigationMenuFragmentConfigurationFieldDefaultValueSerDes.toDTO(
			json);
	}

	public NavigationMenuValue getValue() {
		return value;
	}

	public void setValue(NavigationMenuValue value) {
		this.value = value;
	}

	public void setValue(
		UnsafeSupplier<NavigationMenuValue, Exception> valueUnsafeSupplier) {

		try {
			value = valueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected NavigationMenuValue value;

	@Override
	public NavigationMenuFragmentConfigurationFieldDefaultValue clone()
		throws CloneNotSupportedException {

		return (NavigationMenuFragmentConfigurationFieldDefaultValue)
			super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof
				NavigationMenuFragmentConfigurationFieldDefaultValue)) {

			return false;
		}

		NavigationMenuFragmentConfigurationFieldDefaultValue
			navigationMenuFragmentConfigurationFieldDefaultValue =
				(NavigationMenuFragmentConfigurationFieldDefaultValue)object;

		return Objects.equals(
			toString(),
			navigationMenuFragmentConfigurationFieldDefaultValue.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return NavigationMenuFragmentConfigurationFieldDefaultValueSerDes.
			toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-797078517