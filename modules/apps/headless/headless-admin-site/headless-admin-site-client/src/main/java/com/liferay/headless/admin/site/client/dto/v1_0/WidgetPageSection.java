/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.WidgetPageSectionSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class WidgetPageSection implements Cloneable, Serializable {

	public static WidgetPageSection toDTO(String json) {
		return WidgetPageSectionSerDes.toDTO(json);
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<String, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String id;

	public WidgetPageWidgetInstance[] getWidgetPageWidgetInstances() {
		return widgetPageWidgetInstances;
	}

	public void setWidgetPageWidgetInstances(
		WidgetPageWidgetInstance[] widgetPageWidgetInstances) {

		this.widgetPageWidgetInstances = widgetPageWidgetInstances;
	}

	public void setWidgetPageWidgetInstances(
		UnsafeSupplier<WidgetPageWidgetInstance[], Exception>
			widgetPageWidgetInstancesUnsafeSupplier) {

		try {
			widgetPageWidgetInstances =
				widgetPageWidgetInstancesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected WidgetPageWidgetInstance[] widgetPageWidgetInstances;

	@Override
	public WidgetPageSection clone() throws CloneNotSupportedException {
		return (WidgetPageSection)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WidgetPageSection)) {
			return false;
		}

		WidgetPageSection widgetPageSection = (WidgetPageSection)object;

		return Objects.equals(toString(), widgetPageSection.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return WidgetPageSectionSerDes.toJSON(this);
	}

}