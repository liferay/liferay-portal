/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.dto.v1_0;

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
	description = "Represents settings related with the site navigation menu of a page.",
	value = "SitePageNavigationMenuSettings"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "SitePageNavigationMenuSettings")
public class SitePageNavigationMenuSettings implements Serializable {

	public static SitePageNavigationMenuSettings toDTO(String json) {
		return ObjectMapperUtil.readValue(
			SitePageNavigationMenuSettings.class, json);
	}

	public static SitePageNavigationMenuSettings unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			SitePageNavigationMenuSettings.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The default parameter for a page."
	)
	public String getQueryString() {
		if (_queryStringSupplier != null) {
			queryString = _queryStringSupplier.get();

			_queryStringSupplier = null;
		}

		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;

		_queryStringSupplier = null;
	}

	@JsonIgnore
	public void setQueryString(
		UnsafeSupplier<String, Exception> queryStringUnsafeSupplier) {

		_queryStringSupplier = () -> {
			try {
				return queryStringUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The default parameter for a page.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String queryString;

	@JsonIgnore
	private Supplier<String> _queryStringSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The page's description to be used as summary for search engines."
	)
	public String getTarget() {
		if (_targetSupplier != null) {
			target = _targetSupplier.get();

			_targetSupplier = null;
		}

		return target;
	}

	public void setTarget(String target) {
		this.target = target;

		_targetSupplier = null;
	}

	@JsonIgnore
	public void setTarget(
		UnsafeSupplier<String, Exception> targetUnsafeSupplier) {

		_targetSupplier = () -> {
			try {
				return targetUnsafeSupplier.get();
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
		description = "The page's description to be used as summary for search engines."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String target;

	@JsonIgnore
	private Supplier<String> _targetSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The target's type (specific frame or new tab)."
	)
	@JsonGetter("targetType")
	@Valid
	public TargetType getTargetType() {
		if (_targetTypeSupplier != null) {
			targetType = _targetTypeSupplier.get();

			_targetTypeSupplier = null;
		}

		return targetType;
	}

	@JsonIgnore
	public String getTargetTypeAsString() {
		TargetType targetType = getTargetType();

		if (targetType == null) {
			return null;
		}

		return targetType.toString();
	}

	public void setTargetType(TargetType targetType) {
		this.targetType = targetType;

		_targetTypeSupplier = null;
	}

	@JsonIgnore
	public void setTargetType(
		UnsafeSupplier<TargetType, Exception> targetTypeUnsafeSupplier) {

		_targetTypeSupplier = () -> {
			try {
				return targetTypeUnsafeSupplier.get();
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
		description = "The target's type (specific frame or new tab)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected TargetType targetType;

	@JsonIgnore
	private Supplier<TargetType> _targetTypeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SitePageNavigationMenuSettings)) {
			return false;
		}

		SitePageNavigationMenuSettings sitePageNavigationMenuSettings =
			(SitePageNavigationMenuSettings)object;

		return Objects.equals(
			toString(), sitePageNavigationMenuSettings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String queryString = getQueryString();

		if (queryString != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"queryString\": ");

			sb.append("\"");

			sb.append(_escape(queryString));

			sb.append("\"");
		}

		String target = getTarget();

		if (target != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"target\": ");

			sb.append("\"");

			sb.append(_escape(target));

			sb.append("\"");
		}

		TargetType targetType = getTargetType();

		if (targetType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"targetType\": ");

			sb.append("\"");

			sb.append(targetType);

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.SitePageNavigationMenuSettings",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("TargetType")
	public static enum TargetType {

		SPECIFIC_FRAME("SpecificFrame"), NEW_TAB("NewTab");

		@JsonCreator
		public static TargetType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (TargetType targetType : values()) {
				if (Objects.equals(targetType.getValue(), value)) {
					return targetType;
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

		private TargetType(String value) {
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