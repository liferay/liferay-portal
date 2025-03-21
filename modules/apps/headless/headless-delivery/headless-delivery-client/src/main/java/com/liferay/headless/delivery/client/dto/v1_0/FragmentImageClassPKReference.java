/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.FragmentImageClassPKReferenceSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class FragmentImageClassPKReference implements Cloneable, Serializable {

	public static FragmentImageClassPKReference toDTO(String json) {
		return FragmentImageClassPKReferenceSerDes.toDTO(json);
	}

	public Map<String, ClassPKReference> getClassPKReferences() {
		return classPKReferences;
	}

	public void setClassPKReferences(
		Map<String, ClassPKReference> classPKReferences) {

		this.classPKReferences = classPKReferences;
	}

	public void setClassPKReferences(
		UnsafeSupplier<Map<String, ClassPKReference>, Exception>
			classPKReferencesUnsafeSupplier) {

		try {
			classPKReferences = classPKReferencesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, ClassPKReference> classPKReferences;

	public FragmentImageConfiguration getFragmentImageConfiguration() {
		return fragmentImageConfiguration;
	}

	public void setFragmentImageConfiguration(
		FragmentImageConfiguration fragmentImageConfiguration) {

		this.fragmentImageConfiguration = fragmentImageConfiguration;
	}

	public void setFragmentImageConfiguration(
		UnsafeSupplier<FragmentImageConfiguration, Exception>
			fragmentImageConfigurationUnsafeSupplier) {

		try {
			fragmentImageConfiguration =
				fragmentImageConfigurationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FragmentImageConfiguration fragmentImageConfiguration;

	@Override
	public FragmentImageClassPKReference clone()
		throws CloneNotSupportedException {

		return (FragmentImageClassPKReference)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FragmentImageClassPKReference)) {
			return false;
		}

		FragmentImageClassPKReference fragmentImageClassPKReference =
			(FragmentImageClassPKReference)object;

		return Objects.equals(
			toString(), fragmentImageClassPKReference.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return FragmentImageClassPKReferenceSerDes.toJSON(this);
	}

}