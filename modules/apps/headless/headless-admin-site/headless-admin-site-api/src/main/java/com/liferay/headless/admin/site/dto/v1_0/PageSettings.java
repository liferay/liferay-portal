/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
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
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
@GraphQLName("PageSettings")
@JsonFilter("Liferay.Vulcan")
@JsonSubTypes(
	{
		@JsonSubTypes.Type(
			name = "ContentPageSettings", value = ContentPageSettings.class
		),
		@JsonSubTypes.Type(
			name = "WidgetPageSettings", value = WidgetPageSettings.class
		)
	}
)
@JsonTypeInfo(
	include = JsonTypeInfo.As.PROPERTY, property = "type",
	use = JsonTypeInfo.Id.NAME, visible = true
)
@XmlRootElement(name = "PageSettings")
public abstract class PageSettings implements Serializable {

	public static PageSettings toDTO(String json) {
		return ObjectMapperUtil.readValue(PageSettings.class, json);
	}

	public static PageSettings unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(PageSettings.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of custom meta tags this page has."
	)
	@Valid
	public CustomMetaTag[] getCustomMetaTags() {
		if (_customMetaTagsSupplier != null) {
			customMetaTags = _customMetaTagsSupplier.get();

			_customMetaTagsSupplier = null;
		}

		return customMetaTags;
	}

	public void setCustomMetaTags(CustomMetaTag[] customMetaTags) {
		this.customMetaTags = customMetaTags;

		_customMetaTagsSupplier = null;
	}

