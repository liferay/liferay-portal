/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.CollectionFragmentConfigurationFieldDefaultValueSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class CollectionFragmentConfigurationFieldDefaultValue
	implements Cloneable, Serializable {

	public static CollectionFragmentConfigurationFieldDefaultValue toDTO(
		String json) {

		return CollectionFragmentConfigurationFieldDefaultValueSerDes.toDTO(
			json);
	}

	public CollectionReference getValue() {
		return value;
	}

	public void setValue(CollectionReference value) {
		this.value = value;
	}

	public void setValue(
		UnsafeSupplier<CollectionReference, Exception> valueUnsafeSupplier) {

		try {
			value = valueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected CollectionReference value;

	@Override
	public CollectionFragmentConfigurationFieldDefaultValue clone()
		throws CloneNotSupportedException {

		return (CollectionFragmentConfigurationFieldDefaultValue)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof
				CollectionFragmentConfigurationFieldDefaultValue)) {

			return false;
		}

		CollectionFragmentConfigurationFieldDefaultValue
			collectionFragmentConfigurationFieldDefaultValue =
				(CollectionFragmentConfigurationFieldDefaultValue)object;

		return Objects.equals(
			toString(),
			collectionFragmentConfigurationFieldDefaultValue.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return CollectionFragmentConfigurationFieldDefaultValueSerDes.toJSON(
			this);
	}

}
// LIFERAY-REST-BUILDER-HASH:1353771727