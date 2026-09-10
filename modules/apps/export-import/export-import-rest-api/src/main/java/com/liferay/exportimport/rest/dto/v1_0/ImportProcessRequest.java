/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
@GraphQLName("ImportProcessRequest")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ImportProcessRequest")
public class ImportProcessRequest implements Serializable {

	public static ImportProcessRequest toDTO(String json) {
		return ObjectMapperUtil.readValue(ImportProcessRequest.class, json);
	}

	public static ImportProcessRequest unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			ImportProcessRequest.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getComments() {
		if (_commentsSupplier != null) {
			comments = _commentsSupplier.get();

			_commentsSupplier = null;
		}

		return comments;
	}

	public void setComments(Boolean comments) {
		this.comments = comments;

		_commentsSupplier = null;
	}

	@JsonIgnore
	public void setComments(
		UnsafeSupplier<Boolean, Exception> commentsUnsafeSupplier) {

		_commentsSupplier = () -> {
			try {
				return commentsUnsafeSupplier.get();
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
	protected Boolean comments;

	@JsonIgnore
	private Supplier<Boolean> _commentsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("dataStrategy")
	@Valid
	public DataStrategy getDataStrategy() {
		if (_dataStrategySupplier != null) {
			dataStrategy = _dataStrategySupplier.get();

			_dataStrategySupplier = null;
		}

		return dataStrategy;
	}

	@JsonIgnore
	public String getDataStrategyAsString() {
		DataStrategy dataStrategy = getDataStrategy();

		if (dataStrategy == null) {
			return null;
		}

		return dataStrategy.toString();
	}

	public void setDataStrategy(DataStrategy dataStrategy) {
		this.dataStrategy = dataStrategy;

		_dataStrategySupplier = null;
	}

	@JsonIgnore
	public void setDataStrategy(
		UnsafeSupplier<DataStrategy, Exception> dataStrategyUnsafeSupplier) {

		_dataStrategySupplier = () -> {
			try {
				return dataStrategyUnsafeSupplier.get();
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
	protected DataStrategy dataStrategy;

	@JsonIgnore
	private Supplier<DataStrategy> _dataStrategySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getDeletions() {
		if (_deletionsSupplier != null) {
			deletions = _deletionsSupplier.get();

			_deletionsSupplier = null;
		}

		return deletions;
	}

	public void setDeletions(Boolean deletions) {
		this.deletions = deletions;

		_deletionsSupplier = null;
	}

	@JsonIgnore
	public void setDeletions(
		UnsafeSupplier<Boolean, Exception> deletionsUnsafeSupplier) {

		_deletionsSupplier = () -> {
			try {
				return deletionsUnsafeSupplier.get();
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
	protected Boolean deletions;

	@JsonIgnore
	private Supplier<Boolean> _deletionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getLogo() {
		if (_logoSupplier != null) {
			logo = _logoSupplier.get();

			_logoSupplier = null;
		}

		return logo;
	}

	public void setLogo(Boolean logo) {
		this.logo = logo;

		_logoSupplier = null;
	}

	@JsonIgnore
	public void setLogo(UnsafeSupplier<Boolean, Exception> logoUnsafeSupplier) {
		_logoSupplier = () -> {
			try {
				return logoUnsafeSupplier.get();
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
	protected Boolean logo;

	@JsonIgnore
	private Supplier<Boolean> _logoSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(String name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		_nameSupplier = () -> {
			try {
				return nameUnsafeSupplier.get();
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
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getPermissions() {
		if (_permissionsSupplier != null) {
			permissions = _permissionsSupplier.get();

			_permissionsSupplier = null;
		}

		return permissions;
	}

	public void setPermissions(Boolean permissions) {
		this.permissions = permissions;

		_permissionsSupplier = null;
	}

	@JsonIgnore
	public void setPermissions(
		UnsafeSupplier<Boolean, Exception> permissionsUnsafeSupplier) {

		_permissionsSupplier = () -> {
			try {
				return permissionsUnsafeSupplier.get();
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
	protected Boolean permissions;

	@JsonIgnore
	private Supplier<Boolean> _permissionsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getRatings() {
		if (_ratingsSupplier != null) {
			ratings = _ratingsSupplier.get();

			_ratingsSupplier = null;
		}

		return ratings;
	}

	public void setRatings(Boolean ratings) {
		this.ratings = ratings;

		_ratingsSupplier = null;
	}

	@JsonIgnore
	public void setRatings(
		UnsafeSupplier<Boolean, Exception> ratingsUnsafeSupplier) {

		_ratingsSupplier = () -> {
			try {
				return ratingsUnsafeSupplier.get();
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
	protected Boolean ratings;

	@JsonIgnore
	private Supplier<Boolean> _ratingsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public RequestPortletDataHandler[] getRequestPortletDataHandlers() {
		if (_requestPortletDataHandlersSupplier != null) {
			requestPortletDataHandlers =
				_requestPortletDataHandlersSupplier.get();

			_requestPortletDataHandlersSupplier = null;
		}

		return requestPortletDataHandlers;
	}

	public void setRequestPortletDataHandlers(
		RequestPortletDataHandler[] requestPortletDataHandlers) {

		this.requestPortletDataHandlers = requestPortletDataHandlers;

		_requestPortletDataHandlersSupplier = null;
	}

	@JsonIgnore
	public void setRequestPortletDataHandlers(
		UnsafeSupplier<RequestPortletDataHandler[], Exception>
			requestPortletDataHandlersUnsafeSupplier) {

		_requestPortletDataHandlersSupplier = () -> {
			try {
				return requestPortletDataHandlersUnsafeSupplier.get();
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
	protected RequestPortletDataHandler[] requestPortletDataHandlers;

	@JsonIgnore
	private Supplier<RequestPortletDataHandler[]>
		_requestPortletDataHandlersSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getSitePagesSettings() {
		if (_sitePagesSettingsSupplier != null) {
			sitePagesSettings = _sitePagesSettingsSupplier.get();

			_sitePagesSettingsSupplier = null;
		}

		return sitePagesSettings;
	}

	public void setSitePagesSettings(Boolean sitePagesSettings) {
		this.sitePagesSettings = sitePagesSettings;

		_sitePagesSettingsSupplier = null;
	}

	@JsonIgnore
	public void setSitePagesSettings(
		UnsafeSupplier<Boolean, Exception> sitePagesSettingsUnsafeSupplier) {

		_sitePagesSettingsSupplier = () -> {
			try {
				return sitePagesSettingsUnsafeSupplier.get();
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
	protected Boolean sitePagesSettings;

	@JsonIgnore
	private Supplier<Boolean> _sitePagesSettingsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getSiteTemplateSettings() {
		if (_siteTemplateSettingsSupplier != null) {
			siteTemplateSettings = _siteTemplateSettingsSupplier.get();

			_siteTemplateSettingsSupplier = null;
		}

		return siteTemplateSettings;
	}

	public void setSiteTemplateSettings(Boolean siteTemplateSettings) {
		this.siteTemplateSettings = siteTemplateSettings;

		_siteTemplateSettingsSupplier = null;
	}

	@JsonIgnore
	public void setSiteTemplateSettings(
		UnsafeSupplier<Boolean, Exception> siteTemplateSettingsUnsafeSupplier) {

		_siteTemplateSettingsSupplier = () -> {
			try {
				return siteTemplateSettingsUnsafeSupplier.get();
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
	protected Boolean siteTemplateSettings;

	@JsonIgnore
	private Supplier<Boolean> _siteTemplateSettingsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getThemeSettings() {
		if (_themeSettingsSupplier != null) {
			themeSettings = _themeSettingsSupplier.get();

			_themeSettingsSupplier = null;
		}

		return themeSettings;
	}

	public void setThemeSettings(Boolean themeSettings) {
		this.themeSettings = themeSettings;

		_themeSettingsSupplier = null;
	}

	@JsonIgnore
	public void setThemeSettings(
		UnsafeSupplier<Boolean, Exception> themeSettingsUnsafeSupplier) {

		_themeSettingsSupplier = () -> {
			try {
				return themeSettingsUnsafeSupplier.get();
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
	protected Boolean themeSettings;

	@JsonIgnore
	private Supplier<Boolean> _themeSettingsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("userIdStrategy")
	@Valid
	public UserIdStrategy getUserIdStrategy() {
		if (_userIdStrategySupplier != null) {
			userIdStrategy = _userIdStrategySupplier.get();

			_userIdStrategySupplier = null;
		}

		return userIdStrategy;
	}

	@JsonIgnore
	public String getUserIdStrategyAsString() {
		UserIdStrategy userIdStrategy = getUserIdStrategy();

		if (userIdStrategy == null) {
			return null;
		}

		return userIdStrategy.toString();
	}

	public void setUserIdStrategy(UserIdStrategy userIdStrategy) {
		this.userIdStrategy = userIdStrategy;

		_userIdStrategySupplier = null;
	}

	@JsonIgnore
	public void setUserIdStrategy(
		UnsafeSupplier<UserIdStrategy, Exception>
			userIdStrategyUnsafeSupplier) {

		_userIdStrategySupplier = () -> {
			try {
				return userIdStrategyUnsafeSupplier.get();
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
	protected UserIdStrategy userIdStrategy;

	@JsonIgnore
	private Supplier<UserIdStrategy> _userIdStrategySupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ImportProcessRequest)) {
			return false;
		}

		ImportProcessRequest importProcessRequest =
			(ImportProcessRequest)object;

		return Objects.equals(toString(), importProcessRequest.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean comments = getComments();

		if (comments != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"comments\": ");

			sb.append(comments);
		}

		DataStrategy dataStrategy = getDataStrategy();

		if (dataStrategy != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataStrategy\": ");

			sb.append("\"");
			sb.append(dataStrategy);
			sb.append("\"");
		}

		Boolean deletions = getDeletions();

		if (deletions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deletions\": ");

			sb.append(deletions);
		}

		Boolean logo = getLogo();

		if (logo != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"logo\": ");

			sb.append(logo);
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

		Boolean permissions = getPermissions();

		if (permissions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append(permissions);
		}

		Boolean ratings = getRatings();

		if (ratings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ratings\": ");

			sb.append(ratings);
		}

		RequestPortletDataHandler[] requestPortletDataHandlers =
			getRequestPortletDataHandlers();

		if (requestPortletDataHandlers != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"requestPortletDataHandlers\": ");

			sb.append("[");

			for (int i = 0; i < requestPortletDataHandlers.length; i++) {
				sb.append(String.valueOf(requestPortletDataHandlers[i]));

				if ((i + 1) < requestPortletDataHandlers.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Boolean sitePagesSettings = getSitePagesSettings();

		if (sitePagesSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sitePagesSettings\": ");

			sb.append(sitePagesSettings);
		}

		Boolean siteTemplateSettings = getSiteTemplateSettings();

		if (siteTemplateSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteTemplateSettings\": ");

			sb.append(siteTemplateSettings);
		}

		Boolean themeSettings = getThemeSettings();

		if (themeSettings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"themeSettings\": ");

			sb.append(themeSettings);
		}

		UserIdStrategy userIdStrategy = getUserIdStrategy();

		if (userIdStrategy != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userIdStrategy\": ");

			sb.append("\"");
			sb.append(userIdStrategy);
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.exportimport.rest.dto.v1_0.ImportProcessRequest",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("DataStrategy")
	public static enum DataStrategy {

		MIRROR("MIRROR"), MIRROR_OVERWRITE("MIRROR_OVERWRITE"),
		COPY_AS_NEW("COPY_AS_NEW");

		@JsonCreator
		public static DataStrategy create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (DataStrategy dataStrategy : values()) {
				if (Objects.equals(dataStrategy.getValue(), value)) {
					return dataStrategy;
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

		private DataStrategy(String value) {
			_value = value;
		}

		private final String _value;

	}

	@GraphQLName("UserIdStrategy")
	public static enum UserIdStrategy {

		CURRENT_USER_ID("CURRENT_USER_ID"),
		ALWAYS_CURRENT_USER_ID("ALWAYS_CURRENT_USER_ID");

		@JsonCreator
		public static UserIdStrategy create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (UserIdStrategy userIdStrategy : values()) {
				if (Objects.equals(userIdStrategy.getValue(), value)) {
					return userIdStrategy;
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

		private UserIdStrategy(String value) {
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
// LIFERAY-REST-BUILDER-HASH:1456459607