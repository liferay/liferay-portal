/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.dto.v1_0;

import com.liferay.headless.delivery.client.function.UnsafeSupplier;
import com.liferay.headless.delivery.client.serdes.v1_0.PageFragmentDropZoneDefinitionSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class PageFragmentDropZoneDefinition implements Cloneable, Serializable {

	public static PageFragmentDropZoneDefinition toDTO(String json) {
		return PageFragmentDropZoneDefinitionSerDes.toDTO(json);
	}

	public String getFragmentDropZoneId() {
		return fragmentDropZoneId;
	}

	public void setFragmentDropZoneId(String fragmentDropZoneId) {
		this.fragmentDropZoneId = fragmentDropZoneId;
	}

	public void setFragmentDropZoneId(
		UnsafeSupplier<String, Exception> fragmentDropZoneIdUnsafeSupplier) {

		try {
			fragmentDropZoneId = fragmentDropZoneIdUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String fragmentDropZoneId;

	@Override
	public PageFragmentDropZoneDefinition clone()
		throws CloneNotSupportedException {

		return (PageFragmentDropZoneDefinition)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PageFragmentDropZoneDefinition)) {
			return false;
		}

		PageFragmentDropZoneDefinition pageFragmentDropZoneDefinition =
			(PageFragmentDropZoneDefinition)object;

		return Objects.equals(
			toString(), pageFragmentDropZoneDefinition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PageFragmentDropZoneDefinitionSerDes.toJSON(this);
	}

}