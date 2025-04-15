/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.WidgetInstanceSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class WidgetInstance implements Cloneable, Serializable {

	public static WidgetInstance toDTO(String json) {
		return WidgetInstanceSerDes.toDTO(json);
	}

	public Map<String, Object> getWidgetConfig() {
		return widgetConfig;
	}

	public void setWidgetConfig(Map<String, Object> widgetConfig) {
		this.widgetConfig = widgetConfig;
	}

	public void setWidgetConfig(
		UnsafeSupplier<Map<String, Object>, Exception>
			widgetConfigUnsafeSupplier) {

		try {
			widgetConfig = widgetConfigUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Object> widgetConfig;

	public String getWidgetInstanceId() {
		return widgetInstanceId;
	}

	public void setWidgetInstanceId(String widgetInstanceId) {
		this.widgetInstanceId = widgetInstanceId;
	}

	public void setWidgetInstanceId(
		UnsafeSupplier<String, Exception> widgetInstanceIdUnsafeSupplier) {

		try {
			widgetInstanceId = widgetInstanceIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String widgetInstanceId;

	public String getWidgetName() {
		return widgetName;
	}

	public void setWidgetName(String widgetName) {
		this.widgetName = widgetName;
	}

	public void setWidgetName(
		UnsafeSupplier<String, Exception> widgetNameUnsafeSupplier) {

		try {
			widgetName = widgetNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String widgetName;

	public WidgetPermission[] getWidgetPermissions() {
		return widgetPermissions;
	}

	public void setWidgetPermissions(WidgetPermission[] widgetPermissions) {
		this.widgetPermissions = widgetPermissions;
	}

	public void setWidgetPermissions(
		UnsafeSupplier<WidgetPermission[], Exception>
			widgetPermissionsUnsafeSupplier) {

		try {
			widgetPermissions = widgetPermissionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected WidgetPermission[] widgetPermissions;

	@Override
	public WidgetInstance clone() throws CloneNotSupportedException {
		return (WidgetInstance)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WidgetInstance)) {
			return false;
		}

		WidgetInstance widgetInstance = (WidgetInstance)object;

		return Objects.equals(toString(), widgetInstance.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return WidgetInstanceSerDes.toJSON(this);
	}

}