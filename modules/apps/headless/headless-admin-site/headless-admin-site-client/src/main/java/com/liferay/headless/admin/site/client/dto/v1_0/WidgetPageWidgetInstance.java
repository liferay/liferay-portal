/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.WidgetPageWidgetInstanceSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class WidgetPageWidgetInstance implements Cloneable, Serializable {

	public static WidgetPageWidgetInstance toDTO(String json) {
		return WidgetPageWidgetInstanceSerDes.toDTO(json);
	}

	public String getExternalReferenceCode() {
		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;
	}

	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		try {
			externalReferenceCode = externalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String externalReferenceCode;

	public String getParentSectionId() {
		return parentSectionId;
	}

	public void setParentSectionId(String parentSectionId) {
		this.parentSectionId = parentSectionId;
	}

	public void setParentSectionId(
		UnsafeSupplier<String, Exception> parentSectionIdUnsafeSupplier) {

		try {
			parentSectionId = parentSectionIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String parentSectionId;

	public String getParentWidgetInstanceExternalReferenceCode() {
		return parentWidgetInstanceExternalReferenceCode;
	}

	public void setParentWidgetInstanceExternalReferenceCode(
		String parentWidgetInstanceExternalReferenceCode) {

		this.parentWidgetInstanceExternalReferenceCode =
			parentWidgetInstanceExternalReferenceCode;
	}

	public void setParentWidgetInstanceExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			parentWidgetInstanceExternalReferenceCodeUnsafeSupplier) {

		try {
			parentWidgetInstanceExternalReferenceCode =
				parentWidgetInstanceExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String parentWidgetInstanceExternalReferenceCode;

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public void setPosition(
		UnsafeSupplier<Integer, Exception> positionUnsafeSupplier) {

		try {
			position = positionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer position;

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

	public WidgetLookAndFeelConfig[] getWidgetLookAndFeelConfig() {
		return widgetLookAndFeelConfig;
	}

	public void setWidgetLookAndFeelConfig(
		WidgetLookAndFeelConfig[] widgetLookAndFeelConfig) {

		this.widgetLookAndFeelConfig = widgetLookAndFeelConfig;
	}

	public void setWidgetLookAndFeelConfig(
		UnsafeSupplier<WidgetLookAndFeelConfig[], Exception>
			widgetLookAndFeelConfigUnsafeSupplier) {

		try {
			widgetLookAndFeelConfig =
				widgetLookAndFeelConfigUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected WidgetLookAndFeelConfig[] widgetLookAndFeelConfig;

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
	public WidgetPageWidgetInstance clone() throws CloneNotSupportedException {
		return (WidgetPageWidgetInstance)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WidgetPageWidgetInstance)) {
			return false;
		}

		WidgetPageWidgetInstance widgetPageWidgetInstance =
			(WidgetPageWidgetInstance)object;

		return Objects.equals(toString(), widgetPageWidgetInstance.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return WidgetPageWidgetInstanceSerDes.toJSON(this);
	}

}