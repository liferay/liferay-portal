/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
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
	description = "Contains the configuration properties for Google Analytics (UA/GA3 and GA4).",
	value = "GoogleAnalyticsConfiguration"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Contains the configuration properties for Google Analytics (UA/GA3 and GA4)."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "GoogleAnalyticsConfiguration")
public class GoogleAnalyticsConfiguration implements Serializable {

	public static GoogleAnalyticsConfiguration toDTO(String json) {
		return ObjectMapperUtil.readValue(
			GoogleAnalyticsConfiguration.class, json);
	}

	public static GoogleAnalyticsConfiguration unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			GoogleAnalyticsConfiguration.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Set the Google Analytics 4 (GA4) custom options that are used for this set of pages. This is a JSON configuration for gtag()."
	)
	public String getGoogleAnalytics4CustomConfig() {
		if (_googleAnalytics4CustomConfigSupplier != null) {
			googleAnalytics4CustomConfig =
				_googleAnalytics4CustomConfigSupplier.get();

			_googleAnalytics4CustomConfigSupplier = null;
		}

		return googleAnalytics4CustomConfig;
	}

	public void setGoogleAnalytics4CustomConfig(
		String googleAnalytics4CustomConfig) {

		this.googleAnalytics4CustomConfig = googleAnalytics4CustomConfig;

		_googleAnalytics4CustomConfigSupplier = null;
	}

	@JsonIgnore
	public void setGoogleAnalytics4CustomConfig(
		UnsafeSupplier<String, Exception>
			googleAnalytics4CustomConfigUnsafeSupplier) {

		_googleAnalytics4CustomConfigSupplier = () -> {
			try {
				return googleAnalytics4CustomConfigUnsafeSupplier.get();
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
		description = "Set the Google Analytics 4 (GA4) custom options that are used for this set of pages. This is a JSON configuration for gtag()."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String googleAnalytics4CustomConfig;

	@JsonIgnore
	private Supplier<String> _googleAnalytics4CustomConfigSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Set the Google Analytics 4 ID (GA4) that is used for this set of pages."
	)
	public String getGoogleAnalytics4Id() {
		if (_googleAnalytics4IdSupplier != null) {
			googleAnalytics4Id = _googleAnalytics4IdSupplier.get();

			_googleAnalytics4IdSupplier = null;
		}

		return googleAnalytics4Id;
	}

	public void setGoogleAnalytics4Id(String googleAnalytics4Id) {
		this.googleAnalytics4Id = googleAnalytics4Id;

		_googleAnalytics4IdSupplier = null;
	}

	@JsonIgnore
	public void setGoogleAnalytics4Id(
		UnsafeSupplier<String, Exception> googleAnalytics4IdUnsafeSupplier) {

		_googleAnalytics4IdSupplier = () -> {
			try {
				return googleAnalytics4IdUnsafeSupplier.get();
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
		description = "Set the Google Analytics 4 ID (GA4) that is used for this set of pages."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String googleAnalytics4Id;

	@JsonIgnore
	private Supplier<String> _googleAnalytics4IdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Set the Google Analytics (UA/GA3) create method's custom options that are used for this set of pages."
	)
	public String getGoogleAnalyticsCreateCustomConfig() {
		if (_googleAnalyticsCreateCustomConfigSupplier != null) {
			googleAnalyticsCreateCustomConfig =
				_googleAnalyticsCreateCustomConfigSupplier.get();

			_googleAnalyticsCreateCustomConfigSupplier = null;
		}

		return googleAnalyticsCreateCustomConfig;
	}

	public void setGoogleAnalyticsCreateCustomConfig(
		String googleAnalyticsCreateCustomConfig) {

		this.googleAnalyticsCreateCustomConfig =
			googleAnalyticsCreateCustomConfig;

		_googleAnalyticsCreateCustomConfigSupplier = null;
	}

	@JsonIgnore
	public void setGoogleAnalyticsCreateCustomConfig(
		UnsafeSupplier<String, Exception>
			googleAnalyticsCreateCustomConfigUnsafeSupplier) {

		_googleAnalyticsCreateCustomConfigSupplier = () -> {
			try {
				return googleAnalyticsCreateCustomConfigUnsafeSupplier.get();
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
		description = "Set the Google Analytics (UA/GA3) create method's custom options that are used for this set of pages."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String googleAnalyticsCreateCustomConfig;

	@JsonIgnore
	private Supplier<String> _googleAnalyticsCreateCustomConfigSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Set the Google Analytics (UA/GA3) custom options that are used for this set of pages."
	)
	public String getGoogleAnalyticsCustomConfig() {
		if (_googleAnalyticsCustomConfigSupplier != null) {
			googleAnalyticsCustomConfig =
				_googleAnalyticsCustomConfigSupplier.get();

			_googleAnalyticsCustomConfigSupplier = null;
		}

		return googleAnalyticsCustomConfig;
	}

	public void setGoogleAnalyticsCustomConfig(
		String googleAnalyticsCustomConfig) {

		this.googleAnalyticsCustomConfig = googleAnalyticsCustomConfig;

		_googleAnalyticsCustomConfigSupplier = null;
	}

	@JsonIgnore
	public void setGoogleAnalyticsCustomConfig(
		UnsafeSupplier<String, Exception>
			googleAnalyticsCustomConfigUnsafeSupplier) {

		_googleAnalyticsCustomConfigSupplier = () -> {
			try {
				return googleAnalyticsCustomConfigUnsafeSupplier.get();
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
		description = "Set the Google Analytics (UA/GA3) custom options that are used for this set of pages."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String googleAnalyticsCustomConfig;

	@JsonIgnore
	private Supplier<String> _googleAnalyticsCustomConfigSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Set the Google Analytics ID (UA/GA3) that is used for this set of pages."
	)
	public String getGoogleAnalyticsId() {
		if (_googleAnalyticsIdSupplier != null) {
			googleAnalyticsId = _googleAnalyticsIdSupplier.get();

			_googleAnalyticsIdSupplier = null;
		}

		return googleAnalyticsId;
	}

	public void setGoogleAnalyticsId(String googleAnalyticsId) {
		this.googleAnalyticsId = googleAnalyticsId;

		_googleAnalyticsIdSupplier = null;
	}

	@JsonIgnore
	public void setGoogleAnalyticsId(
		UnsafeSupplier<String, Exception> googleAnalyticsIdUnsafeSupplier) {

		_googleAnalyticsIdSupplier = () -> {
			try {
				return googleAnalyticsIdUnsafeSupplier.get();
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
		description = "Set the Google Analytics ID (UA/GA3) that is used for this set of pages."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String googleAnalyticsId;

	@JsonIgnore
	private Supplier<String> _googleAnalyticsIdSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof GoogleAnalyticsConfiguration)) {
			return false;
		}

		GoogleAnalyticsConfiguration googleAnalyticsConfiguration =
			(GoogleAnalyticsConfiguration)object;

		return Objects.equals(
			toString(), googleAnalyticsConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String googleAnalytics4CustomConfig = getGoogleAnalytics4CustomConfig();

		if (googleAnalytics4CustomConfig != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"googleAnalytics4CustomConfig\": ");

			sb.append("\"");

			sb.append(_escape(googleAnalytics4CustomConfig));

			sb.append("\"");
		}

		String googleAnalytics4Id = getGoogleAnalytics4Id();

		if (googleAnalytics4Id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"googleAnalytics4Id\": ");

			sb.append("\"");

			sb.append(_escape(googleAnalytics4Id));

			sb.append("\"");
		}

		String googleAnalyticsCreateCustomConfig =
			getGoogleAnalyticsCreateCustomConfig();

		if (googleAnalyticsCreateCustomConfig != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"googleAnalyticsCreateCustomConfig\": ");

			sb.append("\"");

			sb.append(_escape(googleAnalyticsCreateCustomConfig));

			sb.append("\"");
		}

		String googleAnalyticsCustomConfig = getGoogleAnalyticsCustomConfig();

		if (googleAnalyticsCustomConfig != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"googleAnalyticsCustomConfig\": ");

			sb.append("\"");

			sb.append(_escape(googleAnalyticsCustomConfig));

			sb.append("\"");
		}

		String googleAnalyticsId = getGoogleAnalyticsId();

		if (googleAnalyticsId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"googleAnalyticsId\": ");

			sb.append("\"");

			sb.append(_escape(googleAnalyticsId));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.site.dto.v1_0.GoogleAnalyticsConfiguration",
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
// LIFERAY-REST-BUILDER-HASH:623263606