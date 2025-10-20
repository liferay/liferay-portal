/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.ModuleViewportSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class ModuleViewport implements Cloneable, Serializable {

	public static ModuleViewport toDTO(String json) {
		return ModuleViewportSerDes.toDTO(json);
	}

	public Id getId() {
		return id;
	}

	public String getIdAsString() {
		if (id == null) {
			return null;
		}

		return id.toString();
	}

	public void setId(Id id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Id, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Id id;

	public ModuleViewportDefinition getModuleViewportDefinition() {
		return moduleViewportDefinition;
	}

	public void setModuleViewportDefinition(
		ModuleViewportDefinition moduleViewportDefinition) {

		this.moduleViewportDefinition = moduleViewportDefinition;
	}

	public void setModuleViewportDefinition(
		UnsafeSupplier<ModuleViewportDefinition, Exception>
			moduleViewportDefinitionUnsafeSupplier) {

		try {
			moduleViewportDefinition =
				moduleViewportDefinitionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ModuleViewportDefinition moduleViewportDefinition;

	@Override
	public ModuleViewport clone() throws CloneNotSupportedException {
		return (ModuleViewport)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ModuleViewport)) {
			return false;
		}

		ModuleViewport moduleViewport = (ModuleViewport)object;

		return Objects.equals(toString(), moduleViewport.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ModuleViewportSerDes.toJSON(this);
	}

	public static enum Id {

		LANDSCAPE_MOBILE("LandscapeMobile"), PORTRAIT_MOBILE("PortraitMobile"),
		TABLET("Tablet");

		public static Id create(String value) {
			for (Id id : values()) {
				if (Objects.equals(id.getValue(), value) ||
					Objects.equals(id.name(), value)) {

					return id;
				}
			}

			return null;
		}

		public String getValue() {
			return _value;
		}

		@Override
		public String toString() {
			return _value;
		}

		private Id(String value) {
			_value = value;
		}

		private final String _value;

	}

}