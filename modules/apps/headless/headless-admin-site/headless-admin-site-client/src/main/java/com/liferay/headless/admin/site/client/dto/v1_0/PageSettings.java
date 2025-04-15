/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.PageSettingsSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public abstract class PageSettings implements Cloneable, Serializable {

	public static PageSettings toDTO(String json) {
		return PageSettingsSerDes.toDTO(json);
	}

	public CustomMetaTag[] getCustomMetaTags() {
		return customMetaTags;
	}

	public void setCustomMetaTags(CustomMetaTag[] customMetaTags) {
		this.customMetaTags = customMetaTags;
	}

	public void setCustomMetaTags(
		UnsafeSupplier<CustomMetaTag[], Exception>
			customMetaTagsUnsafeSupplier) {

		try {
			customMetaTags = customMetaTagsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected CustomMetaTag[] customMetaTags;

	public Boolean getHiddenFromNavigation() {
		return hiddenFromNavigation;
	}

	public void setHiddenFromNavigation(Boolean hiddenFromNavigation) {
		this.hiddenFromNavigation = hiddenFromNavigation;
	}

	public void setHiddenFromNavigation(
		UnsafeSupplier<Boolean, Exception> hiddenFromNavigationUnsafeSupplier) {

		try {
			hiddenFromNavigation = hiddenFromNavigationUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean hiddenFromNavigation;

	public NavigationMenuSettings getNavigationMenuSettings() {
		return navigationMenuSettings;
	}

	public void setNavigationMenuSettings(
		NavigationMenuSettings navigationMenuSettings) {

		this.navigationMenuSettings = navigationMenuSettings;
	}

	public void setNavigationMenuSettings(
		UnsafeSupplier<NavigationMenuSettings, Exception>
			navigationMenuSettingsUnsafeSupplier) {

		try {
			navigationMenuSettings = navigationMenuSettingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected NavigationMenuSettings navigationMenuSettings;

	public OpenGraphSettings getOpenGraphSettings() {
		return openGraphSettings;
	}

	public void setOpenGraphSettings(OpenGraphSettings openGraphSettings) {
		this.openGraphSettings = openGraphSettings;
	}

	public void setOpenGraphSettings(
		UnsafeSupplier<OpenGraphSettings, Exception>
			openGraphSettingsUnsafeSupplier) {

		try {
			openGraphSettings = openGraphSettingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected OpenGraphSettings openGraphSettings;

	public SEOSettings getSeoSettings() {
		return seoSettings;
	}

	public void setSeoSettings(SEOSettings seoSettings) {
		this.seoSettings = seoSettings;
	}

	public void setSeoSettings(
		UnsafeSupplier<SEOSettings, Exception> seoSettingsUnsafeSupplier) {

		try {
			seoSettings = seoSettingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected SEOSettings seoSettings;

	public Type getType() {
		return type;
	}

	public String getTypeAsString() {
		if (type == null) {
			return null;
		}

		return type.toString();
	}

	public void setType(Type type) {
		this.type = type;
	}

	public void setType(UnsafeSupplier<Type, Exception> typeUnsafeSupplier) {
		try {
			type = typeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Type type;

	@Override
	public PageSettings clone() throws CloneNotSupportedException {
		return (PageSettings)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PageSettings)) {
			return false;
		}

		PageSettings pageSettings = (PageSettings)object;

		return Objects.equals(toString(), pageSettings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return PageSettingsSerDes.toJSON(this);
	}

	public static enum Type {

		CONTENT_PAGE_SETTINGS("ContentPageSettings"),
		WIDGET_PAGE_SETTINGS("WidgetPageSettings");

		public static Type create(String value) {
			for (Type type : values()) {
				if (Objects.equals(type.getValue(), value) ||
					Objects.equals(type.name(), value)) {

					return type;
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

		private Type(String value) {
			_value = value;
		}

		private final String _value;

	}

}