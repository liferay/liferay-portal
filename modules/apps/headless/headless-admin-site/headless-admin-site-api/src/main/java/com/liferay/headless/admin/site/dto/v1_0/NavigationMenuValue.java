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
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;

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
	description = "A value of a field of type navigation menu.",
	value = "NavigationMenuValue"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A value of a field of type navigation menu."
)
@JsonFilter("Liferay.Vulcan")
@JsonSubTypes(
	{
		@JsonSubTypes.Type(
			name = "ContextualMenu",
			value = ContextualMenuNavigationMenuValue.class
		),
		@JsonSubTypes.Type(
			name = "SiteMenu", value = SiteMenuNavigationMenuValue.class
		),
		@JsonSubTypes.Type(
			name = "SitePages", value = SitePagesNavigationMenuValue.class
		)
	}
)
@JsonTypeInfo(
	include = JsonTypeInfo.As.EXISTING_PROPERTY,
	property = "navigationMenuType", use = JsonTypeInfo.Id.NAME, visible = true
)
@XmlRootElement(name = "NavigationMenuValue")
public abstract class NavigationMenuValue implements Serializable {

	public static NavigationMenuValue toDTO(String json) {
		return ObjectMapperUtil.readValue(NavigationMenuValue.class, json);
	}

	public static NavigationMenuValue unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			NavigationMenuValue.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("navigationMenuType")
	@Valid
	public NavigationMenuType getNavigationMenuType() {
		if (_navigationMenuTypeSupplier != null) {
			navigationMenuType = _navigationMenuTypeSupplier.get();

			_navigationMenuTypeSupplier = null;
		}

		return navigationMenuType;
	}

	@JsonIgnore
	public String getNavigationMenuTypeAsString() {
		NavigationMenuType navigationMenuType = getNavigationMenuType();

		if (navigationMenuType == null) {
			return null;
		}

		return navigationMenuType.toString();
	}

	public void setNavigationMenuType(NavigationMenuType navigationMenuType) {
		this.navigationMenuType = navigationMenuType;

		_navigationMenuTypeSupplier = null;
	}

	@JsonIgnore
	public void setNavigationMenuType(
		UnsafeSupplier<NavigationMenuType, Exception>
			navigationMenuTypeUnsafeSupplier) {

		_navigationMenuTypeSupplier = () -> {
			try {
				return navigationMenuTypeUnsafeSupplier.get();
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
	protected NavigationMenuType navigationMenuType;

	@JsonIgnore
	private Supplier<NavigationMenuType> _navigationMenuTypeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof NavigationMenuValue)) {
			return false;
		}

		NavigationMenuValue navigationMenuValue = (NavigationMenuValue)object;

		return Objects.equals(toString(), navigationMenuValue.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		NavigationMenuType navigationMenuType = getNavigationMenuType();

		if (navigationMenuType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"navigationMenuType\": ");

			sb.append("\"");
			sb.append(navigationMenuType);
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.NavigationMenuValue",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("NavigationMenuType")
	public static enum NavigationMenuType {

		CONTEXTUAL_MENU("ContextualMenu"), SITE_MENU("SiteMenu"),
		SITE_PAGES("SitePages");

		@JsonCreator
		public static NavigationMenuType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (NavigationMenuType navigationMenuType : values()) {
				if (Objects.equals(navigationMenuType.getValue(), value)) {
					return navigationMenuType;
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

		private NavigationMenuType(String value) {
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
// LIFERAY-REST-BUILDER-HASH:1129482975