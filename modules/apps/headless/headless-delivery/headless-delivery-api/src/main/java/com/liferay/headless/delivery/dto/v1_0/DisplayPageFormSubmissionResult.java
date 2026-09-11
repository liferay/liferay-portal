/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
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

import java.util.Arrays;
import java.util.Collection;
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
	description = "Represents a definition of a submission result of type display page template.",
	value = "DisplayPageFormSubmissionResult"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Represents a definition of a submission result of type display page template."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "DisplayPageFormSubmissionResult")
public class DisplayPageFormSubmissionResult implements Serializable {

	public static DisplayPageFormSubmissionResult toDTO(String json) {
		return ObjectMapperUtil.readValue(
			DisplayPageFormSubmissionResult.class, json);
	}

	public static DisplayPageFormSubmissionResult unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			DisplayPageFormSubmissionResult.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Default display page when a form container submission is successful."
	)
	public Boolean getDefaultDisplayPage() {
		if (_defaultDisplayPageSupplier != null) {
			defaultDisplayPage = _defaultDisplayPageSupplier.get();

			_defaultDisplayPageSupplier = null;
		}

		return defaultDisplayPage;
	}

	public void setDefaultDisplayPage(Boolean defaultDisplayPage) {
		this.defaultDisplayPage = defaultDisplayPage;

		_defaultDisplayPageSupplier = null;
	}

	@JsonIgnore
	public void setDefaultDisplayPage(
		UnsafeSupplier<Boolean, Exception> defaultDisplayPageUnsafeSupplier) {

		_defaultDisplayPageSupplier = () -> {
			try {
				return defaultDisplayPageUnsafeSupplier.get();
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
		description = "Default display page when a form container submission is successful."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean defaultDisplayPage;

	@JsonIgnore
	private Supplier<Boolean> _defaultDisplayPageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The mapping of the display page template submission result."
	)
	@Valid
	public Mapping getMapping() {
		if (_mappingSupplier != null) {
			mapping = _mappingSupplier.get();

			_mappingSupplier = null;
		}

		return mapping;
	}

	public void setMapping(Mapping mapping) {
		this.mapping = mapping;

		_mappingSupplier = null;
	}

	@JsonIgnore
	public void setMapping(
		UnsafeSupplier<Mapping, Exception> mappingUnsafeSupplier) {

		_mappingSupplier = () -> {
			try {
				return mappingUnsafeSupplier.get();
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
		description = "The mapping of the display page template submission result."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Mapping mapping;

	@JsonIgnore
	private Supplier<Mapping> _mappingSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The localized display page template form submission result's notification text."
	)
	@Valid
	public FragmentInlineValue getNotificationTextFragmentInlineValue() {
		if (_notificationTextFragmentInlineValueSupplier != null) {
			notificationTextFragmentInlineValue =
				_notificationTextFragmentInlineValueSupplier.get();

			_notificationTextFragmentInlineValueSupplier = null;
		}

		return notificationTextFragmentInlineValue;
	}

	public void setNotificationTextFragmentInlineValue(
		FragmentInlineValue notificationTextFragmentInlineValue) {

		this.notificationTextFragmentInlineValue =
			notificationTextFragmentInlineValue;

		_notificationTextFragmentInlineValueSupplier = null;
	}

	@JsonIgnore
	public void setNotificationTextFragmentInlineValue(
		UnsafeSupplier<FragmentInlineValue, Exception>
			notificationTextFragmentInlineValueUnsafeSupplier) {

		_notificationTextFragmentInlineValueSupplier = () -> {
			try {
				return notificationTextFragmentInlineValueUnsafeSupplier.get();
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
		description = "The localized display page template form submission result's notification text."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected FragmentInlineValue notificationTextFragmentInlineValue;

	@JsonIgnore
	private Supplier<FragmentInlineValue>
		_notificationTextFragmentInlineValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getShowNotification() {
		if (_showNotificationSupplier != null) {
			showNotification = _showNotificationSupplier.get();

			_showNotificationSupplier = null;
		}

		return showNotification;
	}

	public void setShowNotification(Boolean showNotification) {
		this.showNotification = showNotification;

		_showNotificationSupplier = null;
	}

	@JsonIgnore
	public void setShowNotification(
		UnsafeSupplier<Boolean, Exception> showNotificationUnsafeSupplier) {

		_showNotificationSupplier = () -> {
			try {
				return showNotificationUnsafeSupplier.get();
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
	protected Boolean showNotification;

	@JsonIgnore
	private Supplier<Boolean> _showNotificationSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DisplayPageFormSubmissionResult)) {
			return false;
		}

		DisplayPageFormSubmissionResult displayPageFormSubmissionResult =
			(DisplayPageFormSubmissionResult)object;

		return Objects.equals(
			toString(), displayPageFormSubmissionResult.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean defaultDisplayPage = getDefaultDisplayPage();

		if (defaultDisplayPage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultDisplayPage\": ");

			sb.append(defaultDisplayPage);
		}

		Mapping mapping = getMapping();

		if (mapping != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"mapping\": ");

			sb.append(String.valueOf(mapping));
		}

		FragmentInlineValue notificationTextFragmentInlineValue =
			getNotificationTextFragmentInlineValue();

		if (notificationTextFragmentInlineValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"notificationTextFragmentInlineValue\": ");

			sb.append(String.valueOf(notificationTextFragmentInlineValue));
		}

		Boolean showNotification = getShowNotification();

		if (showNotification != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"showNotification\": ");

			sb.append(showNotification);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.DisplayPageFormSubmissionResult",
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

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
// LIFERAY-REST-BUILDER-HASH:-889748308