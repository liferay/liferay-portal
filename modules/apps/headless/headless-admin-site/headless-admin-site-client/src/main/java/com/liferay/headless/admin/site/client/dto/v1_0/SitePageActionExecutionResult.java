/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.SitePageActionExecutionResultSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class SitePageActionExecutionResult implements Cloneable, Serializable {

	public static SitePageActionExecutionResult toDTO(String json) {
		return SitePageActionExecutionResultSerDes.toDTO(json);
	}

	public ItemExternalReference getItemReference() {
		return itemReference;
	}

	public void setItemReference(ItemExternalReference itemReference) {
		this.itemReference = itemReference;
	}

	public void setItemReference(
		UnsafeSupplier<ItemExternalReference, Exception>
			itemReferenceUnsafeSupplier) {

		try {
			itemReference = itemReferenceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ItemExternalReference itemReference;

	@Override
	public SitePageActionExecutionResult clone()
		throws CloneNotSupportedException {

		return (SitePageActionExecutionResult)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SitePageActionExecutionResult)) {
			return false;
		}

		SitePageActionExecutionResult sitePageActionExecutionResult =
			(SitePageActionExecutionResult)object;

		return Objects.equals(
			toString(), sitePageActionExecutionResult.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SitePageActionExecutionResultSerDes.toJSON(this);
	}

}