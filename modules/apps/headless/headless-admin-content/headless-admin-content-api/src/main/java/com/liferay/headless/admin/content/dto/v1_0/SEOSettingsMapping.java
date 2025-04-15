/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.content.dto.v1_0;

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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Specific settings related to SEO",
	value = "SEOSettingsMapping"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "SEOSettingsMapping")
public class SEOSettingsMapping implements Serializable {

	public static SEOSettingsMapping toDTO(String json) {
		return ObjectMapperUtil.readValue(SEOSettingsMapping.class, json);
	}

	public static SEOSettingsMapping unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(SEOSettingsMapping.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Field of the content type that will be used as the description"
	)
	public String getDescriptionMappingFieldKey() {
		if (_descriptionMappingFieldKeySupplier != null) {
			descriptionMappingFieldKey =
				_descriptionMappingFieldKeySupplier.get();

			_descriptionMappingFieldKeySupplier = null;
		}

		return descriptionMappingFieldKey;
	}

	public void setDescriptionMappingFieldKey(
		String descriptionMappingFieldKey) {

		this.descriptionMappingFieldKey = descriptionMappingFieldKey;

		_descriptionMappingFieldKeySupplier = null;
	}

	@JsonIgnore
	public void setDescriptionMappingFieldKey(
		UnsafeSupplier<String, Exception>
			descriptionMappingFieldKeyUnsafeSupplier) {

		_descriptionMappingFieldKeySupplier = () -> {
			try {
				return descriptionMappingFieldKeyUnsafeSupplier.get();
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
		description = "Field of the content type that will be used as the description"
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String descriptionMappingFieldKey;

	@JsonIgnore
	private Supplier<String> _descriptionMappingFieldKeySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Field of the content type that will be used as the HTML title"
	)
	public String getHtmlTitleMappingFieldKey() {
		if (_htmlTitleMappingFieldKeySupplier != null) {
			htmlTitleMappingFieldKey = _htmlTitleMappingFieldKeySupplier.get();

			_htmlTitleMappingFieldKeySupplier = null;
		}

		return htmlTitleMappingFieldKey;
	}

	public void setHtmlTitleMappingFieldKey(String htmlTitleMappingFieldKey) {
		this.htmlTitleMappingFieldKey = htmlTitleMappingFieldKey;

		_htmlTitleMappingFieldKeySupplier = null;
	}

	@JsonIgnore
	public void setHtmlTitleMappingFieldKey(
		UnsafeSupplier<String, Exception>
			htmlTitleMappingFieldKeyUnsafeSupplier) {

		_htmlTitleMappingFieldKeySupplier = () -> {
			try {
				return htmlTitleMappingFieldKeyUnsafeSupplier.get();
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
		description = "Field of the content type that will be used as the HTML title"
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String htmlTitleMappingFieldKey;

	@JsonIgnore
	private Supplier<String> _htmlTitleMappingFieldKeySupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Robots of the page that renders the Display Page Template"
	)
	public String getRobots() {
		if (_robotsSupplier != null) {
			robots = _robotsSupplier.get();

			_robotsSupplier = null;
		}

		return robots;
	}

	public void setRobots(String robots) {
		this.robots = robots;

		_robotsSupplier = null;
	}

	@JsonIgnore
	public void setRobots(
		UnsafeSupplier<String, Exception> robotsUnsafeSupplier) {

		_robotsSupplier = () -> {
			try {
				return robotsUnsafeSupplier.get();
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
		description = "Robots of the page that renders the Display Page Template"
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String robots;

	@JsonIgnore
	private Supplier<String> _robotsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Internationalized field of the robots of the page that renders the Display Page Template"
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
		description = "Internationalized field of the robots of the page that renders the Display Page Template"
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> robots_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _robots_i18nSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SEOSettingsMapping)) {
			return false;
		}

		SEOSettingsMapping seoSettingsMapping = (SEOSettingsMapping)object;

		return Objects.equals(toString(), seoSettingsMapping.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String descriptionMappingFieldKey = getDescriptionMappingFieldKey();

		if (descriptionMappingFieldKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"descriptionMappingFieldKey\": ");

			sb.append("\"");

			sb.append(_escape(descriptionMappingFieldKey));

			sb.append("\"");
		}

		String htmlTitleMappingFieldKey = getHtmlTitleMappingFieldKey();

		if (htmlTitleMappingFieldKey != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"htmlTitleMappingFieldKey\": ");

			sb.append("\"");

			sb.append(_escape(htmlTitleMappingFieldKey));

			sb.append("\"");
		}

		String robots = getRobots();

		if (robots != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"robots\": ");

			sb.append("\"");

			sb.append(_escape(robots));

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

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.content.dto.v1_0.SEOSettingsMapping",
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