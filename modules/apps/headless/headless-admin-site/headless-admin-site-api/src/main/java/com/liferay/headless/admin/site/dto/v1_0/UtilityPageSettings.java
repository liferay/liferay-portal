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
@GraphQLName(
	description = "The settings of an utility page.",
	value = "UtilityPageSettings"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "UtilityPageSettings")
public class UtilityPageSettings implements Serializable {

	public static UtilityPageSettings toDTO(String json) {
		return ObjectMapperUtil.readValue(UtilityPageSettings.class, json);
	}

	public static UtilityPageSettings unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			UtilityPageSettings.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The utility page's SEO settings."
	)
	@Valid
	public UtilityPageSEOSettings getSeoSettings() {
		if (_seoSettingsSupplier != null) {
			seoSettings = _seoSettingsSupplier.get();

			_seoSettingsSupplier = null;
		}

		return seoSettings;
	}

	public void setSeoSettings(UtilityPageSEOSettings seoSettings) {
		this.seoSettings = seoSettings;

		_seoSettingsSupplier = null;
	}

	@JsonIgnore
	public void setSeoSettings(
		UnsafeSupplier<UtilityPageSEOSettings, Exception>
			seoSettingsUnsafeSupplier) {

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

	@GraphQLField(description = "The utility page's SEO settings.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected UtilityPageSEOSettings seoSettings;

	@JsonIgnore
	private Supplier<UtilityPageSEOSettings> _seoSettingsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof UtilityPageSettings)) {
			return false;
		}

		UtilityPageSettings utilityPageSettings = (UtilityPageSettings)object;

		return Objects.equals(toString(), utilityPageSettings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		UtilityPageSEOSettings seoSettings = getSeoSettings();

		if (seoSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"seoSettings\": ");

			sb.append(String.valueOf(seoSettings));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.UtilityPageSettings",
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