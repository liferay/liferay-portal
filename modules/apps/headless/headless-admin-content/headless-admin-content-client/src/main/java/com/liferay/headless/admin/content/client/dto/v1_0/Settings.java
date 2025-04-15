/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.content.client.dto.v1_0;

import com.liferay.headless.admin.content.client.function.UnsafeSupplier;
import com.liferay.headless.admin.content.client.serdes.v1_0.SettingsSerDes;

import java.io.Serializable;

import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
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

	public Object getFavIcon() {
		return favIcon;
	}

	public void setFavIcon(Object favIcon) {
		this.favIcon = favIcon;
	}

	public void setFavIcon(
		UnsafeSupplier<Object, Exception> favIconUnsafeSupplier) {

		try {
			favIcon = favIconUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object favIcon;

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

	public MasterPage getMasterPage() {
		return masterPage;
	}

	public void setMasterPage(MasterPage masterPage) {
		this.masterPage = masterPage;
	}

	public void setMasterPage(
		UnsafeSupplier<MasterPage, Exception> masterPageUnsafeSupplier) {

		try {
			masterPage = masterPageUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected MasterPage masterPage;

	public StyleBook getStyleBook() {
		return styleBook;
	}

	public void setStyleBook(StyleBook styleBook) {
		this.styleBook = styleBook;
	}

	public void setStyleBook(
		UnsafeSupplier<StyleBook, Exception> styleBookUnsafeSupplier) {

		try {
			styleBook = styleBookUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected StyleBook styleBook;

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

	public Object getThemeSettings() {
		return themeSettings;
	}

	public void setThemeSettings(Object themeSettings) {
		this.themeSettings = themeSettings;
	}

	public void setThemeSettings(
		UnsafeSupplier<Object, Exception> themeSettingsUnsafeSupplier) {

		try {
			themeSettings = themeSettingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object themeSettings;

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