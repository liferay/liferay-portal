/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.dto.v1_0;

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

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
@GraphQLName("GeneralConfiguration")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "GeneralConfiguration")
public class GeneralConfiguration implements Serializable {

	public static GeneralConfiguration toDTO(String json) {
		return ObjectMapperUtil.readValue(GeneralConfiguration.class, json);
	}

	public static GeneralConfiguration unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			GeneralConfiguration.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getClauseContributorsExcludes() {
		if (_clauseContributorsExcludesSupplier != null) {
			clauseContributorsExcludes =
				_clauseContributorsExcludesSupplier.get();

			_clauseContributorsExcludesSupplier = null;
		}

		return clauseContributorsExcludes;
	}

	public void setClauseContributorsExcludes(
		String[] clauseContributorsExcludes) {

		this.clauseContributorsExcludes = clauseContributorsExcludes;

		_clauseContributorsExcludesSupplier = null;
	}

	@JsonIgnore
	public void setClauseContributorsExcludes(
		UnsafeSupplier<String[], Exception>
			clauseContributorsExcludesUnsafeSupplier) {

		_clauseContributorsExcludesSupplier = () -> {
			try {
				return clauseContributorsExcludesUnsafeSupplier.get();
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
	protected String[] clauseContributorsExcludes;

	@JsonIgnore
	private Supplier<String[]> _clauseContributorsExcludesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getClauseContributorsIncludes() {
		if (_clauseContributorsIncludesSupplier != null) {
			clauseContributorsIncludes =
				_clauseContributorsIncludesSupplier.get();

			_clauseContributorsIncludesSupplier = null;
		}

		return clauseContributorsIncludes;
	}

	public void setClauseContributorsIncludes(
		String[] clauseContributorsIncludes) {

		this.clauseContributorsIncludes = clauseContributorsIncludes;

		_clauseContributorsIncludesSupplier = null;
	}

	@JsonIgnore
	public void setClauseContributorsIncludes(
		UnsafeSupplier<String[], Exception>
			clauseContributorsIncludesUnsafeSupplier) {

		_clauseContributorsIncludesSupplier = () -> {
			try {
				return clauseContributorsIncludesUnsafeSupplier.get();
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
	protected String[] clauseContributorsIncludes;

	@JsonIgnore
	private Supplier<String[]> _clauseContributorsIncludesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getEmptySearchEnabled() {
		if (_emptySearchEnabledSupplier != null) {
			emptySearchEnabled = _emptySearchEnabledSupplier.get();

			_emptySearchEnabledSupplier = null;
		}

		return emptySearchEnabled;
	}

	public void setEmptySearchEnabled(Boolean emptySearchEnabled) {
		this.emptySearchEnabled = emptySearchEnabled;

		_emptySearchEnabledSupplier = null;
	}

	@JsonIgnore
	public void setEmptySearchEnabled(
		UnsafeSupplier<Boolean, Exception> emptySearchEnabledUnsafeSupplier) {

		_emptySearchEnabledSupplier = () -> {
			try {
				return emptySearchEnabledUnsafeSupplier.get();
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
	protected Boolean emptySearchEnabled;

	@JsonIgnore
	private Supplier<Boolean> _emptySearchEnabledSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getExplain() {
		if (_explainSupplier != null) {
			explain = _explainSupplier.get();

			_explainSupplier = null;
		}

		return explain;
	}

	public void setExplain(Boolean explain) {
		this.explain = explain;

		_explainSupplier = null;
	}

	@JsonIgnore
	public void setExplain(
		UnsafeSupplier<Boolean, Exception> explainUnsafeSupplier) {

		_explainSupplier = () -> {
			try {
				return explainUnsafeSupplier.get();
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
	protected Boolean explain;

	@JsonIgnore
	private Supplier<Boolean> _explainSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getIncludeResponseString() {
		if (_includeResponseStringSupplier != null) {
			includeResponseString = _includeResponseStringSupplier.get();

			_includeResponseStringSupplier = null;
		}

		return includeResponseString;
	}

	public void setIncludeResponseString(Boolean includeResponseString) {
		this.includeResponseString = includeResponseString;

		_includeResponseStringSupplier = null;
	}

	@JsonIgnore
	public void setIncludeResponseString(
		UnsafeSupplier<Boolean, Exception>
			includeResponseStringUnsafeSupplier) {

		_includeResponseStringSupplier = () -> {
			try {
				return includeResponseStringUnsafeSupplier.get();
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
	protected Boolean includeResponseString;

	@JsonIgnore
	private Supplier<Boolean> _includeResponseStringSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getLanguageId() {
		if (_languageIdSupplier != null) {
			languageId = _languageIdSupplier.get();

			_languageIdSupplier = null;
		}

		return languageId;
	}

	public void setLanguageId(String languageId) {
		this.languageId = languageId;

		_languageIdSupplier = null;
	}

	@JsonIgnore
	public void setLanguageId(
		UnsafeSupplier<String, Exception> languageIdUnsafeSupplier) {

		_languageIdSupplier = () -> {
			try {
				return languageIdUnsafeSupplier.get();
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
	protected String languageId;

	@JsonIgnore
	private Supplier<String> _languageIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String queryString;

	@JsonIgnore
	private Supplier<String> _queryStringSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getSearchableAssetTypes() {
		if (_searchableAssetTypesSupplier != null) {
			searchableAssetTypes = _searchableAssetTypesSupplier.get();

			_searchableAssetTypesSupplier = null;
		}

		return searchableAssetTypes;
	}

	public void setSearchableAssetTypes(String[] searchableAssetTypes) {
		this.searchableAssetTypes = searchableAssetTypes;

		_searchableAssetTypesSupplier = null;
	}

	@JsonIgnore
	public void setSearchableAssetTypes(
		UnsafeSupplier<String[], Exception>
			searchableAssetTypesUnsafeSupplier) {

		_searchableAssetTypesSupplier = () -> {
			try {
				return searchableAssetTypesUnsafeSupplier.get();
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
	protected String[] searchableAssetTypes;

	@JsonIgnore
	private Supplier<String[]> _searchableAssetTypesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getTimeZoneId() {
		if (_timeZoneIdSupplier != null) {
			timeZoneId = _timeZoneIdSupplier.get();

			_timeZoneIdSupplier = null;
		}

		return timeZoneId;
	}

	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;

		_timeZoneIdSupplier = null;
	}

	@JsonIgnore
	public void setTimeZoneId(
		UnsafeSupplier<String, Exception> timeZoneIdUnsafeSupplier) {

		_timeZoneIdSupplier = () -> {
			try {
				return timeZoneIdUnsafeSupplier.get();
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
	protected String timeZoneId;

	@JsonIgnore
	private Supplier<String> _timeZoneIdSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof GeneralConfiguration)) {
			return false;
		}

		GeneralConfiguration generalConfiguration =
			(GeneralConfiguration)object;

		return Objects.equals(toString(), generalConfiguration.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String[] clauseContributorsExcludes = getClauseContributorsExcludes();

		if (clauseContributorsExcludes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"clauseContributorsExcludes\": ");

			sb.append("[");

			for (int i = 0; i < clauseContributorsExcludes.length; i++) {
				sb.append("\"");

				sb.append(_escape(clauseContributorsExcludes[i]));

				sb.append("\"");

				if ((i + 1) < clauseContributorsExcludes.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String[] clauseContributorsIncludes = getClauseContributorsIncludes();

		if (clauseContributorsIncludes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"clauseContributorsIncludes\": ");

			sb.append("[");

			for (int i = 0; i < clauseContributorsIncludes.length; i++) {
				sb.append("\"");

				sb.append(_escape(clauseContributorsIncludes[i]));

				sb.append("\"");

				if ((i + 1) < clauseContributorsIncludes.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Boolean emptySearchEnabled = getEmptySearchEnabled();

		if (emptySearchEnabled != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emptySearchEnabled\": ");

			sb.append(emptySearchEnabled);
		}

		Boolean explain = getExplain();

		if (explain != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"explain\": ");

			sb.append(explain);
		}

		Boolean includeResponseString = getIncludeResponseString();

		if (includeResponseString != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"includeResponseString\": ");

			sb.append(includeResponseString);
		}

		String languageId = getLanguageId();

		if (languageId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"languageId\": ");

			sb.append("\"");

			sb.append(_escape(languageId));

			sb.append("\"");
		}

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

		String[] searchableAssetTypes = getSearchableAssetTypes();

		if (searchableAssetTypes != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"searchableAssetTypes\": ");

			sb.append("[");

			for (int i = 0; i < searchableAssetTypes.length; i++) {
				sb.append("\"");

				sb.append(_escape(searchableAssetTypes[i]));

				sb.append("\"");

				if ((i + 1) < searchableAssetTypes.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String timeZoneId = getTimeZoneId();

		if (timeZoneId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"timeZoneId\": ");

			sb.append("\"");

			sb.append(_escape(timeZoneId));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.search.experiences.rest.dto.v1_0.GeneralConfiguration",
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