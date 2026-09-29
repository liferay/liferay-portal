/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.dto.v1_0;

import com.liferay.headless.admin.fragment.client.function.UnsafeSupplier;
import com.liferay.headless.admin.fragment.client.serdes.v1_0.NavigationMenuValueSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public abstract class NavigationMenuValue implements Cloneable, Serializable {

	public static NavigationMenuValue toDTO(String json) {
		return NavigationMenuValueSerDes.toDTO(json);
	}

	public NavigationMenuType getNavigationMenuType() {
		return navigationMenuType;
	}

	public String getNavigationMenuTypeAsString() {
		if (navigationMenuType == null) {
			return null;
		}

		return navigationMenuType.toString();
	}

	public void setNavigationMenuType(NavigationMenuType navigationMenuType) {
		this.navigationMenuType = navigationMenuType;
	}

	public void setNavigationMenuType(
		UnsafeSupplier<NavigationMenuType, Exception>
			navigationMenuTypeUnsafeSupplier) {

		try {
			navigationMenuType = navigationMenuTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected NavigationMenuType navigationMenuType;

	@Override
	public NavigationMenuValue clone() throws CloneNotSupportedException {
		return (NavigationMenuValue)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof NavigationMenuValue)) {
			return false;
		}

		NavigationMenuValue navigationMenuValue = (NavigationMenuValue)object;

		return Objects.equals(toString(), navigationMenuValue.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return NavigationMenuValueSerDes.toJSON(this);
	}

	public static enum NavigationMenuType {

		CONTEXTUAL_MENU("ContextualMenu"), SITE_MENU("SiteMenu"),
		SITE_PAGES("SitePages");

		public static NavigationMenuType create(String value) {
			for (NavigationMenuType navigationMenuType : values()) {
				if (Objects.equals(navigationMenuType.getValue(), value) ||
					Objects.equals(navigationMenuType.name(), value)) {

					return navigationMenuType;
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

		private NavigationMenuType(String value) {
			_value = value;
		}

		private final String _value;

	}

}
// LIFERAY-REST-BUILDER-HASH:-824751795