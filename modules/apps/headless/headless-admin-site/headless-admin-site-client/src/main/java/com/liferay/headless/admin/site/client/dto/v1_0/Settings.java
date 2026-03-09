/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.dto.v1_0;

import com.liferay.headless.admin.site.client.function.UnsafeSupplier;
import com.liferay.headless.admin.site.client.serdes.v1_0.SettingsSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class Settings implements Cloneable, Serializable {

	public static Settings toDTO(String json) {
		return SettingsSerDes.toDTO(json);
	}

	public String getColorSchemeName() {
		return colorSchemeName;
	}

	public void setColorSchemeName(String colorSchemeName) {
		this.colorSchemeName = colorSchemeName;
	}

	public void setColorSchemeName(
		UnsafeSupplier<String, Exception> colorSchemeNameUnsafeSupplier) {

		try {
			colorSchemeName = colorSchemeNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String colorSchemeName;

	public String getCss() {
		return css;
	}

	public void setCss(String css) {
		this.css = css;
	}

	public void setCss(UnsafeSupplier<String, Exception> cssUnsafeSupplier) {
		try {
			css = cssUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String css;

	public FavIcon getFavIcon() {
		return favIcon;
	}

	public void setFavIcon(FavIcon favIcon) {
		this.favIcon = favIcon;
	}

	public void setFavIcon(
		UnsafeSupplier<FavIcon, Exception> favIconUnsafeSupplier) {

		try {
			favIcon = favIconUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FavIcon favIcon;

	public ClientExtension[] getGlobalCSSClientExtensions() {
		return globalCSSClientExtensions;
	}

	public void setGlobalCSSClientExtensions(
		ClientExtension[] globalCSSClientExtensions) {

		this.globalCSSClientExtensions = globalCSSClientExtensions;
	}

	public void setGlobalCSSClientExtensions(
		UnsafeSupplier<ClientExtension[], Exception>
			globalCSSClientExtensionsUnsafeSupplier) {

		try {
			globalCSSClientExtensions =
				globalCSSClientExtensionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ClientExtension[] globalCSSClientExtensions;

	public ClientExtension[] getGlobalJSClientExtensions() {
		return globalJSClientExtensions;
	}

	public void setGlobalJSClientExtensions(
		ClientExtension[] globalJSClientExtensions) {

		this.globalJSClientExtensions = globalJSClientExtensions;
	}

	public void setGlobalJSClientExtensions(
		UnsafeSupplier<ClientExtension[], Exception>
			globalJSClientExtensionsUnsafeSupplier) {

		try {
			globalJSClientExtensions =
				globalJSClientExtensionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ClientExtension[] globalJSClientExtensions;

	public IconImageURL getIconImageURL() {
		return iconImageURL;
	}

	public void setIconImageURL(IconImageURL iconImageURL) {
		this.iconImageURL = iconImageURL;
	}

	public void setIconImageURL(
		UnsafeSupplier<IconImageURL, Exception> iconImageURLUnsafeSupplier) {

		try {
			iconImageURL = iconImageURLUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected IconImageURL iconImageURL;

	public String getJavascript() {
		return javascript;
	}

	public void setJavascript(String javascript) {
		this.javascript = javascript;
	}

	public void setJavascript(
		UnsafeSupplier<String, Exception> javascriptUnsafeSupplier) {

		try {
			javascript = javascriptUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String javascript;

	public ItemExternalReference getMasterPageItemExternalReference() {
		return masterPageItemExternalReference;
	}

	public void setMasterPageItemExternalReference(
		ItemExternalReference masterPageItemExternalReference) {

		this.masterPageItemExternalReference = masterPageItemExternalReference;
	}

	public void setMasterPageItemExternalReference(
		UnsafeSupplier<ItemExternalReference, Exception>
			masterPageItemExternalReferenceUnsafeSupplier) {

		try {
			masterPageItemExternalReference =
				masterPageItemExternalReferenceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ItemExternalReference masterPageItemExternalReference;

	public ItemExternalReference getStyleBookItemExternalReference() {
		return styleBookItemExternalReference;
	}

	public void setStyleBookItemExternalReference(
		ItemExternalReference styleBookItemExternalReference) {

		this.styleBookItemExternalReference = styleBookItemExternalReference;
	}

	public void setStyleBookItemExternalReference(
		UnsafeSupplier<ItemExternalReference, Exception>
			styleBookItemExternalReferenceUnsafeSupplier) {

		try {
			styleBookItemExternalReference =
				styleBookItemExternalReferenceUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ItemExternalReference styleBookItemExternalReference;

	public ClientExtension getThemeCSSClientExtension() {
		return themeCSSClientExtension;
	}

	public void setThemeCSSClientExtension(
		ClientExtension themeCSSClientExtension) {

		this.themeCSSClientExtension = themeCSSClientExtension;
	}

	public void setThemeCSSClientExtension(
		UnsafeSupplier<ClientExtension, Exception>
			themeCSSClientExtensionUnsafeSupplier) {

		try {
			themeCSSClientExtension =
				themeCSSClientExtensionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ClientExtension themeCSSClientExtension;

	public String getThemeName() {
		return themeName;
	}

	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}

	public void setThemeName(
		UnsafeSupplier<String, Exception> themeNameUnsafeSupplier) {

		try {
			themeName = themeNameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String themeName;

	public Map<String, String> getThemeSettings() {
		return themeSettings;
	}

	public void setThemeSettings(Map<String, String> themeSettings) {
		this.themeSettings = themeSettings;
	}

	public void setThemeSettings(
		UnsafeSupplier<Map<String, String>, Exception>
			themeSettingsUnsafeSupplier) {

		try {
			themeSettings = themeSettingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> themeSettings;

	public ClientExtension getThemeSpritemapClientExtension() {
		return themeSpritemapClientExtension;
	}

	public void setThemeSpritemapClientExtension(
		ClientExtension themeSpritemapClientExtension) {

		this.themeSpritemapClientExtension = themeSpritemapClientExtension;
	}

	public void setThemeSpritemapClientExtension(
		UnsafeSupplier<ClientExtension, Exception>
			themeSpritemapClientExtensionUnsafeSupplier) {

		try {
			themeSpritemapClientExtension =
				themeSpritemapClientExtensionUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ClientExtension themeSpritemapClientExtension;

	@Override
	public Settings clone() throws CloneNotSupportedException {
		return (Settings)super.clone();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Settings)) {
			return false;
		}

		Settings settings = (Settings)object;

		return Objects.equals(toString(), settings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		return SettingsSerDes.toJSON(this);
	}

}