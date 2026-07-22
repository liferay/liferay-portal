/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.dto.v1_0;

import com.liferay.bulk.rest.client.function.UnsafeSupplier;
import com.liferay.bulk.rest.client.serdes.v1_0.AddObjectToProjectBulkSelectionActionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class AddObjectToProjectBulkSelectionAction
	extends BulkAction implements Cloneable, Serializable {

	public static AddObjectToProjectBulkSelectionAction toDTO(String json) {
		return AddObjectToProjectBulkSelectionActionSerDes.toDTO(json);
	}

	public String[] getProjectScopeKeys() {
		return projectScopeKeys;
	}

	public void setProjectScopeKeys(String[] projectScopeKeys) {
		this.projectScopeKeys = projectScopeKeys;
	}

	public void setProjectScopeKeys(
		UnsafeSupplier<String[], Exception> projectScopeKeysUnsafeSupplier) {

		try {
			projectScopeKeys = projectScopeKeysUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] projectScopeKeys;

	@Override
	public AddObjectToProjectBulkSelectionAction clone()
		throws CloneNotSupportedException {

		return (AddObjectToProjectBulkSelectionAction)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof AddObjectToProjectBulkSelectionAction)) {
			return false;
		}

		AddObjectToProjectBulkSelectionAction
			addObjectToProjectBulkSelectionAction =
				(AddObjectToProjectBulkSelectionAction)object;

		return Objects.equals(
			toString(), addObjectToProjectBulkSelectionAction.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return AddObjectToProjectBulkSelectionActionSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-193945308