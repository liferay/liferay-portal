/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.TargetCollectionDisplayTypeOptionsSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class TargetCollectionDisplayTypeOptions
	implements Cloneable, Serializable {

	public static TargetCollectionDisplayTypeOptions toDTO(String json) {
		return TargetCollectionDisplayTypeOptionsSerDes.toDTO(json);
	}

	public Map<String, Dependency> getDependency() {
		return dependency;
	}

	public void setDependency(Map<String, Dependency> dependency) {
		this.dependency = dependency;
	}

	public void setDependency(
		UnsafeSupplier<Map<String, Dependency>, Exception>
			dependencyUnsafeSupplier) {

		try {
			dependency = dependencyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Dependency> dependency;

	public Boolean getEnableCompatibleCollections() {
		return enableCompatibleCollections;
	}

	public void setEnableCompatibleCollections(
		Boolean enableCompatibleCollections) {

		this.enableCompatibleCollections = enableCompatibleCollections;
	}

	public void setEnableCompatibleCollections(
		UnsafeSupplier<Boolean, Exception>
			enableCompatibleCollectionsUnsafeSupplier) {

		try {
			enableCompatibleCollections =
				enableCompatibleCollectionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean enableCompatibleCollections;

	@Override
	public TargetCollectionDisplayTypeOptions clone()
		throws CloneNotSupportedException {

		return (TargetCollectionDisplayTypeOptions)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TargetCollectionDisplayTypeOptions)) {
			return false;
		}

		TargetCollectionDisplayTypeOptions targetCollectionDisplayTypeOptions =
			(TargetCollectionDisplayTypeOptions)object;

		return Objects.equals(
			toString(), targetCollectionDisplayTypeOptions.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return TargetCollectionDisplayTypeOptionsSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:1165270589