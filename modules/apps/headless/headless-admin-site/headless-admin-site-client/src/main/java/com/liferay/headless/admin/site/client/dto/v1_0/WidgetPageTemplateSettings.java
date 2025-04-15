/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.WidgetPageTemplateSettingsSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class WidgetPageTemplateSettings
	extends PageTemplateSettings implements Cloneable, Serializable {

	public static WidgetPageTemplateSettings toDTO(String json) {
		return WidgetPageTemplateSettingsSerDes.toDTO(json);
	}

	public String getLayoutTemplateId() {
		return layoutTemplateId;
	}

	public void setLayoutTemplateId(String layoutTemplateId) {
		this.layoutTemplateId = layoutTemplateId;
	}

	public void setLayoutTemplateId(
		UnsafeSupplier<String, Exception> layoutTemplateIdUnsafeSupplier) {

		try {
			layoutTemplateId = layoutTemplateIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String layoutTemplateId;

	public NavigationMenuSettings getNavigationMenuSettings() {
		return navigationMenuSettings;
	}

	public void setNavigationMenuSettings(
		NavigationMenuSettings navigationMenuSettings) {

		this.navigationMenuSettings = navigationMenuSettings;
	}

	public void setNavigationMenuSettings(
		UnsafeSupplier<NavigationMenuSettings, Exception>
			navigationMenuSettingsUnsafeSupplier) {

		try {
			navigationMenuSettings = navigationMenuSettingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected NavigationMenuSettings navigationMenuSettings;

	@Override
	public WidgetPageTemplateSettings clone()
		throws CloneNotSupportedException {

		return (WidgetPageTemplateSettings)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WidgetPageTemplateSettings)) {
			return false;
		}

		WidgetPageTemplateSettings widgetPageTemplateSettings =
			(WidgetPageTemplateSettings)object;

		return Objects.equals(
			toString(), widgetPageTemplateSettings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return WidgetPageTemplateSettingsSerDes.toJSON(this);
	}

}