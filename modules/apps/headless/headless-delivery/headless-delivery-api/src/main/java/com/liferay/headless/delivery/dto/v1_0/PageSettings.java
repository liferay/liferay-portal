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
	description = "Represents the settings of a Page.", value = "PageSettings"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PageSettings")
public class PageSettings implements Serializable {

	public static PageSettings toDTO(String json) {
		return ObjectMapperUtil.readValue(PageSettings.class, json);
	}

	public static PageSettings unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(PageSettings.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of custom metatags this page has."
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

	@GraphQLField(description = "A list of custom metatags this page has.")
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

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page's site navigation menu settings."
	)
	@Valid
	public SitePageNavigationMenuSettings getSitePageNavigationMenuSettings() {
		if (_sitePageNavigationMenuSettingsSupplier != null) {
			sitePageNavigationMenuSettings =
				_sitePageNavigationMenuSettingsSupplier.get();

			_sitePageNavigationMenuSettingsSupplier = null;
		}

		return sitePageNavigationMenuSettings;
	}

	public void setSitePageNavigationMenuSettings(
		SitePageNavigationMenuSettings sitePageNavigationMenuSettings) {

		this.sitePageNavigationMenuSettings = sitePageNavigationMenuSettings;

		_sitePageNavigationMenuSettingsSupplier = null;
	}

	@JsonIgnore
	public void setSitePageNavigationMenuSettings(
		UnsafeSupplier<SitePageNavigationMenuSettings, Exception>
			sitePageNavigationMenuSettingsUnsafeSupplier) {

		_sitePageNavigationMenuSettingsSupplier = () -> {
			try {
				return sitePageNavigationMenuSettingsUnsafeSupplier.get();
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
	protected SitePageNavigationMenuSettings sitePageNavigationMenuSettings;

	@JsonIgnore
	private Supplier<SitePageNavigationMenuSettings>
		_sitePageNavigationMenuSettingsSupplier;

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

		SitePageNavigationMenuSettings sitePageNavigationMenuSettings =
			getSitePageNavigationMenuSettings();

		if (sitePageNavigationMenuSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sitePageNavigationMenuSettings\": ");

			sb.append(String.valueOf(sitePageNavigationMenuSettings));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.PageSettings",
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