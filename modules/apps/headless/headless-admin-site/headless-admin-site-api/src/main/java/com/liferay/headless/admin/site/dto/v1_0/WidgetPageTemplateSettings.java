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
	description = "The settings of a widget page template.",
	value = "WidgetPageTemplateSettings"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "WidgetPageTemplateSettings")
public class WidgetPageTemplateSettings
	extends PageTemplateSettings implements Serializable {

	public static WidgetPageTemplateSettings toDTO(String json) {
		return ObjectMapperUtil.readValue(
			WidgetPageTemplateSettings.class, json);
	}

	public static WidgetPageTemplateSettings unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			WidgetPageTemplateSettings.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The identifier of the layout template."
	)
	public String getLayoutTemplateId() {
		if (_layoutTemplateIdSupplier != null) {
			layoutTemplateId = _layoutTemplateIdSupplier.get();

			_layoutTemplateIdSupplier = null;
		}

		return layoutTemplateId;
	}

	public void setLayoutTemplateId(String layoutTemplateId) {
		this.layoutTemplateId = layoutTemplateId;

		_layoutTemplateIdSupplier = null;
	}

	@JsonIgnore
	public void setLayoutTemplateId(
		UnsafeSupplier<String, Exception> layoutTemplateIdUnsafeSupplier) {

		_layoutTemplateIdSupplier = () -> {
			try {
				return layoutTemplateIdUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The identifier of the layout template.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String layoutTemplateId;

	@JsonIgnore
	private Supplier<String> _layoutTemplateIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The widget page template's site navigation menu settings."
	)
	@Valid
	public NavigationMenuSettings getNavigationMenuSettings() {
		if (_navigationMenuSettingsSupplier != null) {
			navigationMenuSettings = _navigationMenuSettingsSupplier.get();

			_navigationMenuSettingsSupplier = null;
		}

		return navigationMenuSettings;
	}

	public void setNavigationMenuSettings(
		NavigationMenuSettings navigationMenuSettings) {

		this.navigationMenuSettings = navigationMenuSettings;

		_navigationMenuSettingsSupplier = null;
	}

	@JsonIgnore
	public void setNavigationMenuSettings(
		UnsafeSupplier<NavigationMenuSettings, Exception>
			navigationMenuSettingsUnsafeSupplier) {

		_navigationMenuSettingsSupplier = () -> {
			try {
				return navigationMenuSettingsUnsafeSupplier.get();
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
		description = "The widget page template's site navigation menu settings."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected NavigationMenuSettings navigationMenuSettings;

	@JsonIgnore
	private Supplier<NavigationMenuSettings> _navigationMenuSettingsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof WidgetPageTemplateSettings)) {
			return false;
		}

		WidgetPageTemplateSettings widgetPageTemplateSettings =
			(WidgetPageTemplateSettings)object;

		return Objects.equals(
			toString(), widgetPageTemplateSettings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String layoutTemplateId = getLayoutTemplateId();

		if (layoutTemplateId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"layoutTemplateId\": ");

			sb.append("\"");

			sb.append(_escape(layoutTemplateId));

			sb.append("\"");
		}

		NavigationMenuSettings navigationMenuSettings =
			getNavigationMenuSettings();

		if (navigationMenuSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"navigationMenuSettings\": ");

			sb.append(String.valueOf(navigationMenuSettings));
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
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.WidgetPageTemplateSettings",
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