/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
@GraphQLName(description = "Settings related with SEO.", value = "SEOSettings")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "SEOSettings")
public class SEOSettings implements Serializable {

	public static SEOSettings toDTO(String json) {
		return ObjectMapperUtil.readValue(SEOSettings.class, json);
	}

	public static SEOSettings unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(SEOSettings.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The localized canonical URL of the page, if it exists."
	)
	@Valid
	public Map<String, String> getCustomCanonicalURL_i18n() {
		if (_customCanonicalURL_i18nSupplier != null) {
			customCanonicalURL_i18n = _customCanonicalURL_i18nSupplier.get();

			_customCanonicalURL_i18nSupplier = null;
		}

		return customCanonicalURL_i18n;
	}

	public void setCustomCanonicalURL_i18n(
		Map<String, String> customCanonicalURL_i18n) {

		this.customCanonicalURL_i18n = customCanonicalURL_i18n;

		_customCanonicalURL_i18nSupplier = null;
	}

	@JsonIgnore
	public void setCustomCanonicalURL_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			customCanonicalURL_i18nUnsafeSupplier) {

		_customCanonicalURL_i18nSupplier = () -> {
			try {
				return customCanonicalURL_i18nUnsafeSupplier.get();
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
		description = "The localized canonical URL of the page, if it exists."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> customCanonicalURL_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _customCanonicalURL_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The localized descriptions of the page to be used as summary for search engines."
	)
	@Valid
	public Map<String, String> getDescription_i18n() {
		if (_description_i18nSupplier != null) {
			description_i18n = _description_i18nSupplier.get();

			_description_i18nSupplier = null;
		}

		return description_i18n;
	}

	public void setDescription_i18n(Map<String, String> description_i18n) {
		this.description_i18n = description_i18n;

		_description_i18nSupplier = null;
	}

	@JsonIgnore
	public void setDescription_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			description_i18nUnsafeSupplier) {

		_description_i18nSupplier = () -> {
			try {
				return description_i18nUnsafeSupplier.get();
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
		description = "The localized descriptions of the page to be used as summary for search engines."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> description_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _description_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The localized main titles of the page to be used by search engines."
	)
	@Valid
	public Map<String, String> getHtmlTitle_i18n() {
		if (_htmlTitle_i18nSupplier != null) {
			htmlTitle_i18n = _htmlTitle_i18nSupplier.get();

			_htmlTitle_i18nSupplier = null;
		}

		return htmlTitle_i18n;
	}

	public void setHtmlTitle_i18n(Map<String, String> htmlTitle_i18n) {
		this.htmlTitle_i18n = htmlTitle_i18n;

		_htmlTitle_i18nSupplier = null;
	}

	@JsonIgnore
	public void setHtmlTitle_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			htmlTitle_i18nUnsafeSupplier) {

		_htmlTitle_i18nSupplier = () -> {
			try {
				return htmlTitle_i18nUnsafeSupplier.get();
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
		description = "The localized main titles of the page to be used by search engines."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> htmlTitle_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _htmlTitle_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A localized tag telling search engines if and how they should crawl the page."
	)
	@Valid
	public Map<String, String> getRobots_i18n() {
		if (_robots_i18nSupplier != null) {
			robots_i18n = _robots_i18nSupplier.get();

			_robots_i18nSupplier = null;
		}

		return robots_i18n;
	}

	public void setRobots_i18n(Map<String, String> robots_i18n) {
		this.robots_i18n = robots_i18n;

		_robots_i18nSupplier = null;
	}

	@JsonIgnore
	public void setRobots_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			robots_i18nUnsafeSupplier) {

		_robots_i18nSupplier = () -> {
			try {
				return robots_i18nUnsafeSupplier.get();
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
		description = "A localized tag telling search engines if and how they should crawl the page."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> robots_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _robots_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of localized target keywords of the page to be used by search engines."
	)
	@Valid
	public Map<String, String> getSeoKeywords_i18n() {
		if (_seoKeywords_i18nSupplier != null) {
			seoKeywords_i18n = _seoKeywords_i18nSupplier.get();

			_seoKeywords_i18nSupplier = null;
		}

		return seoKeywords_i18n;
	}

	public void setSeoKeywords_i18n(Map<String, String> seoKeywords_i18n) {
		this.seoKeywords_i18n = seoKeywords_i18n;

		_seoKeywords_i18nSupplier = null;
	}

	@JsonIgnore
	public void setSeoKeywords_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			seoKeywords_i18nUnsafeSupplier) {

		_seoKeywords_i18nSupplier = () -> {
			try {
				return seoKeywords_i18nUnsafeSupplier.get();
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
		description = "A list of localized target keywords of the page to be used by search engines."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> seoKeywords_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _seoKeywords_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Represents settings related with the site map."
	)
	@Valid
	public SiteMapSettings getSiteMapSettings() {
		if (_siteMapSettingsSupplier != null) {
			siteMapSettings = _siteMapSettingsSupplier.get();

			_siteMapSettingsSupplier = null;
		}

		return siteMapSettings;
	}

	public void setSiteMapSettings(SiteMapSettings siteMapSettings) {
		this.siteMapSettings = siteMapSettings;

		_siteMapSettingsSupplier = null;
	}

	@JsonIgnore
	public void setSiteMapSettings(
		UnsafeSupplier<SiteMapSettings, Exception>
			siteMapSettingsUnsafeSupplier) {

		_siteMapSettingsSupplier = () -> {
			try {
				return siteMapSettingsUnsafeSupplier.get();
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
		description = "Represents settings related with the site map."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected SiteMapSettings siteMapSettings;

	@JsonIgnore
	private Supplier<SiteMapSettings> _siteMapSettingsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SEOSettings)) {
			return false;
		}

		SEOSettings seoSettings = (SEOSettings)object;

		return Objects.equals(toString(), seoSettings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Map<String, String> customCanonicalURL_i18n =
			getCustomCanonicalURL_i18n();

		if (customCanonicalURL_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCanonicalURL_i18n\": ");

			sb.append(_toJSON(customCanonicalURL_i18n));
		}

		Map<String, String> description_i18n = getDescription_i18n();

		if (description_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description_i18n\": ");

			sb.append(_toJSON(description_i18n));
		}

		Map<String, String> htmlTitle_i18n = getHtmlTitle_i18n();

		if (htmlTitle_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"htmlTitle_i18n\": ");

			sb.append(_toJSON(htmlTitle_i18n));
		}

		Map<String, String> robots_i18n = getRobots_i18n();

		if (robots_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"robots_i18n\": ");

			sb.append(_toJSON(robots_i18n));
		}

		Map<String, String> seoKeywords_i18n = getSeoKeywords_i18n();

		if (seoKeywords_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"seoKeywords_i18n\": ");

			sb.append(_toJSON(seoKeywords_i18n));
		}

		SiteMapSettings siteMapSettings = getSiteMapSettings();

		if (siteMapSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteMapSettings\": ");

			sb.append(String.valueOf(siteMapSettings));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.SEOSettings",
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