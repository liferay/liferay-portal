/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bulk.rest.client.dto.v1_0;

import com.liferay.bulk.rest.client.function.UnsafeSupplier;
import com.liferay.bulk.rest.client.serdes.v1_0.DefaultPermissionBulkActionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class DefaultPermissionBulkAction
	extends BulkAction implements Cloneable, Serializable {

	public static DefaultPermissionBulkAction toDTO(String json) {
		return DefaultPermissionBulkActionSerDes.toDTO(json);
	}

	public String getDefaultPermissions() {
		return defaultPermissions;
	}

	public void setDefaultPermissions(String defaultPermissions) {
		this.defaultPermissions = defaultPermissions;
	}

	public void setDefaultPermissions(
		UnsafeSupplier<String, Exception> defaultPermissionsUnsafeSupplier) {

		try {
			defaultPermissions = defaultPermissionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String defaultPermissions;

	public Long getDepotGroupId() {
		return depotGroupId;
	}

	public void setDepotGroupId(Long depotGroupId) {
		this.depotGroupId = depotGroupId;
	}

	public void setDepotGroupId(
		UnsafeSupplier<Long, Exception> depotGroupIdUnsafeSupplier) {

		try {
			depotGroupId = depotGroupIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long depotGroupId;

	public String getTreePath() {
		return treePath;
	}

	public void setTreePath(String treePath) {
		this.treePath = treePath;
	}

	public void setTreePath(
		UnsafeSupplier<String, Exception> treePathUnsafeSupplier) {

		try {
			treePath = treePathUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String treePath;

	@Override
	public DefaultPermissionBulkAction clone()
		throws CloneNotSupportedException {

		return (DefaultPermissionBulkAction)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DefaultPermissionBulkAction)) {
			return false;
		}

		DefaultPermissionBulkAction defaultPermissionBulkAction =
			(DefaultPermissionBulkAction)object;

		return Objects.equals(
			toString(), defaultPermissionBulkAction.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return DefaultPermissionBulkActionSerDes.toJSON(this);
	}

}