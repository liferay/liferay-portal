/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.SitePagesNavigationMenuValueSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class SitePagesNavigationMenuValue
	extends NavigationMenuValue implements Cloneable, Serializable {

	public static SitePagesNavigationMenuValue toDTO(String json) {
		return SitePagesNavigationMenuValueSerDes.toDTO(json);
	}

	public PageSetType getPageSetType() {
		return pageSetType;
	}

	public String getPageSetTypeAsString() {
		if (pageSetType == null) {
			return null;
		}

		return pageSetType.toString();
	}

	public void setPageSetType(PageSetType pageSetType) {
		this.pageSetType = pageSetType;
	}

	public void setPageSetType(
		UnsafeSupplier<PageSetType, Exception> pageSetTypeUnsafeSupplier) {

		try {
			pageSetType = pageSetTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected PageSetType pageSetType;

	public String getParentSitePageExternalReferenceCode() {
		return parentSitePageExternalReferenceCode;
	}

	public void setParentSitePageExternalReferenceCode(
		String parentSitePageExternalReferenceCode) {

		this.parentSitePageExternalReferenceCode =
			parentSitePageExternalReferenceCode;
	}

	public void setParentSitePageExternalReferenceCode(
		UnsafeSupplier<String, Exception>
			parentSitePageExternalReferenceCodeUnsafeSupplier) {

		try {
			parentSitePageExternalReferenceCode =
				parentSitePageExternalReferenceCodeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String parentSitePageExternalReferenceCode;

	@Override
	public SitePagesNavigationMenuValue clone()
		throws CloneNotSupportedException {

		return (SitePagesNavigationMenuValue)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SitePagesNavigationMenuValue)) {
			return false;
		}

		SitePagesNavigationMenuValue sitePagesNavigationMenuValue =
			(SitePagesNavigationMenuValue)object;

		return Objects.equals(
			toString(), sitePagesNavigationMenuValue.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SitePagesNavigationMenuValueSerDes.toJSON(this);
	}

	public static enum PageSetType {

		PRIVATE_PAGES("PrivatePages"), PUBLIC_PAGES("PublicPages");

		public static PageSetType create(String value) {
			for (PageSetType pageSetType : values()) {
				if (Objects.equals(pageSetType.getValue(), value) ||
					Objects.equals(pageSetType.name(), value)) {

					return pageSetType;
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

		private PageSetType(String value) {
			_value = value;
		}

		private final String _value;

	}

}
// LIFERAY-REST-BUILDER-HASH:1571674711