/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
 * @author Roberto Díaz
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Represents the settings of an asset library.",
	value = "Settings"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Settings")
public class Settings implements Serializable {

	public static Settings toDTO(String json) {
		return ObjectMapperUtil.readValue(Settings.class, json);
	}

	public static Settings unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Settings.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getAutoTaggingEnabled() {
		if (_autoTaggingEnabledSupplier != null) {
			autoTaggingEnabled = _autoTaggingEnabledSupplier.get();

			_autoTaggingEnabledSupplier = null;
		}

		return autoTaggingEnabled;
	}

	public void setAutoTaggingEnabled(Boolean autoTaggingEnabled) {
		this.autoTaggingEnabled = autoTaggingEnabled;

		_autoTaggingEnabledSupplier = null;
	}

	@JsonIgnore
	public void setAutoTaggingEnabled(
		UnsafeSupplier<Boolean, Exception> autoTaggingEnabledUnsafeSupplier) {

		_autoTaggingEnabledSupplier = () -> {
			try {
				return autoTaggingEnabledUnsafeSupplier.get();
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
	protected Boolean autoTaggingEnabled;

	@JsonIgnore
	private Supplier<Boolean> _autoTaggingEnabledSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getAvailableLanguageIds() {
		if (_availableLanguageIdsSupplier != null) {
			availableLanguageIds = _availableLanguageIdsSupplier.get();

			_availableLanguageIdsSupplier = null;
		}

		return availableLanguageIds;
	}

	public void setAvailableLanguageIds(String[] availableLanguageIds) {
		this.availableLanguageIds = availableLanguageIds;

		_availableLanguageIdsSupplier = null;
	}

	@JsonIgnore
	public void setAvailableLanguageIds(
		UnsafeSupplier<String[], Exception>
			availableLanguageIdsUnsafeSupplier) {

		_availableLanguageIdsSupplier = () -> {
			try {
				return availableLanguageIdsUnsafeSupplier.get();
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
	protected String[] availableLanguageIds;

	@JsonIgnore
	private Supplier<String[]> _availableLanguageIdsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getDefaultLanguageId() {
		if (_defaultLanguageIdSupplier != null) {
			defaultLanguageId = _defaultLanguageIdSupplier.get();

			_defaultLanguageIdSupplier = null;
		}

		return defaultLanguageId;
	}

	public void setDefaultLanguageId(String defaultLanguageId) {
		this.defaultLanguageId = defaultLanguageId;

		_defaultLanguageIdSupplier = null;
	}

	@JsonIgnore
	public void setDefaultLanguageId(
		UnsafeSupplier<String, Exception> defaultLanguageIdUnsafeSupplier) {

		_defaultLanguageIdSupplier = () -> {
			try {
				return defaultLanguageIdUnsafeSupplier.get();
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
	protected String defaultLanguageId;

	@JsonIgnore
	private Supplier<String> _defaultLanguageIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getLogoColor() {
		if (_logoColorSupplier != null) {
			logoColor = _logoColorSupplier.get();

			_logoColorSupplier = null;
		}

		return logoColor;
	}

	public void setLogoColor(String logoColor) {
		this.logoColor = logoColor;

		_logoColorSupplier = null;
	}

	@JsonIgnore
	public void setLogoColor(
		UnsafeSupplier<String, Exception> logoColorUnsafeSupplier) {

		_logoColorSupplier = () -> {
			try {
				return logoColorUnsafeSupplier.get();
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
	protected String logoColor;

	@JsonIgnore
	private Supplier<String> _logoColorSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public MimeTypeLimit[] getMimeTypeLimits() {
		if (_mimeTypeLimitsSupplier != null) {
			mimeTypeLimits = _mimeTypeLimitsSupplier.get();

			_mimeTypeLimitsSupplier = null;
		}

		return mimeTypeLimits;
	}

	public void setMimeTypeLimits(MimeTypeLimit[] mimeTypeLimits) {
		this.mimeTypeLimits = mimeTypeLimits;

		_mimeTypeLimitsSupplier = null;
	}

	@JsonIgnore
	public void setMimeTypeLimits(
		UnsafeSupplier<MimeTypeLimit[], Exception>
			mimeTypeLimitsUnsafeSupplier) {

		_mimeTypeLimitsSupplier = () -> {
			try {
				return mimeTypeLimitsUnsafeSupplier.get();
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
	protected MimeTypeLimit[] mimeTypeLimits;

	@JsonIgnore
	private Supplier<MimeTypeLimit[]> _mimeTypeLimitsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getSharingEnabled() {
		if (_sharingEnabledSupplier != null) {
			sharingEnabled = _sharingEnabledSupplier.get();

			_sharingEnabledSupplier = null;
		}

		return sharingEnabled;
	}

	public void setSharingEnabled(Boolean sharingEnabled) {
		this.sharingEnabled = sharingEnabled;

		_sharingEnabledSupplier = null;
	}

	@JsonIgnore
	public void setSharingEnabled(
		UnsafeSupplier<Boolean, Exception> sharingEnabledUnsafeSupplier) {

		_sharingEnabledSupplier = () -> {
			try {
				return sharingEnabledUnsafeSupplier.get();
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
	protected Boolean sharingEnabled;

	@JsonIgnore
	private Supplier<Boolean> _sharingEnabledSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getUseCustomLanguages() {
		if (_useCustomLanguagesSupplier != null) {
			useCustomLanguages = _useCustomLanguagesSupplier.get();

			_useCustomLanguagesSupplier = null;
		}

		return useCustomLanguages;
	}

	public void setUseCustomLanguages(Boolean useCustomLanguages) {
		this.useCustomLanguages = useCustomLanguages;

		_useCustomLanguagesSupplier = null;
	}

	@JsonIgnore
	public void setUseCustomLanguages(
		UnsafeSupplier<Boolean, Exception> useCustomLanguagesUnsafeSupplier) {

		_useCustomLanguagesSupplier = () -> {
			try {
				return useCustomLanguagesUnsafeSupplier.get();
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
	protected Boolean useCustomLanguages;

	@JsonIgnore
	private Supplier<Boolean> _useCustomLanguagesSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Settings)) {
			return false;
		}

		Settings settings = (Settings)object;

		return Objects.equals(toString(), settings.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean autoTaggingEnabled = getAutoTaggingEnabled();

		if (autoTaggingEnabled != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"autoTaggingEnabled\": ");

			sb.append(autoTaggingEnabled);
		}

		String[] availableLanguageIds = getAvailableLanguageIds();

		if (availableLanguageIds != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"availableLanguageIds\": ");

			sb.append("[");

			for (int i = 0; i < availableLanguageIds.length; i++) {
				sb.append("\"");

				sb.append(_escape(availableLanguageIds[i]));

				sb.append("\"");

				if ((i + 1) < availableLanguageIds.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String defaultLanguageId = getDefaultLanguageId();

		if (defaultLanguageId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultLanguageId\": ");

			sb.append("\"");

			sb.append(_escape(defaultLanguageId));

			sb.append("\"");
		}

		String logoColor = getLogoColor();

		if (logoColor != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"logoColor\": ");

			sb.append("\"");

			sb.append(_escape(logoColor));

			sb.append("\"");
		}

		MimeTypeLimit[] mimeTypeLimits = getMimeTypeLimits();

		if (mimeTypeLimits != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"mimeTypeLimits\": ");

			sb.append("[");

			for (int i = 0; i < mimeTypeLimits.length; i++) {
				sb.append(String.valueOf(mimeTypeLimits[i]));

				if ((i + 1) < mimeTypeLimits.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Boolean sharingEnabled = getSharingEnabled();

		if (sharingEnabled != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sharingEnabled\": ");

			sb.append(sharingEnabled);
		}

		Boolean useCustomLanguages = getUseCustomLanguages();

		if (useCustomLanguages != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"useCustomLanguages\": ");

			sb.append(useCustomLanguages);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.asset.library.dto.v1_0.Settings",
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