	@JsonIgnore
	public void setCustomMetaTags(
		UnsafeSupplier<CustomMetaTag[], Exception>
			customMetaTagsUnsafeSupplier) {

		_customMetaTagsSupplier = () -> {
			try {
				return customMetaTagsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A list of custom meta tags this page has.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected CustomMetaTag[] customMetaTags;

	@JsonIgnore
	private Supplier<CustomMetaTag[]> _customMetaTagsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether the page is hidden from navigation."
	)
	public Boolean getHiddenFromNavigation() {
		if (_hiddenFromNavigationSupplier != null) {
			hiddenFromNavigation = _hiddenFromNavigationSupplier.get();

			_hiddenFromNavigationSupplier = null;
		}

		return hiddenFromNavigation;
	}

	public void setHiddenFromNavigation(Boolean hiddenFromNavigation) {
		this.hiddenFromNavigation = hiddenFromNavigation;

		_hiddenFromNavigationSupplier = null;
	}

	@JsonIgnore
	public void setHiddenFromNavigation(
		UnsafeSupplier<Boolean, Exception> hiddenFromNavigationUnsafeSupplier) {

		_hiddenFromNavigationSupplier = () -> {
			try {
				return hiddenFromNavigationUnsafeSupplier.get();
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
		description = "A flag that indicates whether the page is hidden from navigation."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean hiddenFromNavigation;

	@JsonIgnore
	private Supplier<Boolean> _hiddenFromNavigationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page's site navigation menu settings."
	)
	@Valid
	public NavigationMenuSettings getNavigationMenuSettings() {
		if (_navigationMenuSettingsSupplier != null) {
			navigationMenuSettings = _navigationMenuSettingsSupplier.get();

			_navigationMenuSettingsSupplier = null;
		}

		return navigationMenuSettings;
	}

	public void setNavigationMenuSettings(
		NavigationMenuSettings navigationMenuSettings) {

		this.navigationMenuSettings = navigationMenuSettings;

		_navigationMenuSettingsSupplier = null;
	}

	@JsonIgnore
	public void setNavigationMenuSettings(
		UnsafeSupplier<NavigationMenuSettings, Exception>
			navigationMenuSettingsUnsafeSupplier) {

		_navigationMenuSettingsSupplier = () -> {
			try {
				return navigationMenuSettingsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The page's site navigation menu settings.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected NavigationMenuSettings navigationMenuSettings;

	@JsonIgnore
	private Supplier<NavigationMenuSettings> _navigationMenuSettingsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page's Open Graph settings."
	)
	@Valid
	public OpenGraphSettings getOpenGraphSettings() {
		if (_openGraphSettingsSupplier != null) {
			openGraphSettings = _openGraphSettingsSupplier.get();

			_openGraphSettingsSupplier = null;
		}

		return openGraphSettings;
	}

	public void setOpenGraphSettings(OpenGraphSettings openGraphSettings) {
		this.openGraphSettings = openGraphSettings;

		_openGraphSettingsSupplier = null;
	}

	@JsonIgnore
	public void setOpenGraphSettings(
		UnsafeSupplier<OpenGraphSettings, Exception>
			openGraphSettingsUnsafeSupplier) {

		_openGraphSettingsSupplier = () -> {
			try {
				return openGraphSettingsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The page's Open Graph settings.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected OpenGraphSettings openGraphSettings;

	@JsonIgnore
	private Supplier<OpenGraphSettings> _openGraphSettingsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page's SEO settings."
	)
	@Valid
	public SEOSettings getSeoSettings() {
		if (_seoSettingsSupplier != null) {
			seoSettings = _seoSettingsSupplier.get();

			_seoSettingsSupplier = null;
		}

		return seoSettings;
	}

	public void setSeoSettings(SEOSettings seoSettings) {
		this.seoSettings = seoSettings;

		_seoSettingsSupplier = null;
	}

	@JsonIgnore
	public void setSeoSettings(
		UnsafeSupplier<SEOSettings, Exception> seoSettingsUnsafeSupplier) {

		_seoSettingsSupplier = () -> {
			try {
				return seoSettingsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The page's SEO settings.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected SEOSettings seoSettings;

	@JsonIgnore
	private Supplier<SEOSettings> _seoSettingsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("type")
	@Valid
	public Type getType() {
		if (_typeSupplier != null) {
			type = _typeSupplier.get();

			_typeSupplier = null;
		}

		return type;
	}

	@JsonIgnore
	public String getTypeAsString() {
		Type type = getType();

		if (type == null) {
			return null;
		}

		return type.toString();
	}

	public void setType(Type type) {
		this.type = type;

		_typeSupplier = null;
	}

	@JsonIgnore
	public void setType(UnsafeSupplier<Type, Exception> typeUnsafeSupplier) {
		_typeSupplier = () -> {
			try {
				return typeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Type type;

	@JsonIgnore
	private Supplier<Type> _typeSupplier;

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
		StringBundler sb = new StringBundler();

		sb.append("{");

		CustomMetaTag[] customMetaTags = getCustomMetaTags();

		if (customMetaTags != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customMetaTags\": ");

			sb.append("[");

			for (int i = 0; i < customMetaTags.length; i++) {
				sb.append(String.valueOf(customMetaTags[i]));

				if ((i + 1) < customMetaTags.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Boolean hiddenFromNavigation = getHiddenFromNavigation();

		if (hiddenFromNavigation != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hiddenFromNavigation\": ");

			sb.append(hiddenFromNavigation);
		}

		NavigationMenuSettings navigationMenuSettings =
			getNavigationMenuSettings();

		if (navigationMenuSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"navigationMenuSettings\": ");

			sb.append(String.valueOf(navigationMenuSettings));
		}

		OpenGraphSettings openGraphSettings = getOpenGraphSettings();

		if (openGraphSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"openGraphSettings\": ");

			sb.append(String.valueOf(openGraphSettings));
		}

		SEOSettings seoSettings = getSeoSettings();

		if (seoSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"seoSettings\": ");

			sb.append(String.valueOf(seoSettings));
		}

		Type type = getType();

		if (type != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(type);

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.PageSettings",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("Type")
	public static enum Type {

		CONTENT_PAGE_SETTINGS("ContentPageSettings"),
		WIDGET_PAGE_SETTINGS("WidgetPageSettings");

		@JsonCreator
		public static Type create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Type type : values()) {
				if (Objects.equals(type.getValue(), value)) {
					return type;
				}
			}

			throw new IllegalArgumentException("Invalid enum value: " + value);
		}

		@JsonValue
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