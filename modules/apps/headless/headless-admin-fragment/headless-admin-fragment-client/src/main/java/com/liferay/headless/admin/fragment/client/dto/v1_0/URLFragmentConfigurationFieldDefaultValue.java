/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.URLFragmentConfigurationFieldDefaultValueSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class URLFragmentConfigurationFieldDefaultValue
	implements Cloneable, Serializable {

	public static URLFragmentConfigurationFieldDefaultValue toDTO(String json) {
		return URLFragmentConfigurationFieldDefaultValueSerDes.toDTO(json);
	}

	public URLValue getValue() {
		return value;
	}

	public void setValue(URLValue value) {
		this.value = value;
	}

	public void setValue(
		UnsafeSupplier<URLValue, Exception> valueUnsafeSupplier) {

		try {
			value = valueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected URLValue value;

	@Override
	public URLFragmentConfigurationFieldDefaultValue clone()
		throws CloneNotSupportedException {

		return (URLFragmentConfigurationFieldDefaultValue)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof URLFragmentConfigurationFieldDefaultValue)) {
			return false;
		}

		URLFragmentConfigurationFieldDefaultValue
			urlFragmentConfigurationFieldDefaultValue =
				(URLFragmentConfigurationFieldDefaultValue)object;

		return Objects.equals(
			toString(), urlFragmentConfigurationFieldDefaultValue.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return URLFragmentConfigurationFieldDefaultValueSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1940438535