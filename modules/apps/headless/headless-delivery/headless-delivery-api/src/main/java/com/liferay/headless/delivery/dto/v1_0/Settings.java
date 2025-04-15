/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Represents the settings of a page.", value = "Settings"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Settings")
public class Settings implements Serializable {

	public static Settings toDTO(String json) {
		return ObjectMapperUtil.readValue(Settings.class, json);
	}

	public static Settings unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Settings.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page's color scheme name."
	)
	public String getColorSchemeName() {
		if (_colorSchemeNameSupplier != null) {
			colorSchemeName = _colorSchemeNameSupplier.get();

			_colorSchemeNameSupplier = null;
		}

		return colorSchemeName;
	}

	public void setColorSchemeName(String colorSchemeName) {
		this.colorSchemeName = colorSchemeName;

		_colorSchemeNameSupplier = null;
	}

	@JsonIgnore
	public void setColorSchemeName(
		UnsafeSupplier<String, Exception> colorSchemeNameUnsafeSupplier) {

		_colorSchemeNameSupplier = () -> {
			try {
				return colorSchemeNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The page's color scheme name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String colorSchemeName;

	@JsonIgnore
	private Supplier<String> _colorSchemeNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(description = "The page's CSS.")
	public String getCss() {
		if (_cssSupplier != null) {
			css = _cssSupplier.get();

			_cssSupplier = null;
		}

		return css;
	}

	public void setCss(String css) {
		this.css = css;

		_cssSupplier = null;
	}

	@JsonIgnore
	public void setCss(UnsafeSupplier<String, Exception> cssUnsafeSupplier) {
		_cssSupplier = () -> {
			try {
				return cssUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The page's CSS.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String css;

	@JsonIgnore
	private Supplier<String> _cssSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The FavIcon of the page"
	)
	@Valid
	public Object getFavIcon() {
		if (_favIconSupplier != null) {
			favIcon = _favIconSupplier.get();

			_favIconSupplier = null;
		}

		return favIcon;
	}

	public void setFavIcon(Object favIcon) {
		this.favIcon = favIcon;

		_favIconSupplier = null;
	}

	@JsonIgnore
	public void setFavIcon(
		UnsafeSupplier<Object, Exception> favIconUnsafeSupplier) {

		_favIconSupplier = () -> {
			try {
				return favIconUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The FavIcon of the page")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Object favIcon;

	@JsonIgnore
	private Supplier<Object> _favIconSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The client extensions for global css associated to the page."
	)
	@Valid
	public ClientExtension[] getGlobalCSSClientExtensions() {
		if (_globalCSSClientExtensionsSupplier != null) {
			globalCSSClientExtensions =
				_globalCSSClientExtensionsSupplier.get();

			_globalCSSClientExtensionsSupplier = null;
		}

		return globalCSSClientExtensions;
	}

	public void setGlobalCSSClientExtensions(
		ClientExtension[] globalCSSClientExtensions) {

		this.globalCSSClientExtensions = globalCSSClientExtensions;

		_globalCSSClientExtensionsSupplier = null;
	}

	@JsonIgnore
	public void setGlobalCSSClientExtensions(
		UnsafeSupplier<ClientExtension[], Exception>
			globalCSSClientExtensionsUnsafeSupplier) {

		_globalCSSClientExtensionsSupplier = () -> {
			try {
				return globalCSSClientExtensionsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The client extensions for global css associated to the page."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ClientExtension[] globalCSSClientExtensions;

	@JsonIgnore
	private Supplier<ClientExtension[]> _globalCSSClientExtensionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The client extensions for global js associated to the page."
	)
	@Valid
	public ClientExtension[] getGlobalJSClientExtensions() {
		if (_globalJSClientExtensionsSupplier != null) {
			globalJSClientExtensions = _globalJSClientExtensionsSupplier.get();

			_globalJSClientExtensionsSupplier = null;
		}

		return globalJSClientExtensions;
	}

	public void setGlobalJSClientExtensions(
		ClientExtension[] globalJSClientExtensions) {

		this.globalJSClientExtensions = globalJSClientExtensions;

		_globalJSClientExtensionsSupplier = null;
	}

	@JsonIgnore
	public void setGlobalJSClientExtensions(
		UnsafeSupplier<ClientExtension[], Exception>
			globalJSClientExtensionsUnsafeSupplier) {

		_globalJSClientExtensionsSupplier = () -> {
			try {
				return globalJSClientExtensionsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The client extensions for global js associated to the page."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ClientExtension[] globalJSClientExtensions;

	@JsonIgnore
	private Supplier<ClientExtension[]> _globalJSClientExtensionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page's JavaScript."
	)
	public String getJavascript() {
		if (_javascriptSupplier != null) {
			javascript = _javascriptSupplier.get();

			_javascriptSupplier = null;
		}

		return javascript;
	}

	public void setJavascript(String javascript) {
		this.javascript = javascript;

		_javascriptSupplier = null;
	}

	@JsonIgnore
	public void setJavascript(
		UnsafeSupplier<String, Exception> javascriptUnsafeSupplier) {

		_javascriptSupplier = () -> {
			try {
				return javascriptUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The page's JavaScript.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String javascript;

	@JsonIgnore
	private Supplier<String> _javascriptSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page's master page."
	)
	@Valid
	public MasterPage getMasterPage() {
		if (_masterPageSupplier != null) {
			masterPage = _masterPageSupplier.get();

			_masterPageSupplier = null;
		}

		return masterPage;
	}

	public void setMasterPage(MasterPage masterPage) {
		this.masterPage = masterPage;

		_masterPageSupplier = null;
	}

	@JsonIgnore
	public void setMasterPage(
		UnsafeSupplier<MasterPage, Exception> masterPageUnsafeSupplier) {

		_masterPageSupplier = () -> {
			try {
				return masterPageUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The page's master page.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected MasterPage masterPage;

	@JsonIgnore
	private Supplier<MasterPage> _masterPageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The StyleBook that is applied to the page."
	)
	@Valid
	public StyleBook getStyleBook() {
		if (_styleBookSupplier != null) {
			styleBook = _styleBookSupplier.get();

			_styleBookSupplier = null;
		}

		return styleBook;
	}

	public void setStyleBook(StyleBook styleBook) {
		this.styleBook = styleBook;

		_styleBookSupplier = null;
	}

	@JsonIgnore
	public void setStyleBook(
		UnsafeSupplier<StyleBook, Exception> styleBookUnsafeSupplier) {

		_styleBookSupplier = () -> {
			try {
				return styleBookUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The StyleBook that is applied to the page.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected StyleBook styleBook;

	@JsonIgnore
	private Supplier<StyleBook> _styleBookSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The Client Extension for the theme css of a page"
	)
	@Valid
	public ClientExtension getThemeCSSClientExtension() {
		if (_themeCSSClientExtensionSupplier != null) {
			themeCSSClientExtension = _themeCSSClientExtensionSupplier.get();

			_themeCSSClientExtensionSupplier = null;
		}

		return themeCSSClientExtension;
	}

	public void setThemeCSSClientExtension(
		ClientExtension themeCSSClientExtension) {

		this.themeCSSClientExtension = themeCSSClientExtension;

		_themeCSSClientExtensionSupplier = null;
	}

	@JsonIgnore
	public void setThemeCSSClientExtension(
		UnsafeSupplier<ClientExtension, Exception>
			themeCSSClientExtensionUnsafeSupplier) {

		_themeCSSClientExtensionSupplier = () -> {
			try {
				return themeCSSClientExtensionUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The Client Extension for the theme css of a page"
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ClientExtension themeCSSClientExtension;

	@JsonIgnore
	private Supplier<ClientExtension> _themeCSSClientExtensionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page's theme name."
	)
	public String getThemeName() {
		if (_themeNameSupplier != null) {
			themeName = _themeNameSupplier.get();

			_themeNameSupplier = null;
		}

		return themeName;
	}

	public void setThemeName(String themeName) {
		this.themeName = themeName;

		_themeNameSupplier = null;
	}

	@JsonIgnore
	public void setThemeName(
		UnsafeSupplier<String, Exception> themeNameUnsafeSupplier) {

		_themeNameSupplier = () -> {
			try {
				return themeNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The page's theme name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String themeName;

	@JsonIgnore
	private Supplier<String> _themeNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page's theme settings."
	)
	@Valid
	public Object getThemeSettings() {
		if (_themeSettingsSupplier != null) {
			themeSettings = _themeSettingsSupplier.get();

			_themeSettingsSupplier = null;
		}

		return themeSettings;
	}

	public void setThemeSettings(Object themeSettings) {
		this.themeSettings = themeSettings;

		_themeSettingsSupplier = null;
	}

	@JsonIgnore
	public void setThemeSettings(
		UnsafeSupplier<Object, Exception> themeSettingsUnsafeSupplier) {

		_themeSettingsSupplier = () -> {
			try {
				return themeSettingsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The page's theme settings.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Object themeSettings;

	@JsonIgnore
	private Supplier<Object> _themeSettingsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The Client Extension for the theme spritemap of a page"
	)
	@Valid
	public ClientExtension getThemeSpritemapClientExtension() {
		if (_themeSpritemapClientExtensionSupplier != null) {
			themeSpritemapClientExtension =
				_themeSpritemapClientExtensionSupplier.get();

			_themeSpritemapClientExtensionSupplier = null;
		}

		return themeSpritemapClientExtension;
	}

	public void setThemeSpritemapClientExtension(
		ClientExtension themeSpritemapClientExtension) {

		this.themeSpritemapClientExtension = themeSpritemapClientExtension;

		_themeSpritemapClientExtensionSupplier = null;
	}

	@JsonIgnore
	public void setThemeSpritemapClientExtension(
		UnsafeSupplier<ClientExtension, Exception>
			themeSpritemapClientExtensionUnsafeSupplier) {

		_themeSpritemapClientExtensionSupplier = () -> {
			try {
				return themeSpritemapClientExtensionUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The Client Extension for the theme spritemap of a page"
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ClientExtension themeSpritemapClientExtension;

	@JsonIgnore
	private Supplier<ClientExtension> _themeSpritemapClientExtensionSupplier;

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
		StringBundler sb = new StringBundler();

		sb.append("{");

		String colorSchemeName = getColorSchemeName();

		if (colorSchemeName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"colorSchemeName\": ");

			sb.append("\"");

			sb.append(_escape(colorSchemeName));

			sb.append("\"");
		}

		String css = getCss();

		if (css != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"css\": ");

			sb.append("\"");

			sb.append(_escape(css));

			sb.append("\"");
		}

		Object favIcon = getFavIcon();

		if (favIcon != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"favIcon\": ");

			if (favIcon instanceof Map) {
				sb.append(JSONFactoryUtil.createJSONObject((Map<?, ?>)favIcon));
			}
			else if (favIcon instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)favIcon));
				sb.append("\"");
			}
			else {
				sb.append(favIcon);
			}
		}

		ClientExtension[] globalCSSClientExtensions =
			getGlobalCSSClientExtensions();

		if (globalCSSClientExtensions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"globalCSSClientExtensions\": ");

			sb.append("[");

			for (int i = 0; i < globalCSSClientExtensions.length; i++) {
				sb.append(String.valueOf(globalCSSClientExtensions[i]));

				if ((i + 1) < globalCSSClientExtensions.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		ClientExtension[] globalJSClientExtensions =
			getGlobalJSClientExtensions();

		if (globalJSClientExtensions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"globalJSClientExtensions\": ");

			sb.append("[");

			for (int i = 0; i < globalJSClientExtensions.length; i++) {
				sb.append(String.valueOf(globalJSClientExtensions[i]));

				if ((i + 1) < globalJSClientExtensions.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String javascript = getJavascript();

		if (javascript != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"javascript\": ");

			sb.append("\"");

			sb.append(_escape(javascript));

			sb.append("\"");
		}

		MasterPage masterPage = getMasterPage();

		if (masterPage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"masterPage\": ");

			sb.append(String.valueOf(masterPage));
		}

		StyleBook styleBook = getStyleBook();

		if (styleBook != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"styleBook\": ");

			sb.append(String.valueOf(styleBook));
		}

		ClientExtension themeCSSClientExtension = getThemeCSSClientExtension();

		if (themeCSSClientExtension != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"themeCSSClientExtension\": ");

			sb.append(String.valueOf(themeCSSClientExtension));
		}

		String themeName = getThemeName();

		if (themeName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"themeName\": ");

			sb.append("\"");

			sb.append(_escape(themeName));

			sb.append("\"");
		}

		Object themeSettings = getThemeSettings();

		if (themeSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"themeSettings\": ");

			if (themeSettings instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject((Map<?, ?>)themeSettings));
			}
			else if (themeSettings instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)themeSettings));
				sb.append("\"");
			}
			else {
				sb.append(themeSettings);
			}
		}

		ClientExtension themeSpritemapClientExtension =
			getThemeSpritemapClientExtension();

		if (themeSpritemapClientExtension != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"themeSpritemapClientExtension\": ");

			sb.append(String.valueOf(themeSpritemapClientExtension));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.Settings",
		name = "x-class-name"
	)
	public String xClassName;

	private static String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[])value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof Map) {
						sb.append(_toJSON((Map<String, ?>)valueArray[i]));
					}
					else if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>)value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}