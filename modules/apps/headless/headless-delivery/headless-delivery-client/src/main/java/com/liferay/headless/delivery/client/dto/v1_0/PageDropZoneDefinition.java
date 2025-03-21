/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.PageDropZoneDefinitionSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class PageDropZoneDefinition implements Cloneable, Serializable {

	public static PageDropZoneDefinition toDTO(String json) {
		return PageDropZoneDefinitionSerDes.toDTO(json);
	}

	public Object getFragmentSettings() {
		return fragmentSettings;
	}

	public void setFragmentSettings(Object fragmentSettings) {
		this.fragmentSettings = fragmentSettings;
	}

	public void setFragmentSettings(
		UnsafeSupplier<Object, Exception> fragmentSettingsUnsafeSupplier) {

		try {
			fragmentSettings = fragmentSettingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object fragmentSettings;

	@Override
	public PageDropZoneDefinition clone() throws CloneNotSupportedException {
		return (PageDropZoneDefinition)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PageDropZoneDefinition)) {
			return false;
		}

		PageDropZoneDefinition pageDropZoneDefinition =
			(PageDropZoneDefinition)object;

		return Objects.equals(toString(), pageDropZoneDefinition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PageDropZoneDefinitionSerDes.toJSON(this);
	}

}