/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.ModulePageElementDefinitionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class ModulePageElementDefinition
	extends PageElementDefinition implements Cloneable, Serializable {

	public static ModulePageElementDefinition toDTO(String json) {
		return ModulePageElementDefinitionSerDes.toDTO(json);
	}

	public ModuleViewport[] getModuleViewports() {
		return moduleViewports;
	}

	public void setModuleViewports(ModuleViewport[] moduleViewports) {
		this.moduleViewports = moduleViewports;
	}

	public void setModuleViewports(
		UnsafeSupplier<ModuleViewport[], Exception>
			moduleViewportsUnsafeSupplier) {

		try {
			moduleViewports = moduleViewportsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ModuleViewport[] moduleViewports;

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public void setSize(UnsafeSupplier<Integer, Exception> sizeUnsafeSupplier) {
		try {
			size = sizeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Integer size;

	@Override
	public ModulePageElementDefinition clone()
		throws CloneNotSupportedException {

		return (ModulePageElementDefinition)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ModulePageElementDefinition)) {
			return false;
		}

		ModulePageElementDefinition modulePageElementDefinition =
			(ModulePageElementDefinition)object;

		return Objects.equals(
			toString(), modulePageElementDefinition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return ModulePageElementDefinitionSerDes.toJSON(this);
	}

}