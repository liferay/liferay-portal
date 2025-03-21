/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.FragmentSettingsUnallowedSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class FragmentSettingsUnallowed implements Cloneable, Serializable {

	public static FragmentSettingsUnallowed toDTO(String json) {
		return FragmentSettingsUnallowedSerDes.toDTO(json);
	}

	public Fragment[] getUnallowedFragments() {
		return unallowedFragments;
	}

	public void setUnallowedFragments(Fragment[] unallowedFragments) {
		this.unallowedFragments = unallowedFragments;
	}

	public void setUnallowedFragments(
		UnsafeSupplier<Fragment[], Exception>
			unallowedFragmentsUnsafeSupplier) {

		try {
			unallowedFragments = unallowedFragmentsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Fragment[] unallowedFragments;

	@Override
	public FragmentSettingsUnallowed clone() throws CloneNotSupportedException {
		return (FragmentSettingsUnallowed)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FragmentSettingsUnallowed)) {
			return false;
		}

		FragmentSettingsUnallowed fragmentSettingsUnallowed =
			(FragmentSettingsUnallowed)object;

		return Objects.equals(toString(), fragmentSettingsUnallowed.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FragmentSettingsUnallowedSerDes.toJSON(this);
	}

}