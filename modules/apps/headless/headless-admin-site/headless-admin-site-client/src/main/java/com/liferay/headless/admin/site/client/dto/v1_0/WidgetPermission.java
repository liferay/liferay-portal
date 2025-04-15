/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.WidgetPermissionSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class WidgetPermission implements Cloneable, Serializable {

	public static WidgetPermission toDTO(String json) {
		return WidgetPermissionSerDes.toDTO(json);
	}

	public String[] getActionIds() {
		return actionIds;
	}

	public void setActionIds(String[] actionIds) {
		this.actionIds = actionIds;
	}

	public void setActionIds(
		UnsafeSupplier<String[], Exception> actionIdsUnsafeSupplier) {

		try {
			actionIds = actionIdsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] actionIds;

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public void setRoleName(
		UnsafeSupplier<String, Exception> roleNameUnsafeSupplier) {

		try {
			roleName = roleNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String roleName;

	@Override
	public WidgetPermission clone() throws CloneNotSupportedException {
		return (WidgetPermission)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WidgetPermission)) {
			return false;
		}

		WidgetPermission widgetPermission = (WidgetPermission)object;

		return Objects.equals(toString(), widgetPermission.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return WidgetPermissionSerDes.toJSON(this);
	}

}