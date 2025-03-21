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
	description = "Represent a definition of a Page drop zone.",
	value = "PageDropZoneDefinition"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PageDropZoneDefinition")
public class PageDropZoneDefinition implements Serializable {

	public static PageDropZoneDefinition toDTO(String json) {
		return ObjectMapperUtil.readValue(PageDropZoneDefinition.class, json);
	}

	public static PageDropZoneDefinition unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			PageDropZoneDefinition.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page drop zone's allowed or unallowed fragments."
	)
	@Valid
	public Object getFragmentSettings() {
		if (_fragmentSettingsSupplier != null) {
			fragmentSettings = _fragmentSettingsSupplier.get();

			_fragmentSettingsSupplier = null;
		}

		return fragmentSettings;
	}

	public void setFragmentSettings(Object fragmentSettings) {
		this.fragmentSettings = fragmentSettings;

		_fragmentSettingsSupplier = null;
	}

	@JsonIgnore
	public void setFragmentSettings(
		UnsafeSupplier<Object, Exception> fragmentSettingsUnsafeSupplier) {

		_fragmentSettingsSupplier = () -> {
			try {
				return fragmentSettingsUnsafeSupplier.get();
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
		description = "The page drop zone's allowed or unallowed fragments."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Object fragmentSettings;

	@JsonIgnore
	private Supplier<Object> _fragmentSettingsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PageDropZoneDefinition)) {
			return false;
		}

		PageDropZoneDefinition pageDropZoneDefinition =
			(PageDropZoneDefinition)object;

		return Objects.equals(toString(), pageDropZoneDefinition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Object fragmentSettings = getFragmentSettings();

		if (fragmentSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentSettings\": ");

			if (fragmentSettings instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject(
						(Map<?, ?>)fragmentSettings));
			}
			else if (fragmentSettings instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)fragmentSettings));
				sb.append("\"");
			}
			else {
				sb.append(fragmentSettings);
			}
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.PageDropZoneDefinition",
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