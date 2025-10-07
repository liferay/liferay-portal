/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@GraphQLName("GeneralConfig")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "GeneralConfig")
public class GeneralConfig implements Serializable {

	public static GeneralConfig toDTO(String json) {
		return ObjectMapperUtil.readValue(GeneralConfig.class, json);
	}

	public static GeneralConfig unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(GeneralConfig.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("applicationDecorator")
	@Valid
	public ApplicationDecorator getApplicationDecorator() {
		if (_applicationDecoratorSupplier != null) {
			applicationDecorator = _applicationDecoratorSupplier.get();

			_applicationDecoratorSupplier = null;
		}

		return applicationDecorator;
	}

	@JsonIgnore
	public String getApplicationDecoratorAsString() {
		ApplicationDecorator applicationDecorator = getApplicationDecorator();

		if (applicationDecorator == null) {
			return null;
		}

		return applicationDecorator.toString();
	}

	public void setApplicationDecorator(
		ApplicationDecorator applicationDecorator) {

		this.applicationDecorator = applicationDecorator;

		_applicationDecoratorSupplier = null;
	}

	@JsonIgnore
	public void setApplicationDecorator(
		UnsafeSupplier<ApplicationDecorator, Exception>
			applicationDecoratorUnsafeSupplier) {

		_applicationDecoratorSupplier = () -> {
			try {
				return applicationDecoratorUnsafeSupplier.get();
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
	protected ApplicationDecorator applicationDecorator;

	@JsonIgnore
	private Supplier<ApplicationDecorator> _applicationDecoratorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The localized custom titles."
	)
	@Valid
	public Map<String, String> getCustomTitle_i18n() {
		if (_customTitle_i18nSupplier != null) {
			customTitle_i18n = _customTitle_i18nSupplier.get();

			_customTitle_i18nSupplier = null;
		}

		return customTitle_i18n;
	}

	public void setCustomTitle_i18n(Map<String, String> customTitle_i18n) {
		this.customTitle_i18n = customTitle_i18n;

		_customTitle_i18nSupplier = null;
	}

	@JsonIgnore
	public void setCustomTitle_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			customTitle_i18nUnsafeSupplier) {

		_customTitle_i18nSupplier = () -> {
			try {
				return customTitle_i18nUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The localized custom titles.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> customTitle_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _customTitle_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Whether to use a custom title."
	)
	public Boolean getUseCustomTitle() {
		if (_useCustomTitleSupplier != null) {
			useCustomTitle = _useCustomTitleSupplier.get();

			_useCustomTitleSupplier = null;
		}

		return useCustomTitle;
	}

	public void setUseCustomTitle(Boolean useCustomTitle) {
		this.useCustomTitle = useCustomTitle;

		_useCustomTitleSupplier = null;
	}

	@JsonIgnore
	public void setUseCustomTitle(
		UnsafeSupplier<Boolean, Exception> useCustomTitleUnsafeSupplier) {

		_useCustomTitleSupplier = () -> {
			try {
				return useCustomTitleUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Whether to use a custom title.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean useCustomTitle;

	@JsonIgnore
	private Supplier<Boolean> _useCustomTitleSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof GeneralConfig)) {
			return false;
		}

		GeneralConfig generalConfig = (GeneralConfig)object;

		return Objects.equals(toString(), generalConfig.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		ApplicationDecorator applicationDecorator = getApplicationDecorator();

		if (applicationDecorator != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"applicationDecorator\": ");

			sb.append("\"");

			sb.append(applicationDecorator);

			sb.append("\"");
		}

		Map<String, String> customTitle_i18n = getCustomTitle_i18n();

		if (customTitle_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customTitle_i18n\": ");

			sb.append(_toJSON(customTitle_i18n));
		}

		Boolean useCustomTitle = getUseCustomTitle();

		if (useCustomTitle != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"useCustomTitle\": ");

			sb.append(useCustomTitle);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.GeneralConfig",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("ApplicationDecorator")
	public static enum ApplicationDecorator {

		BAREBONE("Barebone"), BORDERLESS("Borderless"), DECORATE("Decorate");

		@JsonCreator
		public static ApplicationDecorator create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (ApplicationDecorator applicationDecorator : values()) {
				if (Objects.equals(applicationDecorator.getValue(), value)) {
					return applicationDecorator;
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

		private ApplicationDecorator(String value) {
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