/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.dto.v1_0;

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

import java.util.Arrays;
import java.util.Collection;
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
@GraphQLName(
	description = "Settings related with SEO of a display page template.",
	value = "DisplayPageTemplateSEOSettings"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Settings related with SEO of a display page template."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "DisplayPageTemplateSEOSettings")
public class DisplayPageTemplateSEOSettings implements Serializable {

	public static DisplayPageTemplateSEOSettings toDTO(String json) {
		return ObjectMapperUtil.readValue(
			DisplayPageTemplateSEOSettings.class, json);
	}

	public static DisplayPageTemplateSEOSettings unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			DisplayPageTemplateSEOSettings.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The description template of the display page template to be used as summary for search engines."
	)
	public String getDescriptionTemplate() {
		if (_descriptionTemplateSupplier != null) {
			descriptionTemplate = _descriptionTemplateSupplier.get();

			_descriptionTemplateSupplier = null;
		}

		return descriptionTemplate;
	}

	public void setDescriptionTemplate(String descriptionTemplate) {
		this.descriptionTemplate = descriptionTemplate;

		_descriptionTemplateSupplier = null;
	}

	@JsonIgnore
	public void setDescriptionTemplate(
		UnsafeSupplier<String, Exception> descriptionTemplateUnsafeSupplier) {

		_descriptionTemplateSupplier = () -> {
			try {
				return descriptionTemplateUnsafeSupplier.get();
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
		description = "The description template of the display page template to be used as summary for search engines."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String descriptionTemplate;

	@JsonIgnore
	private Supplier<String> _descriptionTemplateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The main title template of the display page template to be used by search engines."
	)
	public String getHtmlTitleTemplate() {
		if (_htmlTitleTemplateSupplier != null) {
			htmlTitleTemplate = _htmlTitleTemplateSupplier.get();

			_htmlTitleTemplateSupplier = null;
		}

		return htmlTitleTemplate;
	}

	public void setHtmlTitleTemplate(String htmlTitleTemplate) {
		this.htmlTitleTemplate = htmlTitleTemplate;

		_htmlTitleTemplateSupplier = null;
	}

	@JsonIgnore
	public void setHtmlTitleTemplate(
		UnsafeSupplier<String, Exception> htmlTitleTemplateUnsafeSupplier) {

		_htmlTitleTemplateSupplier = () -> {
			try {
				return htmlTitleTemplateUnsafeSupplier.get();
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
		description = "The main title template of the display page template to be used by search engines."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String htmlTitleTemplate;

	@JsonIgnore
	private Supplier<String> _htmlTitleTemplateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A localized tag telling search engines if and how they should crawl the display page template."
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
		description = "A localized tag telling search engines if and how they should crawl the display page template."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> robots_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _robots_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Represents settings related with the site map."
	)
	@Valid
	public SitemapSettings getSitemapSettings() {
		if (_sitemapSettingsSupplier != null) {
			sitemapSettings = _sitemapSettingsSupplier.get();

			_sitemapSettingsSupplier = null;
		}

		return sitemapSettings;
	}

	public void setSitemapSettings(SitemapSettings sitemapSettings) {
		this.sitemapSettings = sitemapSettings;

		_sitemapSettingsSupplier = null;
	}

	@JsonIgnore
	public void setSitemapSettings(
		UnsafeSupplier<SitemapSettings, Exception>
			sitemapSettingsUnsafeSupplier) {

		_sitemapSettingsSupplier = () -> {
			try {
				return sitemapSettingsUnsafeSupplier.get();
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
	protected SitemapSettings sitemapSettings;

	@JsonIgnore
	private Supplier<SitemapSettings> _sitemapSettingsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DisplayPageTemplateSEOSettings)) {
			return false;
		}

		DisplayPageTemplateSEOSettings displayPageTemplateSEOSettings =
			(DisplayPageTemplateSEOSettings)object;

		return Objects.equals(
			toString(), displayPageTemplateSEOSettings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String descriptionTemplate = getDescriptionTemplate();

		if (descriptionTemplate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"descriptionTemplate\": ");

			sb.append("\"");

			sb.append(_escape(descriptionTemplate));

			sb.append("\"");
		}

		String htmlTitleTemplate = getHtmlTitleTemplate();

		if (htmlTitleTemplate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"htmlTitleTemplate\": ");

			sb.append("\"");

			sb.append(_escape(htmlTitleTemplate));

			sb.append("\"");
		}

		Map<String, String> robots_i18n = getRobots_i18n();

		if (robots_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"robots_i18n\": ");

			sb.append(_toJSON(robots_i18n));
		}

		SitemapSettings sitemapSettings = getSitemapSettings();

		if (sitemapSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sitemapSettings\": ");

			sb.append(String.valueOf(sitemapSettings));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.DisplayPageTemplateSEOSettings",
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

	private static String _toJSON(Object value) {
		if (value instanceof Collection) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray((Collection<?>)value));
		}
		else if (value instanceof Map) {
			return String.valueOf(
				JSONFactoryUtil.createJSONObject((Map<?, ?>)value));
		}
		else if (value instanceof Object[]) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray(
					Arrays.asList((Object[])value)));
		}
		else if (value instanceof String) {
			return StringBundler.concat("\"", _escape(value), "\"");
		}

		return String.valueOf(value);
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
// LIFERAY-REST-BUILDER-HASH:1610677855