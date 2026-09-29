/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.NavigationMenuSelectorFieldSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class NavigationMenuSelectorField
	extends Field implements Cloneable, Serializable {

	public static NavigationMenuSelectorField toDTO(String json) {
		return NavigationMenuSelectorFieldSerDes.toDTO(json);
	}

	public NavigationMenuFragmentConfigurationFieldDefaultValue
		getDefaultValue() {

		return defaultValue;
	}

	public void setDefaultValue(
		NavigationMenuFragmentConfigurationFieldDefaultValue defaultValue) {

		this.defaultValue = defaultValue;
	}

	public void setDefaultValue(
		UnsafeSupplier
			<NavigationMenuFragmentConfigurationFieldDefaultValue, Exception>
				defaultValueUnsafeSupplier) {

		try {
			defaultValue = defaultValueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected NavigationMenuFragmentConfigurationFieldDefaultValue defaultValue;

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
	public NavigationMenuSelectorField clone()
		throws CloneNotSupportedException {

		return (NavigationMenuSelectorField)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof NavigationMenuSelectorField)) {
			return false;
		}

		NavigationMenuSelectorField navigationMenuSelectorField =
			(NavigationMenuSelectorField)object;

		return Objects.equals(
			toString(), navigationMenuSelectorField.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return NavigationMenuSelectorFieldSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1738224400