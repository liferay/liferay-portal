/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.dto.v2_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.Generated;

import javax.xml.bind.annotation.XmlRootElement;

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

	@Schema
	public Boolean getApplyIndexerClauses() {
		if (_applyIndexerClausesSupplier != null) {
			applyIndexerClauses = _applyIndexerClausesSupplier.get();

			_applyIndexerClausesSupplier = null;
		}

		return applyIndexerClauses;
	}

	public void setApplyIndexerClauses(Boolean applyIndexerClauses) {
		this.applyIndexerClauses = applyIndexerClauses;

		_applyIndexerClausesSupplier = null;
	}

	@JsonIgnore
	public void setApplyIndexerClauses(
		UnsafeSupplier<Boolean, Exception> applyIndexerClausesUnsafeSupplier) {

		_applyIndexerClausesSupplier = () -> {
			try {
				return applyIndexerClausesUnsafeSupplier.get();
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
	protected Boolean applyIndexerClauses;

	@JsonIgnore
	private Supplier<Boolean> _applyIndexerClausesSupplier;

	@Schema
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

	@Schema
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

	@Schema
	public Boolean getCollectionProvider() {
		if (_collectionProviderSupplier != null) {
			collectionProvider = _collectionProviderSupplier.get();

			_collectionProviderSupplier = null;
		}

		return collectionProvider;
	}

	public void setCollectionProvider(Boolean collectionProvider) {
		this.collectionProvider = collectionProvider;

		_collectionProviderSupplier = null;
	}

	@JsonIgnore
	public void setCollectionProvider(
		UnsafeSupplier<Boolean, Exception> collectionProviderUnsafeSupplier) {

		_collectionProviderSupplier = () -> {
			try {
				return collectionProviderUnsafeSupplier.get();
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
	protected Boolean collectionProvider;

	@JsonIgnore
	private Supplier<Boolean> _collectionProviderSupplier;

	@Schema
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

	@Schema
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

	@Schema
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

	@Schema
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

		Boolean applyIndexerClauses = getApplyIndexerClauses();

		if (applyIndexerClauses != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"applyIndexerClauses\": ");

			sb.append(applyIndexerClauses);
		}

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

		Boolean collectionProvider = getCollectionProvider();

		if (collectionProvider != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"collectionProvider\": ");

			sb.append(collectionProvider);
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

		sb.append("}");

		return sb.toString();
	}

	@Schema(
		accessMode = Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.search.experiences.rest.dto.v2_0.GeneralConfiguration",
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