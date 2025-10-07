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
	description = "A widget instance's look and feel configuration.",
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
	public AdvancedStylingConfig getAdvancedStylingConfig() {
		if (_advancedStylingConfigSupplier != null) {
			advancedStylingConfig = _advancedStylingConfigSupplier.get();

			_advancedStylingConfigSupplier = null;
		}

		return advancedStylingConfig;
	}

	public void setAdvancedStylingConfig(
		AdvancedStylingConfig advancedStylingConfig) {

		this.advancedStylingConfig = advancedStylingConfig;

		_advancedStylingConfigSupplier = null;
	}

	@JsonIgnore
	public void setAdvancedStylingConfig(
		UnsafeSupplier<AdvancedStylingConfig, Exception>
			advancedStylingConfigUnsafeSupplier) {

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
	protected AdvancedStylingConfig advancedStylingConfig;

	@JsonIgnore
	private Supplier<AdvancedStylingConfig> _advancedStylingConfigSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public BackgroundStylesConfig getBackgroundStylesConfig() {
		if (_backgroundStylesConfigSupplier != null) {
			backgroundStylesConfig = _backgroundStylesConfigSupplier.get();

			_backgroundStylesConfigSupplier = null;
		}

		return backgroundStylesConfig;
	}

	public void setBackgroundStylesConfig(
		BackgroundStylesConfig backgroundStylesConfig) {

		this.backgroundStylesConfig = backgroundStylesConfig;

		_backgroundStylesConfigSupplier = null;
	}

	@JsonIgnore
	public void setBackgroundStylesConfig(
		UnsafeSupplier<BackgroundStylesConfig, Exception>
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
	protected BackgroundStylesConfig backgroundStylesConfig;

	@JsonIgnore
	private Supplier<BackgroundStylesConfig> _backgroundStylesConfigSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public BorderStylesConfig getBorderStylesConfig() {
		if (_borderStylesConfigSupplier != null) {
			borderStylesConfig = _borderStylesConfigSupplier.get();

			_borderStylesConfigSupplier = null;
		}

		return borderStylesConfig;
	}

	public void setBorderStylesConfig(BorderStylesConfig borderStylesConfig) {
		this.borderStylesConfig = borderStylesConfig;

		_borderStylesConfigSupplier = null;
	}

	@JsonIgnore
	public void setBorderStylesConfig(
		UnsafeSupplier<BorderStylesConfig, Exception>
			borderStylesConfigUnsafeSupplier) {

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
	protected BorderStylesConfig borderStylesConfig;

	@JsonIgnore
	private Supplier<BorderStylesConfig> _borderStylesConfigSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public GeneralConfig getGeneralConfig() {
		if (_generalConfigSupplier != null) {
			generalConfig = _generalConfigSupplier.get();

			_generalConfigSupplier = null;
		}

		return generalConfig;
	}

	public void setGeneralConfig(GeneralConfig generalConfig) {
		this.generalConfig = generalConfig;

		_generalConfigSupplier = null;
	}

	@JsonIgnore
	public void setGeneralConfig(
		UnsafeSupplier<GeneralConfig, Exception> generalConfigUnsafeSupplier) {

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
	protected GeneralConfig generalConfig;

	@JsonIgnore
	private Supplier<GeneralConfig> _generalConfigSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public MarginAndPaddingConfig getMarginAndPaddingConfig() {
		if (_marginAndPaddingConfigSupplier != null) {
			marginAndPaddingConfig = _marginAndPaddingConfigSupplier.get();

			_marginAndPaddingConfigSupplier = null;
		}

		return marginAndPaddingConfig;
	}

	public void setMarginAndPaddingConfig(
		MarginAndPaddingConfig marginAndPaddingConfig) {

		this.marginAndPaddingConfig = marginAndPaddingConfig;

		_marginAndPaddingConfigSupplier = null;
	}

	@JsonIgnore
	public void setMarginAndPaddingConfig(
		UnsafeSupplier<MarginAndPaddingConfig, Exception>
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
	protected MarginAndPaddingConfig marginAndPaddingConfig;

	@JsonIgnore
	private Supplier<MarginAndPaddingConfig> _marginAndPaddingConfigSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public TextStylesConfig getTextStylesConfig() {
		if (_textStylesConfigSupplier != null) {
			textStylesConfig = _textStylesConfigSupplier.get();

			_textStylesConfigSupplier = null;
		}

		return textStylesConfig;
	}

	public void setTextStylesConfig(TextStylesConfig textStylesConfig) {
		this.textStylesConfig = textStylesConfig;

		_textStylesConfigSupplier = null;
	}

	@JsonIgnore
	public void setTextStylesConfig(
		UnsafeSupplier<TextStylesConfig, Exception>
			textStylesConfigUnsafeSupplier) {

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
	protected TextStylesConfig textStylesConfig;

	@JsonIgnore
	private Supplier<TextStylesConfig> _textStylesConfigSupplier;

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

		AdvancedStylingConfig advancedStylingConfig =
			getAdvancedStylingConfig();

		if (advancedStylingConfig != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"advancedStylingConfig\": ");

			sb.append(String.valueOf(advancedStylingConfig));
		}

		BackgroundStylesConfig backgroundStylesConfig =
			getBackgroundStylesConfig();

		if (backgroundStylesConfig != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"backgroundStylesConfig\": ");

			sb.append(String.valueOf(backgroundStylesConfig));
		}

		BorderStylesConfig borderStylesConfig = getBorderStylesConfig();

		if (borderStylesConfig != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"borderStylesConfig\": ");

			sb.append(String.valueOf(borderStylesConfig));
		}

		GeneralConfig generalConfig = getGeneralConfig();

		if (generalConfig != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"generalConfig\": ");

			sb.append(String.valueOf(generalConfig));
		}

		MarginAndPaddingConfig marginAndPaddingConfig =
			getMarginAndPaddingConfig();

		if (marginAndPaddingConfig != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"marginAndPaddingConfig\": ");

			sb.append(String.valueOf(marginAndPaddingConfig));
		}

		TextStylesConfig textStylesConfig = getTextStylesConfig();

		if (textStylesConfig != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"textStylesConfig\": ");

			sb.append(String.valueOf(textStylesConfig));
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