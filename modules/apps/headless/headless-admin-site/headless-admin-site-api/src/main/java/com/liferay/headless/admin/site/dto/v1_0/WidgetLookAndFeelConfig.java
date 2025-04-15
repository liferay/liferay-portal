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
import com.liferay.portal.kernel.json.JSONFactoryUtil;
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
@GraphQLName(
	description = "The widget instance's look and feel configuration.",
	value = "WidgetLookAndFeelConfig"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "WidgetLookAndFeelConfig")
public class WidgetLookAndFeelConfig implements Serializable {

	public static WidgetLookAndFeelConfig toDTO(String json) {
		return ObjectMapperUtil.readValue(WidgetLookAndFeelConfig.class, json);
	}

	public static WidgetLookAndFeelConfig unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			WidgetLookAndFeelConfig.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getAdvancedStylingConfig() {
		if (_advancedStylingConfigSupplier != null) {
			advancedStylingConfig = _advancedStylingConfigSupplier.get();

			_advancedStylingConfigSupplier = null;
		}

		return advancedStylingConfig;
	}

	public void setAdvancedStylingConfig(Object advancedStylingConfig) {
		this.advancedStylingConfig = advancedStylingConfig;

		_advancedStylingConfigSupplier = null;
	}

	@JsonIgnore
	public void setAdvancedStylingConfig(
		UnsafeSupplier<Object, Exception> advancedStylingConfigUnsafeSupplier) {

		_advancedStylingConfigSupplier = () -> {
			try {
				return advancedStylingConfigUnsafeSupplier.get();
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
	protected Object advancedStylingConfig;

	@JsonIgnore
	private Supplier<Object> _advancedStylingConfigSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getBackgroundStylesConfig() {
		if (_backgroundStylesConfigSupplier != null) {
			backgroundStylesConfig = _backgroundStylesConfigSupplier.get();

			_backgroundStylesConfigSupplier = null;
		}

		return backgroundStylesConfig;
	}

	public void setBackgroundStylesConfig(Object backgroundStylesConfig) {
		this.backgroundStylesConfig = backgroundStylesConfig;

		_backgroundStylesConfigSupplier = null;
	}

	@JsonIgnore
	public void setBackgroundStylesConfig(
		UnsafeSupplier<Object, Exception>
			backgroundStylesConfigUnsafeSupplier) {

		_backgroundStylesConfigSupplier = () -> {
			try {
				return backgroundStylesConfigUnsafeSupplier.get();
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
	protected Object backgroundStylesConfig;

	@JsonIgnore
	private Supplier<Object> _backgroundStylesConfigSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getBorderStylesConfig() {
		if (_borderStylesConfigSupplier != null) {
			borderStylesConfig = _borderStylesConfigSupplier.get();

			_borderStylesConfigSupplier = null;
		}

		return borderStylesConfig;
	}

	public void setBorderStylesConfig(Object borderStylesConfig) {
		this.borderStylesConfig = borderStylesConfig;

		_borderStylesConfigSupplier = null;
	}

	@JsonIgnore
	public void setBorderStylesConfig(
		UnsafeSupplier<Object, Exception> borderStylesConfigUnsafeSupplier) {

		_borderStylesConfigSupplier = () -> {
			try {
				return borderStylesConfigUnsafeSupplier.get();
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
	protected Object borderStylesConfig;

	@JsonIgnore
	private Supplier<Object> _borderStylesConfigSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getGeneralConfig() {
		if (_generalConfigSupplier != null) {
			generalConfig = _generalConfigSupplier.get();

			_generalConfigSupplier = null;
		}

		return generalConfig;
	}

	public void setGeneralConfig(Object generalConfig) {
		this.generalConfig = generalConfig;

		_generalConfigSupplier = null;
	}

	@JsonIgnore
	public void setGeneralConfig(
		UnsafeSupplier<Object, Exception> generalConfigUnsafeSupplier) {

		_generalConfigSupplier = () -> {
			try {
				return generalConfigUnsafeSupplier.get();
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
	protected Object generalConfig;

	@JsonIgnore
	private Supplier<Object> _generalConfigSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getMarginAndPaddingConfig() {
		if (_marginAndPaddingConfigSupplier != null) {
			marginAndPaddingConfig = _marginAndPaddingConfigSupplier.get();

			_marginAndPaddingConfigSupplier = null;
		}

		return marginAndPaddingConfig;
	}

	public void setMarginAndPaddingConfig(Object marginAndPaddingConfig) {
		this.marginAndPaddingConfig = marginAndPaddingConfig;

		_marginAndPaddingConfigSupplier = null;
	}

	@JsonIgnore
	public void setMarginAndPaddingConfig(
		UnsafeSupplier<Object, Exception>
			marginAndPaddingConfigUnsafeSupplier) {

		_marginAndPaddingConfigSupplier = () -> {
			try {
				return marginAndPaddingConfigUnsafeSupplier.get();
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
	protected Object marginAndPaddingConfig;

	@JsonIgnore
	private Supplier<Object> _marginAndPaddingConfigSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getTextStylesConfig() {
		if (_textStylesConfigSupplier != null) {
			textStylesConfig = _textStylesConfigSupplier.get();

			_textStylesConfigSupplier = null;
		}

		return textStylesConfig;
	}

	public void setTextStylesConfig(Object textStylesConfig) {
		this.textStylesConfig = textStylesConfig;

		_textStylesConfigSupplier = null;
	}

	@JsonIgnore
	public void setTextStylesConfig(
		UnsafeSupplier<Object, Exception> textStylesConfigUnsafeSupplier) {

		_textStylesConfigSupplier = () -> {
			try {
				return textStylesConfigUnsafeSupplier.get();
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
	protected Object textStylesConfig;

	@JsonIgnore
	private Supplier<Object> _textStylesConfigSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WidgetLookAndFeelConfig)) {
			return false;
		}

		WidgetLookAndFeelConfig widgetLookAndFeelConfig =
			(WidgetLookAndFeelConfig)object;

		return Objects.equals(toString(), widgetLookAndFeelConfig.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Object advancedStylingConfig = getAdvancedStylingConfig();

		if (advancedStylingConfig != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"advancedStylingConfig\": ");

			if (advancedStylingConfig instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject(
						(Map<?, ?>)advancedStylingConfig));
			}
			else if (advancedStylingConfig instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)advancedStylingConfig));
				sb.append("\"");
			}
			else {
				sb.append(advancedStylingConfig);
			}
		}

		Object backgroundStylesConfig = getBackgroundStylesConfig();

		if (backgroundStylesConfig != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"backgroundStylesConfig\": ");

			if (backgroundStylesConfig instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject(
						(Map<?, ?>)backgroundStylesConfig));
			}
			else if (backgroundStylesConfig instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)backgroundStylesConfig));
				sb.append("\"");
			}
			else {
				sb.append(backgroundStylesConfig);
			}
		}

		Object borderStylesConfig = getBorderStylesConfig();

		if (borderStylesConfig != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"borderStylesConfig\": ");

			if (borderStylesConfig instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject(
						(Map<?, ?>)borderStylesConfig));
			}
			else if (borderStylesConfig instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)borderStylesConfig));
				sb.append("\"");
			}
			else {
				sb.append(borderStylesConfig);
			}
		}

		Object generalConfig = getGeneralConfig();

		if (generalConfig != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"generalConfig\": ");

			if (generalConfig instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject((Map<?, ?>)generalConfig));
			}
			else if (generalConfig instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)generalConfig));
				sb.append("\"");
			}
			else {
				sb.append(generalConfig);
			}
		}

		Object marginAndPaddingConfig = getMarginAndPaddingConfig();

		if (marginAndPaddingConfig != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"marginAndPaddingConfig\": ");

			if (marginAndPaddingConfig instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject(
						(Map<?, ?>)marginAndPaddingConfig));
			}
			else if (marginAndPaddingConfig instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)marginAndPaddingConfig));
				sb.append("\"");
			}
			else {
				sb.append(marginAndPaddingConfig);
			}
		}

		Object textStylesConfig = getTextStylesConfig();

		if (textStylesConfig != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"textStylesConfig\": ");

			if (textStylesConfig instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject(
						(Map<?, ?>)textStylesConfig));
			}
			else if (textStylesConfig instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)textStylesConfig));
				sb.append("\"");
			}
			else {
				sb.append(textStylesConfig);
			}
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.WidgetLookAndFeelConfig",
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