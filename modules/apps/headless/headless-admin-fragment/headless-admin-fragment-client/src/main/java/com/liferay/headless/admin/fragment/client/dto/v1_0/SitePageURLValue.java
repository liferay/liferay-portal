/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.SitePageURLValueSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class SitePageURLValue
	extends URLValue implements Cloneable, Serializable {

	public static SitePageURLValue toDTO(String json) {
		return SitePageURLValueSerDes.toDTO(json);
	}

	public ItemExternalReference getSitePageItemExternalReference() {
		return sitePageItemExternalReference;
	}

	public void setSitePageItemExternalReference(
		ItemExternalReference sitePageItemExternalReference) {

		this.sitePageItemExternalReference = sitePageItemExternalReference;
	}

	public void setSitePageItemExternalReference(
		UnsafeSupplier<ItemExternalReference, Exception>
			sitePageItemExternalReferenceUnsafeSupplier) {

		try {
			sitePageItemExternalReference =
				sitePageItemExternalReferenceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ItemExternalReference sitePageItemExternalReference;

	@Override
	public SitePageURLValue clone() throws CloneNotSupportedException {
		return (SitePageURLValue)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SitePageURLValue)) {
			return false;
		}

		SitePageURLValue sitePageURLValue = (SitePageURLValue)object;

		return Objects.equals(toString(), sitePageURLValue.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SitePageURLValueSerDes.toJSON(this);
	}

}
// LIFERAY-REST-BUILDER-HASH:1343855113