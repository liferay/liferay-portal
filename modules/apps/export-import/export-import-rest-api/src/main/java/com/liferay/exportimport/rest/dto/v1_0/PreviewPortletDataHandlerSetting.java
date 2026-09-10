/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.dto.v1_0;

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
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
@GraphQLName("PreviewPortletDataHandlerSetting")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "PreviewPortletDataHandlerSetting")
public class PreviewPortletDataHandlerSetting
	extends PreviewPortletDataHandlerControl implements Serializable {

	public static PreviewPortletDataHandlerSetting toDTO(String json) {
		return ObjectMapperUtil.readValue(
			PreviewPortletDataHandlerSetting.class, json);
	}

	public static PreviewPortletDataHandlerSetting unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			PreviewPortletDataHandlerSetting.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getDefaultState() {
		if (_defaultStateSupplier != null) {
			defaultState = _defaultStateSupplier.get();

			_defaultStateSupplier = null;
		}

		return defaultState;
	}

	public void setDefaultState(Boolean defaultState) {
		this.defaultState = defaultState;

		_defaultStateSupplier = null;
	}

	@JsonIgnore
	public void setDefaultState(
		UnsafeSupplier<Boolean, Exception> defaultStateUnsafeSupplier) {

		_defaultStateSupplier = () -> {
			try {
				return defaultStateUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Boolean defaultState;

	@JsonIgnore
	private Supplier<Boolean> _defaultStateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public PreviewPortletDataHandlerControl[]
		getPreviewPortletDataHandlerControls() {

		if (_previewPortletDataHandlerControlsSupplier != null) {
			previewPortletDataHandlerControls =
				_previewPortletDataHandlerControlsSupplier.get();

			_previewPortletDataHandlerControlsSupplier = null;
		}

		return previewPortletDataHandlerControls;
	}

	public void setPreviewPortletDataHandlerControls(
		PreviewPortletDataHandlerControl[] previewPortletDataHandlerControls) {

		this.previewPortletDataHandlerControls =
			previewPortletDataHandlerControls;

		_previewPortletDataHandlerControlsSupplier = null;
	}

	@JsonIgnore
	public void setPreviewPortletDataHandlerControls(
		UnsafeSupplier<PreviewPortletDataHandlerControl[], Exception>
			previewPortletDataHandlerControlsUnsafeSupplier) {

		_previewPortletDataHandlerControlsSupplier = () -> {
			try {
				return previewPortletDataHandlerControlsUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected PreviewPortletDataHandlerControl[]
		previewPortletDataHandlerControls;

	@JsonIgnore
	private Supplier<PreviewPortletDataHandlerControl[]>
		_previewPortletDataHandlerControlsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof PreviewPortletDataHandlerSetting)) {
			return false;
		}

		PreviewPortletDataHandlerSetting previewPortletDataHandlerSetting =
			(PreviewPortletDataHandlerSetting)object;

		return Objects.equals(
			toString(), previewPortletDataHandlerSetting.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean defaultState = getDefaultState();

		if (defaultState != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultState\": ");

			sb.append(defaultState);
		}

		PreviewPortletDataHandlerControl[] previewPortletDataHandlerControls =
			getPreviewPortletDataHandlerControls();

		if (previewPortletDataHandlerControls != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"previewPortletDataHandlerControls\": ");

			sb.append("[");

			for (int i = 0; i < previewPortletDataHandlerControls.length; i++) {
				sb.append(String.valueOf(previewPortletDataHandlerControls[i]));

				if ((i + 1) < previewPortletDataHandlerControls.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Boolean disabled = getDisabled();

		if (disabled != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"disabled\": ");

			sb.append(disabled);
		}

		String label = getLabel();

		if (label != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(label));

			sb.append("\"");
		}

		String name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(name));

			sb.append("\"");
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
		defaultValue = "com.liferay.exportimport.rest.dto.v1_0.PreviewPortletDataHandlerSetting",
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
// LIFERAY-REST-BUILDER-HASH:700537505