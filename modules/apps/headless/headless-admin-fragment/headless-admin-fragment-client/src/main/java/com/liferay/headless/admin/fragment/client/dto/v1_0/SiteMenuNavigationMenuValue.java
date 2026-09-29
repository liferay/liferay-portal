/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.SiteMenuNavigationMenuValueSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class SiteMenuNavigationMenuValue
	extends NavigationMenuValue implements Cloneable, Serializable {

	public static SiteMenuNavigationMenuValue toDTO(String json) {
		return SiteMenuNavigationMenuValueSerDes.toDTO(json);
	}

	public ItemExternalReference getNavigationMenuItemExternalReference() {
		return navigationMenuItemExternalReference;
	}

	public void setNavigationMenuItemExternalReference(
		ItemExternalReference navigationMenuItemExternalReference) {

		this.navigationMenuItemExternalReference =
			navigationMenuItemExternalReference;
	}

	public void setNavigationMenuItemExternalReference(
		UnsafeSupplier<ItemExternalReference, Exception>
			navigationMenuItemExternalReferenceUnsafeSupplier) {

		try {
			navigationMenuItemExternalReference =
				navigationMenuItemExternalReferenceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ItemExternalReference navigationMenuItemExternalReference;

	public String getParentMenuItemExternalReferenceCode() {
		return parentMenuItemExternalReferenceCode;
	}

	public void setParentMenuItemExternalReferenceCode(
		String parentMenuItemExternalReferenceCode) {

		this.parentMenuItemExternalReferenceCode =
			parentMenuItemExternalReferenceCode;
	}

	public void setParentMenuItemExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			parentMenuItemExternalReferenceCodeUnsafeSupplier) {

		try {
			parentMenuItemExternalReferenceCode =
				parentMenuItemExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String parentMenuItemExternalReferenceCode;

	@Override
	public SiteMenuNavigationMenuValue clone()
		throws CloneNotSupportedException {

		return (SiteMenuNavigationMenuValue)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SiteMenuNavigationMenuValue)) {
			return false;
		}

		SiteMenuNavigationMenuValue siteMenuNavigationMenuValue =
			(SiteMenuNavigationMenuValue)object;

		return Objects.equals(
			toString(), siteMenuNavigationMenuValue.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SiteMenuNavigationMenuValueSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:-1007656110