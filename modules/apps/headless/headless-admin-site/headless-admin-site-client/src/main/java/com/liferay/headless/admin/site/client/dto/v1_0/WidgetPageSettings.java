/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.WidgetPageSettingsSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class WidgetPageSettings
	extends PageSettings implements Cloneable, Serializable {

	public static WidgetPageSettings toDTO(String json) {
		return WidgetPageSettingsSerDes.toDTO(json);
	}

	public Boolean getCustomizable() {
		return customizable;
	}

	public void setCustomizable(Boolean customizable) {
		this.customizable = customizable;
	}

	public void setCustomizable(
		UnsafeSupplier<Boolean, Exception> customizableUnsafeSupplier) {

		try {
			customizable = customizableUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean customizable;

	public String[] getCustomizableSectionIds() {
		return customizableSectionIds;
	}

	public void setCustomizableSectionIds(String[] customizableSectionIds) {
		this.customizableSectionIds = customizableSectionIds;
	}

	public void setCustomizableSectionIds(
		UnsafeSupplier<String[], Exception>
			customizableSectionIdsUnsafeSupplier) {

		try {
			customizableSectionIds = customizableSectionIdsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String[] customizableSectionIds;

	public Boolean getInheritChanges() {
		return inheritChanges;
	}

	public void setInheritChanges(Boolean inheritChanges) {
		this.inheritChanges = inheritChanges;
	}

	public void setInheritChanges(
		UnsafeSupplier<Boolean, Exception> inheritChangesUnsafeSupplier) {

		try {
			inheritChanges = inheritChangesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean inheritChanges;

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

	public ItemExternalReference getWidgetPageTemplateReference() {
		return widgetPageTemplateReference;
	}

	public void setWidgetPageTemplateReference(
		ItemExternalReference widgetPageTemplateReference) {

		this.widgetPageTemplateReference = widgetPageTemplateReference;
	}

	public void setWidgetPageTemplateReference(
		UnsafeSupplier<ItemExternalReference, Exception>
			widgetPageTemplateReferenceUnsafeSupplier) {

		try {
			widgetPageTemplateReference =
				widgetPageTemplateReferenceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ItemExternalReference widgetPageTemplateReference;

	@Override
	public WidgetPageSettings clone() throws CloneNotSupportedException {
		return (WidgetPageSettings)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WidgetPageSettings)) {
			return false;
		}

		WidgetPageSettings widgetPageSettings = (WidgetPageSettings)object;

		return Objects.equals(toString(), widgetPageSettings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return WidgetPageSettingsSerDes.toJSON(this);
	}

}